package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

import org.joda.time.DateTime;

public class TimedSum {
	private final DateTime time;
	public DateTime getTime(){
		return time;
	}
	private final double sum;
	public Double getSum(){
		return sum;
	}
	public TimedSum(DateTime time, double sum){
		this.time = time;
		this.sum = sum;
	}
}
