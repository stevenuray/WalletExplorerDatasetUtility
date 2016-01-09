package net.stevenuray.walletexplorer.downloader;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.mongodb.MongoDBClientSingleton;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletAttribute;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletEOF;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletHeader;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTrailer;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransactions;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * 
 * @author richardc, modified by Steven Uray 
 * 
 * @Description - MongoDB loader. Each loader will consume from it's own
 *              dedicated queue and will take care of all transactions for
 *              assigned wallets, wallet transactions will not be split over
 *              threads.
 */
public class MongoDBInserter implements Runnable {
	private static final int REPORT_TRIGGER=1000;
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;	
	private String walletName;
	private int totalInputTransactions = 0;
	private int subTotalInputTransactions = 0;
	private int totalInsertedTransactions = 0;
	private int subTotalInsertedTransactions = 0;
	private int totalIgnoredTransactions = 0;
	private int subTotalIgnoredTransactions = 0;
	private int invalidTransactions = 0;
	private int reportCount=0;
	private final BlockingQueue<WalletAttribute> walletTransactions;
	private boolean run = true;
	private final String threadId;
	private final Logger log;
	private final int maximumInserts;

	public MongoDBInserter(Logger log,String threadId,BlockingQueue<WalletAttribute> walletTransactions, 
			int maximumInserts) {
		this.walletTransactions = walletTransactions;
		this.threadId = threadId;	
		this.log = log;
		this.maximumInserts = maximumInserts;
		mongoClient = MongoDBClientSingleton.getInstance();
		database = MongoDBConnectionService.getMongoDatabase();
		log.debug("DB Loader " + this.threadId + "  instantiated");
	}	
	
	public int getInvalidTransactions() {
		return invalidTransactions;
	}

	public int getSubTotalIgnoredTransactions() {
		return subTotalIgnoredTransactions;
	}

	public int getSubTotalInputTransactions() {
		return subTotalInputTransactions;
	}

	public int getSubTotalInsertedTransactions() {
		return subTotalInsertedTransactions;
	}

	public int getTotalIgnoredTransactions() {
		return totalIgnoredTransactions;
	}

	public int getTotalInputTransactions() {
		return totalInputTransactions;
	}

	public int getTotalInsertedTransactions() {
		return totalInsertedTransactions;
	}

