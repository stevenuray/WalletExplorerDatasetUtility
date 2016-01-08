package net.stevenuray.walletexplorer.aggregator;

import java.util.Iterator;

import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSums;

public class WalletTransactionSums implements TransactionIntervalSums {
	private class AdapterIterator implements Iterator<TransactionIntervalSum>{
		private final Iterator<WalletTransactionSum> iterator;
		private AdapterIterator(Iterator<WalletTransactionSum> iterator){
			this.iterator = iterator;
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public TransactionIntervalSum next() {
			return iterator.next();
		}		
	}
	private final Iterator<WalletTransactionSum> transactionSums;	
	private final String name;

	public WalletTransactionSums(Iterator<WalletTransactionSum> transactionSums,String name){
		this.transactionSums = transactionSums;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Iterator<TransactionIntervalSum> getSums() {
		return new AdapterIterator(transactionSums);
	}
}
