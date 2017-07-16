package net.stevenuray.walletexplorer.executables;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.stevenuray.walletexplorer.aggregation.CategoryAggregator;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodFactory;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationTimespan;
import net.stevenuray.walletexplorer.categories.WalletCategory;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSum;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSums;
import net.stevenuray.walletexplorer.categories.factories.CategoryFactory;
import net.stevenuray.walletexplorer.categories.factories.ExchangeCategoryFactory;
import net.stevenuray.walletexplorer.persistence.DataPipelines;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.views.TransactionAggregateReviewGraphSceneFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CategoryReviewer extends Application{		
	private static final WalletCategory CATEGORY = CategoryFactory.getGamblingServices();
	private static final int HEIGHT = 1080;
	private static WalletCategoryTransactionSums transactionSums;
	private static final int WIDTH = 1920;
	
	public static void main(String[] args) {		
		setMongoLoggerLevel();					
		setCategoryTransactionSumsOrQuit();
		//printTransactionSumsToConsole(transactionSums);		
		launch(args);
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
		TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> producerFactory = 
				DataPipelines.getMongoDBConvertedProducer();
		CategoryAggregator categoryAggregator = new CategoryAggregator(walletCategory,producerFactory);
		return categoryAggregator;
	}

	private static Interval getMaxTimespan() {
		DateTime start = new DateTime(2011,1,1,0,0,0);
		DateTime end = new DateTime();		
		return new Interval(start,end);
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

	private static void setCategoryTransactionSumsOrQuit(){		
		AggregationTimespan timespan = getAggregationTimespan();
		try {
			setWalletCategoryTransactionSums(CATEGORY,timespan);
		} catch(Exception e) {			
			e.printStackTrace();
			System.out.println("Category Reviewer encountered an exception and cannot review!");
			System.exit(0);
		}
	}

	private static void setMongoLoggerLevel(){
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}

	private static void setWalletCategoryTransactionSums (
			WalletCategory walletCategory,AggregationTimespan aggregationTimespan) 
					throws Exception{		
		CategoryAggregator categoryAggregator = getCategoryAggregator(walletCategory);			
		transactionSums = categoryAggregator.getTransactionSums(aggregationTimespan);			
	}

	@Override
	public void start(Stage primaryStage) throws Exception {	
		TransactionAggregateReviewGraphSceneFactory factory = 
				new TransactionAggregateReviewGraphSceneFactory(WIDTH,HEIGHT);
		Scene aggregateGraphScene = factory.getScene(transactionSums);			
		primaryStage.setTitle(transactionSums.getName());
		primaryStage.setScene(aggregateGraphScene);
		primaryStage.show();
	}	
}