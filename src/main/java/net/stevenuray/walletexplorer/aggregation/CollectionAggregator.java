package net.stevenuray.walletexplorer.aggregation;

import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodListBuilder;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class CollectionAggregator {	
	private final AggregationPeriod aggregationPeriod;
	private int aggregations = 0;
	private final List<Interval> ascendingPeriodList;	
	private WalletTransactionSumBuilder currentAggregateBuilder;
	private final DataPipeline<ConvertedWalletTransaction, WalletTransactionSum> dataPipeline;
	private int transactions = 0;
	private final String walletName;
	
	public CollectionAggregator(
			DataPipeline<ConvertedWalletTransaction, WalletTransactionSum> dataPipeline,
			String walletName,WalletCollection unAggregatedCollection,
			AggregationPeriod aggregationPeriod,Interval timespan,int maxConversionQueueSize) {
		this.dataPipeline = dataPipeline;
		this.walletName = walletName;		
		this.aggregationPeriod = aggregationPeriod;			
		this.ascendingPeriodList = getAscendingPeriodList(timespan);
	}	
	
	public AggregationResults aggregateCollection(){
		//TODO use these
		AggregationResults aggregationResults = new AggregationResults();
		dataPipeline.start();
		//Note: this assumes the Iterator<ConvertedWalletTransaction> is in time-ascending order!
		//TODO make this more explicit
		Iterator<ConvertedWalletTransaction> convertedTransactionIterator = dataPipeline.getData();		
		while(convertedTransactionIterator.hasNext()){
			ConvertedWalletTransaction nextTransaction = convertedTransactionIterator.next();
			aggregate(nextTransaction);
		}
		
		dataPipeline.finish();
		//TODO DEVELOPMENT
		System.out.println("Transactions Aggregated: "+transactions);
		System.out.println("Aggregates Created: "+aggregations);
		return aggregationResults;
	}

	private void aggregate(ConvertedWalletTransaction convertedWalletTransaction){
		if(isTimeToCreateFirstAggregateBuilder()){
			createFirstAggregateBuilder();					
		}

		if(isTimeToBuildAggregate(convertedWalletTransaction)){		
			buildNextWalletTransactionSumAndGiveToConsumer();
			onAggregateCompletion();
		} else{
			transactions++;
			currentAggregateBuilder.insert(convertedWalletTransaction);				
		}
	}
	
	private void buildNextWalletTransactionSumAndGiveToConsumer(){
		WalletTransactionSum walletTransactionAggregate = currentAggregateBuilder.build();
		DataConsumer<WalletTransactionSum> consumer = dataPipeline.getConsumer();
		consumer.consume(walletTransactionAggregate);
	}
	
	private void createFirstAggregateBuilder(){		
		//TODO create and implement ascending time order list here. 
		//Exploiting the fact the list should be in ascending order. 
		Interval firstAggregationInterval = ascendingPeriodList.get(0);
		currentAggregateBuilder = new WalletTransactionSumBuilder(walletName,firstAggregationInterval);
	}
	
	private void createNextAggregateBuilder(DateTime startTime){
		//TODO fix this start time so it is even with the last end time
		DateTime nextEndTime = aggregationPeriod.getNextCompleteInterval(startTime).getStart();
		Interval nextAggregationInterval = new Interval(startTime,nextEndTime);
		currentAggregateBuilder = new WalletTransactionSumBuilder(walletName,nextAggregationInterval);
	}
		
	private List<Interval> getAscendingPeriodList(Interval timespan) {
		AggregationPeriodListBuilder builder = new AggregationPeriodListBuilder(timespan,aggregationPeriod);
		return builder.getAscendingPeriodList();		
	}	
	
	private DateTime getNextStartTime() {
		Interval nextInterval = ascendingPeriodList.get(aggregations);
		return nextInterval.getStart();
	}

	private boolean isAggregationComplete(){
		if(aggregations >= ascendingPeriodList.size()){
			return true; 
		} else{
			return false;
		}
	}
	
	private boolean isTimeToBuildAggregate(ConvertedWalletTransaction convertedWalletTransaction){
		DateTime currentTransactionTime = convertedWalletTransaction.getTransactionTime();
		if(currentAggregateBuilder.isTimeAfterTimespan(currentTransactionTime)){
			return true;
		} else{
			return false;			
		}
	}

	private boolean isTimeToCreateFirstAggregateBuilder() {
		if(currentAggregateBuilder == null){
			return true;
		} else{
			return false;
		}
	}
	
	private void onAggregateCompletion() {
		aggregations++;					 
		if(!isAggregationComplete()){
			DateTime nextStartTime = getNextStartTime();
			createNextAggregateBuilder(nextStartTime);		
		} 
	}
}