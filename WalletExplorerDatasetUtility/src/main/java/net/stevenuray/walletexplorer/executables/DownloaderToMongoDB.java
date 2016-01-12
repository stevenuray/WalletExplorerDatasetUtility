package net.stevenuray.walletexplorer.executables;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.stevenuray.walletexplorer.downloader.MongoDBInserter;
import net.stevenuray.walletexplorer.downloader.WalletExplorerAPIConfigSingleton;
import net.stevenuray.walletexplorer.mongodb.MongoDBClientSingleton;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionLatestTimeQuerier;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletAttribute;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletEOF;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletHeader;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTrailer;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransactions;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author richardc, modified by Steven Uray
 * @description Accepts input wallet names from a flat file resource file called
 *              wallets.txt and retrieves their transactions in batches of 100.
 *              This runs inside a single thread per user's request. Output is
 *              assigned to blocking queues and processed by multiple threads
 *              which update mongodb in an effort to speed up uploads times.
 *              Wallets are stored in their own collections, existing transactions 
 *              are ignored, only new transactions are stored on mongdb. The pom
 *              generates a uber jar so this program can be used at the command
 *              line using uber-WalletExplorer-1.0.jar, located in the target
 *              folder.
 * 
 */
public class DownloaderToMongoDB {
	private static final Logger LOG = getLog();		
	private static final int MAX_QUEUE_LENGTH = 1024;
	private static final int MAXIMUM_INSERTS = 100;
	private static final int EXTRACT_HIGH_WATER_MARK=100;
	private static final int DB_THREADS = 7;	
	private static final String credentials = System.getProperty("credentials");	
	private static Optional<String> credentialLocation = Optional
			.fromNullable(credentials);	
	private static final String wallets = System.getProperty("wallets");
	private static Optional<String> walletsLocation = Optional
			.fromNullable(wallets);
	
	public static void main(String[] args) {
		disableMongoLogInfo();
		DownloaderToMongoDB walletExplorerDownloader = new DownloaderToMongoDB();
		walletExplorerDownloader.downloadAndSaveAllWalletTransactions();
	}
	
	private static void disableMongoLogInfo(){
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}
	
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(Log4JTest.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Test Appender");			
		log.addAppender(appender);
		return log;
	}

	private final BlockingQueue<WalletAttribute>[] walletTransactionsQueue = new ArrayBlockingQueue[DB_THREADS];
		
	private int masterTotalInputTransactions = 0;

	private int masterTotalInsertedTransactions = 0;

