package net.stevenuray.walletexplorer.executables;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;

import net.stevenuray.walletexplorer.conversion.collection.CollectionConverter;
import net.stevenuray.walletexplorer.conversion.collection.ConversionResults;
import net.stevenuray.walletexplorer.conversion.collection.ConvertedCollectionProvider;
import net.stevenuray.walletexplorer.conversion.collection.ConvertedCollectionService;
import net.stevenuray.walletexplorer.conversion.currency.CoindeskBpiConverter;
import net.stevenuray.walletexplorer.conversion.currency.HistoricalBTCToUSDConverter;
import net.stevenuray.walletexplorer.conversion.currency.HistoricalWalletTransactionCurrencyConverter;
import net.stevenuray.walletexplorer.conversion.currency.WalletTransactionCurrencyConverter;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.mongodb.MongoDBConsumer;
import net.stevenuray.walletexplorer.mongodb.MongoDBProducer;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionLatestTimeQuerier;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.BasicDataPipeline;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class TransactionConverter {
	private final static Logger LOG = getLog();
	//TODO REFACTOR THIS!
	private final static Logger COLLECTION_CONVERTER_LOG = getCollectionConverterLog();
	private final static int MAX_CONVERSION_QUEUE_SIZE = 1000; 	
	private static final int MAX_THREADS = 10;	
	
	public static void main(String[] args) {
		/*
		MongoCollectionConnection testCollectionConnection = getCollectionConnection(TEST_TABLE);
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
		convertCollection(testCollectionConnection,executor);
		*/
		disableMongoLogInfo();
		convertAllCollections();
	}
	
	private static int calculateWalletNamesLength(Iterator<String> walletNames){
		int count = 0; 
		while(walletNames.hasNext()){
			walletNames.next();
			count++;
		}
		return count;
	}
		
	private static void convertAllCollections(){
		LOG.info("*****STARTING NEW DATASET CONVERSION!*****");			
		Iterator<String> walletNames = getWalletNamesOrQuit();		
		int walletNamesCount = getWalletNamesCountOrQuit();
		LOG.info("Wallets to convert: "+walletNamesCount);
		int collectionsConverted = 0; 				
		while(walletNames.hasNext()){				
			convertNextCollection(walletNames);					
			collectionsConverted++;			
			LOG.info("Collections Converted: "+collectionsConverted+" / "+walletNamesCount);				
		}		
		LOG.info("*****DATASET CONVERSION COMPLETED!*****");		
	}
	
	private static void convertCollection(WalletCollection unconvertedWalletCollection){			
		CollectionConverter collectionConverter = getCollectionConverter(unconvertedWalletCollection);	
		String walletName = unconvertedWalletCollection.getCollectionName();		
		try{
			ConversionResults conversionResults = collectionConverter.convertCollection();			
		} catch(Exception e){
			e.printStackTrace();
			LOG.error("Conversion failed for wallet: "+walletName+" !");
		}
		
		LOG.info("Finished converting transactions for "+walletName);		
	}
	
	private static void convertNextCollection(Iterator<String> walletNames){		
		String nextWalletName = walletNames.next();	
		LOG.info("Converting Wallet: "+nextWalletName);
		WalletCollection collectionConnection = getWalletCollection(nextWalletName);		
		convertCollection(collectionConnection);				
	}
	
	//TODO refactor this out! Push it to a mongo specific class.
	private static void buildAscendingDateIndex(MongoCollection<Document> collection){			
		collection.createIndex(new Document(WalletTransactionDocumentConverter.DATE_KEY, 1));
		collection.createIndex(new Document("txid", 1),new IndexOptions().unique(true));	
	}
	
	private static void disableMongoLogInfo(){
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}
	
	private static CollectionConverter getCollectionConverter(WalletCollection unconvertedCollection){
		WalletTransactionCurrencyConverter currencyConverter = getTransactionConverter();
		Interval conversionTimespan = getConversionTimespan(unconvertedCollection);		
		LOG.info("Converting transactions from: "+conversionTimespan.getStart()+" to: "+conversionTimespan.getEnd());
		DataPipeline<WalletTransaction, ConvertedWalletTransaction> producerConsumerPair = 
				getProducerConsumerPair(unconvertedCollection,conversionTimespan);
		CollectionConverter collectionConverter = 
				new CollectionConverter(producerConsumerPair,currencyConverter,
						MAX_CONVERSION_QUEUE_SIZE,conversionTimespan,COLLECTION_CONVERTER_LOG);		
		return collectionConverter;
	}
	
	private static WalletTransactionCurrencyConverter getTransactionConverter(){
		HistoricalBTCToUSDConverter currencyConverter = new CoindeskBpiConverter();	
		WalletTransactionCurrencyConverter transactionConverter = 
				new HistoricalWalletTransactionCurrencyConverter(currencyConverter);
		return transactionConverter;
	}
	
	private static DataPipeline<WalletTransaction, ConvertedWalletTransaction> getProducerConsumerPair(
			WalletCollection unconvertedCollection,Interval conversionTimespan){
		DataProducer<WalletTransaction> walletTransactionProducer = 
				getMongoDBWalletTransactions(unconvertedCollection,conversionTimespan);
		DataConsumer<WalletTransaction> walletTransactionConsumer = 
				getMongoDBConvertedTransactionConsumer(unconvertedCollection);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		DataPipeline<WalletTransaction, ConvertedWalletTransaction> producerConsumerPair = 
				new BasicDataPipeline(walletTransactionProducer,walletTransactionConsumer);
		return producerConsumerPair;		
	}
	
	private static DataConsumer<WalletTransaction> getMongoDBConvertedTransactionConsumer(
			WalletCollection unconvertedCollection) {
		WalletCollection convertedWalletCollection = getConvertedCollection(unconvertedCollection);
		MongoCollection<Document> convertedCollection = convertedWalletCollection.getCollection();		
		//TODO find better place to put this function
		buildAscendingDateIndex(convertedCollection);
		Converter<ConvertedWalletTransaction, Document> converter = new ConvertedWalletTransactionDocumentConverter();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		DataConsumer<WalletTransaction> consumer = new MongoDBConsumer(convertedWalletCollection,converter);
		return consumer;
	}

	private static DataProducer<WalletTransaction> getMongoDBWalletTransactions(
			WalletCollection unconvertedCollection,Interval timespan){
		Converter<WalletTransaction,Document> converter = new WalletTransactionDocumentConverter();		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		MongoDBProducer<WalletTransaction> walletTransactionConverter = 
				new MongoDBProducer(unconvertedCollection,timespan,converter);
		return walletTransactionConverter;		
	}
		
	private static Logger getCollectionConverterLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(CollectionConverter.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%t | %d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName(CollectionConverter.class.getName()+" Appender");			
		log.addAppender(appender);
		return log;
	}
	
	private static Interval getConversionTimespan(WalletCollection unconvertedCollection){					
		DateTime latestConvertedTransactionTime = getLatestTransactionTime(unconvertedCollection);		
		Interval conversionTimespan = new Interval(latestConvertedTransactionTime,new DateTime());
		return conversionTimespan;
	}
	
	private static WalletCollection getConvertedCollection(WalletCollection unconvertedCollection){
		String currencySymbol = "USD";
		ConvertedCollectionProvider provider = new ConvertedCollectionService();			
		WalletCollection convertedCollection = provider.getConvertedCollection(unconvertedCollection, currencySymbol);
		return convertedCollection;
	}
	
	private static DateTime getLatestTransactionTime(WalletCollection unconvertedCollection) {
		WalletCollection walletConnection = getConvertedCollection(unconvertedCollection);
		WalletExplorerCollectionLatestTimeQuerier latestTimeQuerier =
				new WalletExplorerCollectionLatestTimeQuerier(walletConnection);
		DateTime latestTransactionTime = null;
		try {
			latestTransactionTime = latestTimeQuerier.call();			
		} catch (Exception e) {			
			LOG.error("Could not find the latest transaction time for: "+walletConnection.getCollectionName()+"."+
					" A complete download will be done to ensure dataset integrity");
			e.printStackTrace();
			return new DateTime(0);
		}
		return latestTransactionTime;
	}
	
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(Log4JTest.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%t | %d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Console Appender");			
		log.addAppender(appender);
		return log;
	}	
	
	private static WalletCollection getWalletCollection(String walletName){		
		MongoClient mongoClient = MongoDBConnectionService.getMongoClient();
		MongoDatabase mongoDatabase = MongoDBConnectionService.getMongoDatabase();
		WalletCollection walletCollection = new WalletCollection(mongoClient,mongoDatabase,walletName);
		return walletCollection;
	}
	
	//TODO refactor this to a WalletNames factory or something. 
	private static Iterator<String> getWalletNames() throws IOException{
		File file = new File("resources/wallets.txt");
		Path path = file.toPath();
		Iterator<String> wallets = Files.readAllLines(path).iterator();
		return wallets;
	}	
	
	private static int getWalletNamesCountOrQuit(){
		int walletNamesCount = 0;
		try{
			walletNamesCount = calculateWalletNamesLength(getWalletNames());			
		} catch(IOException e){
			e.printStackTrace();
			LOG.error("Could not calculate the length of the wallet name source.");
			System.exit(0);
		}
		return walletNamesCount;
	}
	
	private static Iterator<String> getWalletNamesOrQuit(){
		Iterator<String> walletNames = null;
		try{
			walletNames = getWalletNames();
		} catch(IOException e){
			e.printStackTrace();
			LOG.fatal("Could not locate wallet names, and therefore could not convert collections!");
			System.exit(0);
		}
		return walletNames;
	}	
}