	public void run() {
		log.debug("DB Loader " + this.threadId + " running");
		while (run) {
			try {
				addTransactions(walletTransactions.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

		log.debug("DB Loader " + this.threadId + " stopped");
	}

	private void addBatchedTransactions(List<JSONObject> subTransactions) {
		List<Document> documents = new ArrayList<Document>();
		Iterator<JSONObject> details = subTransactions.iterator();
		while (details.hasNext()) {
			JSONObject detail = details.next();
			subTotalInputTransactions++;
			totalInputTransactions++;
			if (isTransactionNew(detail)) {
				Document document = new Document();
				document.append("Transaction Date",
						detail.getLong("time") * 1000);
				document.append("txid", detail.getString("txid"));
				document.append("balance", detail.getDouble("balance"));
				document.append("type", detail.getString("type"));
				document.append("outputs",
						generateOutputs(walletName, detail));
				if (detail.has("fee")) {
					document.append("fee", detail.getDouble("fee"));
				}
				documents.add(document);
				subTotalInsertedTransactions++;
				totalInsertedTransactions++;
			}
			reportCount++;
			if(reportCount==REPORT_TRIGGER){
				//log.info(walletName +" Downloaded Transactions: "+subTotalInputTransactions);
				log.info(walletName+" Inserted Transactions: "+subTotalInsertedTransactions);
				reportCount=0;
			}
		}
		if (!documents.isEmpty()) {
			collection.insertMany(documents);
		}
		

	}

	@SuppressWarnings("unchecked")
	private void addTransactions(WalletAttribute walletAttribute) {

		if (walletAttribute instanceof WalletEOF) {
			log.info("DB Loader Thread: " + this.threadId + " completed!");
			log.info(this.threadId + " Grand Total Downloaded Wallet Transactions: "
					+ totalInputTransactions);
			log.info(this.threadId + " Grand Total Inserted Wallet Transactions: "
					+ totalInsertedTransactions);
			
			log.info(this.threadId
					+ " Grand Total Invalid Wallet Transactions: "
					+ invalidTransactions);
			run = false;
			return;
		}
		if (walletAttribute instanceof WalletHeader) {
			WalletHeader walletHeader = (WalletHeader) walletAttribute;
			walletName = walletHeader.getWalletName();
			log.info("Inserting Wallet Transactions From: "	
					+ walletHeader.getWalletName());
			subTotalInputTransactions = 0;
			subTotalInsertedTransactions = 0;
			reportCount=0;
			initMongoDbCollection();
			return;
		}
		if (walletAttribute instanceof WalletTrailer) {
			WalletTrailer walletTrailer = (WalletTrailer) walletAttribute;
			log.info("Total Wallet Transactions Downloaded for "
					+ walletTrailer.getWalletName() + " : " + subTotalInputTransactions);
			log.info("Total Wallet Transactions Inserted for "
					+ walletTrailer.getWalletName() + " : " + subTotalInsertedTransactions);			
			//display();
			walletName = null;
			return;
		}

		final Iterable<List<JSONObject>> subTransactions = Iterables
				.partition(((WalletTransactions) walletAttribute)
						.getTransactions(), maximumInserts);

		for (final List<JSONObject> subTransaction : subTransactions) {
			addBatchedTransactions(subTransaction);
		}

	}

	private List<Map<String, Object>> generateOutputs(String walletName,
			JSONObject detail) {
		boolean invalidFound = false;
		List<Map<String, Object>> outputs = new ArrayList<Map<String, Object>>();
		if (detail.has("outputs")) {
			@SuppressWarnings("rawtypes")
			Iterator allOutputs = detail.getJSONArray("outputs").iterator();
			while (allOutputs.hasNext()) {
				Map<String, Object> outputTransaction = new HashMap<String, Object>();
				JSONObject output = (JSONObject) allOutputs.next();
				outputTransaction.put("amount", output.getDouble("amount"));
				if (output.has("wallet_id")
						&& !StringUtils.isBlank(output
								.getString("wallet_id"))) {
					outputTransaction.put("wallet_id",
							output.getString("wallet_id"));
				} else {
					invalidFound = true;
				}

				if (output.has("updated_to_block")) {
					outputTransaction.put("updated_to_block",
							output.getString("updated_to_block"));
				}
				if (output.has("label")) {
					outputTransaction.put("label",
							output.getString("label"));
				}
				outputs.add(outputTransaction);
			}

		} else {
			Map<String, Object> outputTransaction = new HashMap<String, Object>();
			outputTransaction.put("amount", detail.getDouble("amount"));
			if (detail.has("wallet_id")
					&& !StringUtils.isBlank(detail.getString("wallet_id"))) {
				outputTransaction.put("wallet_id",
						detail.getString("wallet_id"));
			} else {
				invalidFound = true;
			}
			outputs.add(outputTransaction);
			if (invalidFound) {
				invalidTransactions++;
			}
		}
		return outputs;
	}

	private void initMongoDbCollection() {
		log.debug(this.threadId+" Initialising collection: "+walletName);
		collection = database.getCollection(walletName);
		collection.createIndex(new Document("Transaction Date", 1));
		collection.createIndex(new Document("txid", 1),new IndexOptions().unique(true));	
		log.debug(this.threadId+" Initialisation completed for collection: "+walletName);
	}

	private boolean isTransactionNew(JSONObject detail) {
		BasicDBObject txsSearch = new BasicDBObject();
		txsSearch
				.put("txid",
						new BasicDBObject().append("$eq",
								detail.getString("txid")));
		return !Optional.fromNullable(collection.find(txsSearch).first())
				.isPresent();
	}

}