	/*
	 * single thread json api extractor which instantiates multi thread db
	 * loaders...
	 */
	private void downloadAndSaveAllWalletTransactions() {
		LOG.info("*****BEGINNING DOWNLOAD OF TRANSACTIONS FROM WALLETEXPLORER.COM***** ");
		Thread[] dbLoader = new Thread[DB_THREADS];
		MongoDBInserter[] mongoDBLoad = new MongoDBInserter[DB_THREADS];
		// init db loaders before starting their threads in case of login
		// issues, etc.
		for (int i = 0; i < DB_THREADS; i++) {
			walletTransactionsQueue[i] = new ArrayBlockingQueue(
					MAX_QUEUE_LENGTH);

			try {
				String login = WalletExplorerAPIConfigSingleton.LOGIN;			
				mongoDBLoad[i] = new MongoDBInserter(LOG,"thread" + i,
						walletTransactionsQueue[i],MAXIMUM_INSERTS);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.fatal("Error Creating Insertion Thread: "+i);
				System.exit(1);
			}
		}
		// safe to start db loader threads...
		for (int i = 0; i < DB_THREADS; i++) {
			dbLoader[i] = new Thread(mongoDBLoad[i]);
			dbLoader[i].setPriority((Thread.NORM_PRIORITY + 1));
			dbLoader[i].start();
		}
		// start processing wallets specified inside resource/property flat
		// file.
		int queueId = 0;
		Iterator<String> walletNames = null;
		try {
			walletNames = getWalletNames();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		int walletsDownloaded = 0; 
		while (walletNames.hasNext()) {
			try {
				
				String nextWalletName = walletNames.next();
				DateTime walletEndTime = getLatestTransactionTime(nextWalletName);		
				LOG.info("Downloading Wallet Transactions After: "+walletEndTime+" From: "+nextWalletName);
				queueId = getNextQueueID(queueId);
				extractWalletTransactions(nextWalletName,
						queueId,
						walletEndTime);								
				walletsDownloaded++;
				LOG.info("Download Complete For: "+nextWalletName);
				LOG.info("Wallets Downloaded: "+walletsDownloaded);
				Thread.yield();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		// all wallets extracted and placed on loader queues, so send a poison
		// message to all threads
		// so they know the queues are depleted. Then wait for them to complete
		// processing their workload.
		try {
			for (int i = 0; i < DB_THREADS; i++) {
				walletTransactionsQueue[i].put(new WalletEOF());
			}
			for (int i = 0; i < DB_THREADS; i++) {
				dbLoader[i].join();
				masterTotalInputTransactions += mongoDBLoad[i].getSubTotalInputTransactions();
				masterTotalInsertedTransactions += mongoDBLoad[i].getSubTotalInsertedTransactions();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.info("Total Transactions Downloaded: " + masterTotalInputTransactions);
		LOG.info("Total Transactions Inserted: " + masterTotalInsertedTransactions);
		LOG.info("End Time: " + new Date());
	}

	/*
	 * extract all transactions for wallet and assign to specific queue which
	 * will be consumed by a dedicated db loader thread. db loader threads will
	 * consume transactions for multiple wallets.
	 */
	private void extractWalletTransactions(String walletName, int queueId,DateTime endTime)
			throws InterruptedException {		
		Client client = Client.create();
		//TODO GLOBAL CONFIG VAR THIS!
		WebResource webResource = client
				.resource("http://www.walletexplorer.com/api/1/wallet");

		int fromCount = 0;
		int toCount = EXTRACT_HIGH_WATER_MARK;
		String caller = WalletExplorerAPIConfigSingleton.CALLER;
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams = new MultivaluedMapImpl();
		queryParams.add("wallet", walletName);
		queryParams.add("from", fromCount);
		queryParams.add("count", toCount);
		queryParams.add("caller", caller);

		ClientResponse response = webResource.queryParams(queryParams)
				.type("application/json").get(ClientResponse.class);
		String jsonStr = response.getEntity(String.class);

		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonStr);

		if (!Boolean.valueOf(json.getString("found"))) {
			LOG.error("Wallet " + walletName + " not found");
			return;
		}
		int txsCount = json.getInt("txs_count");
		String wallet_id = json.getString("wallet_id");
		/*
		LOG.info("Started extract and load of " + walletName + " with "
				+ txsCount + " transactions to process");
				*/
		walletTransactionsQueue[queueId].put(new WalletHeader(walletName));		
		for (int i = 0, j = 0; i < txsCount; i += 100, j++) {
			fromCount = i;
			queryParams = new MultivaluedMapImpl();
			queryParams.add("wallet", walletName);
			queryParams.add("from", fromCount);
			queryParams.add("count", toCount);
			queryParams.add("caller", caller);
			
			//TODO global config var this or refactor
			int maxConnectionAttempts = 3;
			for(int m = 0; m < maxConnectionAttempts; m++){
				try{
					response = webResource.queryParams(queryParams)
							.type("application/json").get(ClientResponse.class);
					break;
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			jsonStr = response.getEntity(String.class);

			json = (JSONObject) JSONSerializer.toJSON(jsonStr);
						
			JSONArray subWalletTransactions = (JSONArray) json.get("txs");
			
			/*Note: This loop works in every case except one, if transactions for a wallet name
			 * are not fully downloaded on their initial run, they will never be downloaded!
			 * A quick solution to this is to delete the collection if there is an error and hope
			 * it downloads properly on the next run. 
			 */
			boolean breakAfterThisLoop = false;
			for(int k = 0; k < subWalletTransactions.size(); k++){
				/*Breaking early if we already have transactions earlier than the current transaction.
				 * Note: As of 2015-10-12 WalletExplorer API returns transactions in descending time only!
				 */
				JSONObject subWalletTransaction = (JSONObject) subWalletTransactions.get(k);
				long unixTimestamp = subWalletTransaction.getLong("time");
				DateTime currentTime = new DateTime(unixTimestamp*1000);
				if(currentTime.isBefore(endTime)){					
					breakAfterThisLoop = true;
				}
			}
			walletTransactionsQueue[queueId].put(new WalletTransactions(
					walletName, wallet_id, txsCount, subWalletTransactions));
			
			if(breakAfterThisLoop){
				break;
			}
			//if (j == 1) { // testing only
			//	break;
			//}
		}
		walletTransactionsQueue[queueId].put(new WalletTrailer(walletName));
		/*
		LOG.info("extractWalletTransactions ended for " + walletName + " with "
				+ txsCount + " transactions" + ", " + new Date());
		*/		
	}

	private WalletCollection getMongoCollection(String walletName) {
		MongoClient mongoClient = MongoDBClientSingleton.getInstance();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();			
		WalletCollection mongoCollection = 
				new WalletCollection(mongoClient,database,walletName);
		return mongoCollection;
	}

	private int getNextQueueID(int queueId) {
		switch (queueId) {
		case 0:
			return 1;
		case 1:
			return 2;
		default:
			return 0;
		}
	}

	private DateTime getLatestTransactionTime(String nextWalletName) {
		WalletCollection walletConnection = getMongoCollection(nextWalletName);
		WalletExplorerCollectionLatestTimeQuerier latestTimeQuerier =
				new WalletExplorerCollectionLatestTimeQuerier(walletConnection);
		DateTime latestTransactionTime = null;
		try {
			latestTransactionTime = latestTimeQuerier.call();
		} catch (Exception e) {			
			LOG.info("Could not find the latest transaction time for: "+nextWalletName+"."+
					" A complete download will be done to ensure dataset integrity");
			e.printStackTrace();
			return new DateTime(0);
		}
		return latestTransactionTime;
	}	
	
	private Iterator<String> getWalletNames() throws Exception {	
		Charset encoding = WalletExplorerAPIConfigSingleton.ENCODING;
		File file = new File("resources/wallets.txt");
		Path path = file.toPath();
		Iterator<String> wallets = Files.readAllLines(path, encoding).iterator();
		return wallets;
	}
}