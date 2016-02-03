package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

public interface IntervalSumBuilder {
	public void insert(TimedSum timedSum);	
	public IntervalSum build();
}
