package net.stevenuray.walletexplorer.executables;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.stevenuray.walletexplorer.aggregator.WalletTransactionSum;
import net.stevenuray.walletexplorer.aggregator.WalletTransactionSums;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.mongodb.MongoDBProducer;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionSumDocumentConverter;
import net.stevenuray.walletexplorer.persistence.Converter;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.views.TransactionAggregateReviewGraph;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class WalletReviewer extends Application{	
	private final static String TARGET_WALLET_NAME = "Instawallet.org";
	private final static String TEST_COLLECTION = TARGET_WALLET_NAME+"_To_USD_Per_Month"; 
	private static WalletTransactionSums transactionSums;
	
	public static void main(String[] args) {
		setMongoLoggerLevels();
		setWalletTransactionSums();
		//printEachWalletTransaction(aggregateIterator);				
		launch(args);
	}
	
	private static void setWalletTransactionSums(){
		WalletCollection testCollection = getCollection(TEST_COLLECTION);				
		transactionSums = getWalletTransactionSums(testCollection);
	}
	
	private static WalletTransactionSums getWalletTransactionSums(WalletCollection testCollection){
		Interval timespan = new Interval(new DateTime(0), new DateTime());
		DataProducer<WalletTransactionSum> aggregateProducer = getAggregateProducer(testCollection,timespan);
		Iterator<WalletTransactionSum> aggregateIterator = aggregateProducer.getData();
		return new WalletTransactionSums(aggregateIterator,TARGET_WALLET_NAME);
	}
	
	private static DataProducer<WalletTransactionSum> getAggregateProducer(
			WalletCollection aggregateCollection,Interval timespan){
		Converter<WalletTransactionSum,Document> converter = new WalletTransactionSumDocumentConverter();
		DataProducer<WalletTransactionSum> aggregateProducer = 
				new MongoDBProducer<WalletTransactionSum>(aggregateCollection,timespan,converter);
		return aggregateProducer;
	}
				
	private static WalletCollection getCollection(String walletName){		
		MongoClient mongoClient = MongoDBConnectionService.getMongoClient();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();		
		WalletCollection testTableConnection = new WalletCollection(mongoClient,database,walletName);
		return testTableConnection;
	}
		
	private static void setMongoLoggerLevels(){
		Logger driverLogger = Logger.getLogger("org.mongodb.driver");
		Logger diagnosticsLogger = Logger.getLogger("com.mongodb.diagnostics.logging.JULLogger");		
		driverLogger.setLevel(Level.WARNING);
		diagnosticsLogger.setLevel(Level.WARNING);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {		
		TransactionAggregateReviewGraph categoryReviewGraph = new TransactionAggregateReviewGraph(transactionSums);
		LineChart<String,Number> reviewChart = categoryReviewGraph.getLineChart();
		Scene scene = new Scene(reviewChart,1920,1080);
		primaryStage.setTitle(TARGET_WALLET_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
