package net.stevenuray.walletexplorer.dto;

import java.util.Iterator;

public interface TransactionIntervalSums {
	public String getName();
	public Iterator<TransactionIntervalSum> getSums();
}
