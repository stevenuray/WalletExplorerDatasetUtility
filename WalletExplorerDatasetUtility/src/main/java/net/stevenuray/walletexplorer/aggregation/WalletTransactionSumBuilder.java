package net.stevenuray.walletexplorer.aggregation;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSumBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.SimpleIntervalSumBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.TimedSum;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class WalletTransactionSumBuilder {
	private final Interval timespan;
	private final IntervalSumBuilder transactionVolumeSumBuilder;
	private final String walletName;
	
	public WalletTransactionSumBuilder(String walletName,Interval timespan){
		this.walletName = walletName;
		this.timespan = timespan;
		transactionVolumeSumBuilder = new SimpleIntervalSumBuilder(timespan);		
	}
	
	public synchronized WalletTransactionSum build(){
		IntervalSum transactionVolumeSum = transactionVolumeSumBuilder.build();
		WalletTransactionSum walletTransactionAggregate = 
				new WalletTransactionSum(walletName,timespan,transactionVolumeSum);
		return walletTransactionAggregate;
	}
	
	public Interval getTimespan(){
		return timespan;
	}
	
	public synchronized void insert(ConvertedWalletTransaction convertedWalletTransaction){		
		if(isWalletTransactionInTimespan(convertedWalletTransaction)){
			sumTransaction(convertedWalletTransaction);
		}
	}
		
	public boolean isTimeAfterTimespan(DateTime time){
		if(time.isAfter(timespan.getEnd())){
			return true;
		} else{
			return false;
		}
	}

	public boolean isTimeInTimespan(DateTime time){
		if(timespan.contains(time)){
			return true;
		} else{
			return false;
		}
	}
	
	private boolean isWalletTransactionInTimespan(ConvertedWalletTransaction convertedWalletTransaction) {
		DateTime transactionTime = convertedWalletTransaction.getTransactionTime();
		if(isTimeInTimespan(transactionTime)){
			return true;
		} else{
			return false;
		}
	}
	
	private void sumTransaction(ConvertedWalletTransaction convertedWalletTransaction){
		double transactionVolumeSum = convertedWalletTransaction.getTransactionOutputVolumeSumInUsd();		
		DateTime transactionTime = convertedWalletTransaction.getTransactionTime();
		TimedSum transactionTimedSum = new TimedSum(transactionTime,transactionVolumeSum);
		transactionVolumeSumBuilder.insert(transactionTimedSum);
	}
}
