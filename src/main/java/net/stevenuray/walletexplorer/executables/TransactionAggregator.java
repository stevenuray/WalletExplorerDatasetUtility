package net.stevenuray.walletexplorer.executables;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.stevenuray.walletexplorer.aggregation.AggregationResults;
import net.stevenuray.walletexplorer.aggregation.CollectionAggregator;
import net.stevenuray.walletexplorer.aggregation.WalletTransactionSum;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodFactory;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.mongodb.CollectionNameService;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionSumDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.datapipeline.MongoDBConsumer;
import net.stevenuray.walletexplorer.mongodb.datapipeline.MongoDBProducer;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionEarliestTimeQuerier;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.BasicDataPipeline;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TransactionAggregator {		
	public static void main(String[] args) {						
		setMongoLogInfo();					
		Collection<AggregationPeriod> aggregationPeriods = getAggregationPeriods();
		aggregateAllUsdCollectionsForAggregationPeriods(aggregationPeriods);
		
		//TODO DEVELOPMENT 
		System.out.println("Aggregation Completed!");			
	}
	
	private static void aggregateAllUsdCollectionsForAggregationPeriods(
			Collection<AggregationPeriod> aggregationPeriods){
		for(AggregationPeriod aggregationPeriod : aggregationPeriods){
			aggregateAllUsdCollections(aggregationPeriod);
		}
	}
		
	private static void aggregateAllUsdCollections(AggregationPeriod aggregationPeriod){		
		Iterator<String> walletNames = getWalletNamesOrQuit();		
		int aggregatedCollections = 0; 
		while(walletNames.hasNext()){
			String nextWalletName = walletNames.next();	
			aggregateWalletName(nextWalletName,aggregationPeriod);
			aggregatedCollections++;
			
			//TODO DEVELOPMENT
			System.out.println("Aggregation complete for wallet: "+nextWalletName);			
			System.out.println("AggregatedCollections: "+aggregatedCollections);				
		}	
	}
	
	private static void aggregateWalletName(String nextWalletName,AggregationPeriod aggregationPeriod){				
		WalletCollection convertedWalletCollection = getConvertedWalletCollection(nextWalletName);
		//TODO PASS IN ACTUAL TIMESPAN		
		Interval timespan = new Interval(new DateTime(0),new DateTime());
		aggregateCollection(nextWalletName,convertedWalletCollection,timespan,aggregationPeriod);				
	}
	
	private static void aggregateCollection(
			String walletName,WalletCollection unAggregatedCollection,Interval timespan,
			AggregationPeriod aggregationPeriod){
		System.out.println("AggregationPeriod: "+aggregationPeriod.getName());
		int maxConversionQueueSize = WalletExplorerConfig.MAX_QUEUE_LENGTH;
		DataPipeline<ConvertedWalletTransaction, WalletTransactionSum> dataPipeline = 
				getProducerConsumerPair(unAggregatedCollection,timespan,aggregationPeriod);
		/*TODO Adjust timespan here so when the aggregate collection is empty on first creation 
		 * the aggregator creates aggregation intervals from the first possible full interval instead of from 1970. 		 
		 */
		Interval aggregationTimespan = getAggregationInterval(unAggregatedCollection,timespan);
		CollectionAggregator collectionAggregator = 
				new CollectionAggregator(dataPipeline,walletName,unAggregatedCollection,
						aggregationPeriod,aggregationTimespan,maxConversionQueueSize);	
		//TODO print these to console or log 
		AggregationResults aggregateResults = collectionAggregator.aggregateCollection();
	}
	
	private static Interval getAggregationInterval(WalletCollection convertedCollection,Interval timespan) {
		DateTime start = getEarliestTransactionTime(convertedCollection);
		DateTime end = timespan.getEnd();
		return new Interval(start,end);
	}

	private static void setMongoLogInfo(){
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}
	
	private static DateTime getEarliestTransactionTime(WalletCollection convertedCollection) {		
		WalletExplorerCollectionEarliestTimeQuerier earliestTimeQuerier = 
				new WalletExplorerCollectionEarliestTimeQuerier(convertedCollection);
		DateTime earliestTransactionTime = null;
		try {
			earliestTransactionTime = earliestTimeQuerier.call();			
		} catch (Exception e) {			
			System.out.println("Could not find the latest transaction time for: "+
					convertedCollection.getCollectionName()+"."+
					" A complete aggregation will be done to ensure dataset integrity");
			e.printStackTrace();
			return new DateTime(0);
		}
		return earliestTransactionTime;
	}
	
	private static WalletCollection getAggregateCollection(
			WalletCollection unaggregatedCollection,AggregationPeriod aggregationPeriod) {
		String unconvertedName = unaggregatedCollection.getCollectionName();
		CollectionNameService collectionNameFactory = new CollectionNameService();		
		String aggregateName = collectionNameFactory.getAggregatedCollectionName(unconvertedName, aggregationPeriod);
		
		MongoClient mongoClient = unaggregatedCollection.getMongoClient();
		MongoDatabase database = unaggregatedCollection.getDatabase();
		WalletCollection aggregateCollection = 
				new WalletCollection(mongoClient,database,aggregateName);		
		return aggregateCollection;
	}
	
	private static Collection<AggregationPeriod> getAggregationPeriods(){
		Collection<AggregationPeriod> aggregationPeriodCollection = new ArrayList<>();
		aggregationPeriodCollection.add(getMonth());
		/*TODO fix issue where if there is no transactions for a collection in a year, 
		 * index out of bounds will occur and crash entire program. 
		 */
		//aggregationPeriodCollection.add(getYear());
		return aggregationPeriodCollection;
	}
	
	private static AggregationPeriod getMonth(){
		return AggregationPeriodFactory.getAggregationPeriod(AggregationPeriodFactory.AggregationSize.YEAR);
	}
	
	private static AggregationPeriod getYear(){
		return AggregationPeriodFactory.getAggregationPeriod(AggregationPeriodFactory.AggregationSize.YEAR);
	}
	
	//TODO replace with a factory of some kind
	private static WalletCollection getCollection(String walletName){		
		MongoClient mongoClient = MongoDBConnectionService.getMongoClient();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();		
		WalletCollection walletCollection = new WalletCollection(mongoClient,database,walletName);
		return walletCollection;
	}

	private static WalletCollection getConvertedWalletCollection(String walletName){
		CollectionNameService collectionNameService = new CollectionNameService();
		//TODO swap for forex currency symbol passed in by argument here. 
		String convertedWalletName = collectionNameService.getConvertedCollectionName(walletName, "USD");
		//TODO DEVELOPMENT
		System.out.println("About to aggregate collection: "+convertedWalletName);
		WalletCollection convertedWalletCollection = getCollection(convertedWalletName);
		return convertedWalletCollection;
	}

	private static DataConsumer<WalletTransactionSum> getMongoDBAggregatedTransactionsConsumer(
			WalletCollection unAggregatedCollection,AggregationPeriod aggregationPeriod) {
		WalletCollection aggregateWalletCollection = getAggregateCollection(unAggregatedCollection,aggregationPeriod);		
		Converter<WalletTransactionSum, Document> converter = new WalletTransactionSumDocumentConverter();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		DataConsumer<WalletTransactionSum> consumer = new MongoDBConsumer(aggregateWalletCollection,converter);
		return consumer;
	}
	
	private static DataProducer<ConvertedWalletTransaction> getMongoDBConvertedWalletTransactions(
			WalletCollection convertedCollection,Interval timespan){
		Converter<ConvertedWalletTransaction,Document> converter = new ConvertedWalletTransactionDocumentConverter();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		MongoDBProducer<ConvertedWalletTransaction> walletTransactionProducer = 
				new MongoDBProducer(convertedCollection,timespan,converter);
		return walletTransactionProducer;		
	}
	
	private static DataPipeline<ConvertedWalletTransaction, WalletTransactionSum> getProducerConsumerPair(
			WalletCollection unAggregatedCollection,Interval timespan,AggregationPeriod aggregationPeriod) {
		DataProducer<ConvertedWalletTransaction> producer = 
				getMongoDBConvertedWalletTransactions(unAggregatedCollection,timespan);
		DataConsumer<WalletTransactionSum> consumer = 
				getMongoDBAggregatedTransactionsConsumer(unAggregatedCollection,aggregationPeriod);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		DataPipeline<ConvertedWalletTransaction, WalletTransactionSum> producerConsumerPair = 
				new BasicDataPipeline(producer,consumer);
		return producerConsumerPair;
	}
		
	//TODO refactor to a WalletNames factory or something similar. 
	private static Iterator<String> getWalletNames() throws IOException{
		File file = new File("resources/wallets.txt");
		Path path = file.toPath();
		Iterator<String> wallets = Files.readAllLines(path).iterator();
		return wallets;
	}
	
	private static Iterator<String> getWalletNamesOrQuit(){
		Iterator<String> walletNames = null;
		try{
			walletNames = getWalletNames();
		} catch(IOException e){
			e.printStackTrace();
			System.out.println("Could not locate wallet names, and therefore could not convert collections!");
			System.exit(0);
		}
		return walletNames;
	}	
}
