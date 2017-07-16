package net.stevenuray.walletexplorer.testobjects;

import net.stevenuray.walletexplorer.aggregation.WalletTransactionSum;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TestWalletTransactionSums {
	
	public static WalletTransactionSum getWalletTransactionSum(){		
		double sumTotal = 1000;
		return getWalletTransactionSum(sumTotal);
	}
	
	public static WalletTransactionSum getWalletTransactionSum(double sumTotal){
		String walletName = "BTC-e.com";
		DateTime start = new DateTime(2015,1,1,0,0,0);
		DateTime end = new DateTime(2015,2,1,0,0,0);
		Interval timespan = new Interval(start,end);		
		IntervalSum aggregateTransactionVolume = new IntervalSum(timespan,sumTotal);
		WalletTransactionSum walletTransactionSum =
				new WalletTransactionSum(walletName,timespan,aggregateTransactionVolume);
		return walletTransactionSum;
	}
}
