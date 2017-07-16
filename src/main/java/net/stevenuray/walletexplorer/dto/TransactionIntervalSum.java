package net.stevenuray.walletexplorer.dto;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;

public interface TransactionIntervalSum {
	public IntervalSum getTransactionIntervalSum();
	public String getName();
}
