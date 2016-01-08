package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

public interface IntervalSumBuilder {
	public void insert(TimedSum timedSum);	
	public IntervalSum build();
}
