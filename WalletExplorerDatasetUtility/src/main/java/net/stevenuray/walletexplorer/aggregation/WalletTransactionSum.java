package net.stevenuray.walletexplorer.aggregation;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;

import org.joda.time.Interval;

public class WalletTransactionSum implements TransactionIntervalSum{	
	private final IntervalSum aggregateTransactionVolume;
	private final Interval timespan;
	private final String walletName;
		
	public WalletTransactionSum(String walletName,Interval timespan, IntervalSum aggregateTransactionVolume){	
		this.walletName = walletName;
		this.timespan = timespan;
		this.aggregateTransactionVolume = aggregateTransactionVolume;
	}
		
	public IntervalSum getAggregateTransactionVolume(){
		return aggregateTransactionVolume;
	}

	public String getName() {
		return walletName;
	}
	
	public Interval getTimespan(){
		return timespan;
	}
	
	public IntervalSum getTransactionIntervalSum() {
		return getAggregateTransactionVolume();
	}
		
	public String toPrettyString(){
		return aggregateTransactionVolume.toPrettyString();
	}
	
	public String toString(){
		return toPrettyString();
	}	
}