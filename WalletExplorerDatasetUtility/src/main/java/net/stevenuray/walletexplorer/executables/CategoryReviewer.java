package net.stevenuray.walletexplorer.executables;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriodFactory;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationTimespan;
import net.stevenuray.walletexplorer.categories.CategoryAggregator;
import net.stevenuray.walletexplorer.categories.CategoryProvider;
import net.stevenuray.walletexplorer.categories.ManualCategories;
import net.stevenuray.walletexplorer.categories.WalletCategory;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSum;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSums;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.mongodb.MongoDBCategoryProvider;
import net.stevenuray.walletexplorer.views.TransactionAggregateReviewGraph;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

public class CategoryReviewer extends Application{		
	private static WalletCategoryTransactionSums transactionSums;
	
	public static void main(String[] args) {		
		setMongoLoggerLevel();					
		setCategoryTransactionSumsOrQuit();
		//printTransactionSumsToConsole(transactionSums);		
		launch(args);
	}
	
	private static void setCategoryTransactionSumsOrQuit(){
		WalletCategory category = ManualCategories.getPools();
		AggregationTimespan timespan = getAggregationTimespan();
		try {
			setWalletCategoryTransactionSums(category,timespan);
		} catch (InterruptedException e){
			//TODO DEVELOPMENT
			System.out.println("Category Reviewer was interrupted before it could finish reviewing!");
			System.exit(0);
		} catch(ExecutionException e) {			
			e.printStackTrace();
			System.out.println("Category Reviewer encountered an exception and cannot review!");
			System.exit(0);
		}
	}
	
	private static void setMongoLoggerLevel(){
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}
	
	private static AggregationPeriod getAggregationPeriod(){
		return AggregationPeriodFactory.getAggregationPeriod(AggregationPeriodFactory.AggregationSize.MONTH);
	}

	private static AggregationTimespan getAggregationTimespan() {
		AggregationPeriod aggregationPeriod = getAggregationPeriod();
		Interval timespan = getMaxTimespan();
		return new AggregationTimespan(timespan,aggregationPeriod);
	}

	private static CategoryAggregator getCategoryAggregator(WalletCategory walletCategory) {		
		CategoryProvider<ConvertedWalletTransaction> categoryProvider = getMongoCategoryProvider();
		int maxQueueSize = WalletExplorerConfig.MAX_QUEUE_LENGTH;
		CategoryAggregator categoryAggregator = 
				new CategoryAggregator(walletCategory,categoryProvider,maxQueueSize);
		return categoryAggregator;
	}
	
	private static CategoryProvider<ConvertedWalletTransaction> getMongoCategoryProvider() {		
		MongoDBCategoryProvider mongoDBCategoryProvider = new MongoDBCategoryProvider();
		return mongoDBCategoryProvider;
	}

	private static Interval getMaxTimespan() {
		DateTime start = new DateTime(2011,1,1,0,0,0);
		DateTime end = new DateTime();		
		return new Interval(start,end);
	}	

	private static void setWalletCategoryTransactionSums (
			WalletCategory walletCategory,AggregationTimespan aggregationTimespan) 
					throws InterruptedException, ExecutionException{		
		CategoryAggregator categoryAggregator = getCategoryAggregator(walletCategory);			
		transactionSums = categoryAggregator.getTransactionSums(aggregationTimespan);			
	}

	private static Interval getYearTimespan(int year){
		DateTime start = new DateTime(year-1,12,31,0,0,0);
		DateTime end = new DateTime(year+1,1,1,0,0,0);
		return new Interval(start,end);
	}

	private static void printTransactionSumsToConsole(WalletCategoryTransactionSums transactionSums) {
		//Header message. 
		String currency = "USD";
		System.out.println("Statistics for: "+transactionSums.getWalletCategory().getName()+" in "+currency);
		System.out.println("");
		
		//Body message. 
		List<WalletCategoryTransactionSum> transactionSumsList = transactionSums.getWalletCategoryTransactionSums();
		for(WalletCategoryTransactionSum transactionSum : transactionSumsList){
			System.out.println(transactionSum.getTransactionSumInUSD().toPrettyString());
		}		
		
		//End message.
		System.out.println("");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		TransactionAggregateReviewGraph graph = new TransactionAggregateReviewGraph(transactionSums);
		LineChart<String,Number> reviewChart = graph.getLineChart();
		Scene scene = new Scene(reviewChart,1920,1080);
		primaryStage.setTitle("Category Reviewer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}	
}
