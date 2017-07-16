package net.stevenuray.walletexplorer.categories;

import java.util.ArrayList;
import java.util.List;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodListBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationTimespan;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSumBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.SimpleIntervalSumBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.TimedSum;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class WalletCategoryTransactionSumsBuilder {
	private final List<IntervalSumBuilder> sumBuilders;
	private final WalletCategory walletCategory;
		
	public WalletCategoryTransactionSumsBuilder(WalletCategory walletCategory,AggregationTimespan timespan){		
		this.sumBuilders = getEmptySumBuilders(timespan);
		this.walletCategory = walletCategory;		
	}
	
	/**Should not be called before the process of getting all information for a WalletCategoryTransactionSums
	 * object is completed. Also should not be called unless the process of getting all information for a 
	 * WalletCategoryTransactionSums object encountered no errors. Informational inaccuracies will occur
	 * if either of these two errors occur. 
	 * @return - A WalletCategoryTransactionSums object based on the current state of this object.
	 */
	public WalletCategoryTransactionSums build(){	
		List<WalletCategoryTransactionSum> transactionSumsList = getTransactionSums();
		WalletCategoryTransactionSums transactionSums = 
				new WalletCategoryTransactionSums(walletCategory,transactionSumsList);
		return transactionSums;
	}
	
	public synchronized void insert(ConvertedWalletTransaction walletTransaction){
		TimedSum walletTransactionSumInUsd = extract(walletTransaction);
		//TODO refactor to something more efficient, like key/value. 
		insertViaForLoop(walletTransactionSumInUsd);	
	}
	
	private TimedSum extract(ConvertedWalletTransaction walletTransaction){		
		DateTime transactionTime = walletTransaction.getTransactionTime();
		double transactionAmountInUsd = walletTransaction.getTransactionOutputVolumeSumInUsd();
		TimedSum sumInUsd = new TimedSum(transactionTime,transactionAmountInUsd);
		return sumInUsd;
	}
	
	private List<Interval> getBuilderIntervals(AggregationTimespan timespan){
		AggregationPeriodListBuilder listBuilder = new AggregationPeriodListBuilder(timespan);
		List<Interval> builderIntervals = listBuilder.getAscendingPeriodList();
		return builderIntervals;
	}
	
	private List<IntervalSumBuilder> getEmptySumBuilders(AggregationTimespan timespan){		
		List<Interval> intervals = getBuilderIntervals(timespan);		
		List<IntervalSumBuilder> builders = getEmptySumBuilders(intervals);
		return builders;
	}
	
	private List<IntervalSumBuilder> getEmptySumBuilders(List<Interval> intervals){
		List<IntervalSumBuilder> builders = new ArrayList<IntervalSumBuilder>();
		for(Interval interval : intervals){
			IntervalSumBuilder builder = new SimpleIntervalSumBuilder(interval);
			builders.add(builder);
		}
		return builders;
	}
	
	private List<WalletCategoryTransactionSum> getTransactionSums(){
		List<WalletCategoryTransactionSum> transactionSums = new ArrayList<WalletCategoryTransactionSum>();
		for(IntervalSumBuilder builder : sumBuilders){
			IntervalSum sum = builder.build();
			WalletCategoryTransactionSum transactionSum = new WalletCategoryTransactionSum(walletCategory,sum);
			transactionSums.add(transactionSum);
		}
		return transactionSums;
	}
	
	private void insertViaForLoop(TimedSum walletTransactionSum){
		for(IntervalSumBuilder sumBuilder : sumBuilders){
			/*Exploiting SimpleIntervalSumBuilder's check on the time of each TimedSum,
			 * it won't add the TimedSum to the total unless it is in the correct interval. 
			 * This could cause problems if other implementations of IntervalSumBuilder 
			 * don't check the time or throw exceptions if the TimedSum is not in their Interval. 
			 */
			sumBuilder.insert(walletTransactionSum);
		}
	}
}