package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

import org.joda.time.Interval;

public class SimpleIntervalSumBuilder implements IntervalSumBuilder{
	private final Interval timespan;	
	private double sum = 0; 
	
	public SimpleIntervalSumBuilder(Interval timespan){
		this.timespan = timespan;
	}

	public IntervalSum build() {		
		return new IntervalSum(timespan,sum);
	}	

	public synchronized void insert(TimedSum timedSum) {
		if(isTimedSumInTimespan(timedSum)){
			sum+=timedSum.getSum();
		}		
	}

	private boolean isTimedSumInTimespan(TimedSum timedSum) {
		if(timespan.contains(timedSum.getTime())){
			return true;
		} else{
			return false;
		}		
	}
}