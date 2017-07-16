package net.stevenuray.walletexplorer.testobjects;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TestIntervalSums {
	public static IntervalSum getIntervalSum(){
		double sum = 1000;
		return getIntervalSum(sum);
	}
	
	public static IntervalSum getIntervalSum(double sum){
		DateTime start = new DateTime(2015,1,1,0,0,0);
		DateTime end = new DateTime(2015,2,1,0,0,0);
		Interval timespan = new Interval(start,end);
		IntervalSum intervalSum = new IntervalSum(timespan,sum);
		return intervalSum;
	}	
}