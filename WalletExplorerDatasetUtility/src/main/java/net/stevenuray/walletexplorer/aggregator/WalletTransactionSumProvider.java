package net.stevenuray.walletexplorer.aggregator;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletAttribute;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletEOF;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletHeader;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTrailer;

public class WalletTransactionSumProvider implements Iterator<WalletTransactionSum>{
	private static final int POLL_TIMEOUT_SECONDS = 30;
	private final BlockingQueue<WalletAttribute> sourceQueue;
	private boolean hasMoreTransactions = true;
	
	public WalletTransactionSumProvider(BlockingQueue<WalletAttribute> sourceQueue){
		this.sourceQueue = sourceQueue;
	}
	
	public boolean hasNext() {
		return hasMoreTransactions;
	}

	public WalletTransactionSum next() {
		WalletAttribute walletAttribute = null;
		try{
			walletAttribute = sourceQueue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch(InterruptedException e){
			hasMoreTransactions = false;
		}
		if(walletAttribute instanceof WalletEOF){
			//This is an indication from the source it is out of transactions.
			hasMoreTransactions = false;
		}
		if(walletAttribute instanceof WalletTrailer){
			/*This is an indication from the source all transactions from the current wallet have been inserted
			 * into the queue. Disregarding this indication by calling this function again, with the intention
			 * that the next WalletAttribute will be something different.
			 */
			return next();
		}
		if(walletAttribute instanceof WalletHeader){
			/*This is an indication from the source transactions are coming from a new wallet. 
			 * Disregarding it by calling this function again, with the intention that the next 
			 * WalletAttribute will be something different. 
			 */
			return next();
		}
		
		if(walletAttribute instanceof WalletTransactionSum){
			WalletTransactionSum transactionAggregate = (WalletTransactionSum) walletAttribute;
			return transactionAggregate;
		}
		
		/*If there was an error getting the next transaction, or we are at the end of transactions, return null
		 * to indicate this to callers. 
		 * TODO implement better system here. 
		 */
		hasMoreTransactions = false;
		return null;
	}

	public Iterator<TransactionIntervalSum> getSums() {
		return new TransactionIntervalSumIterator(this);
	}
	
	/**Wraps the parent class to enable converting it's WalletTransactionAggregate Iterator to produce an
	 * TransactionIntervalSum Iterator
	 * @author Steven Uray 2015-11-20
	 */
	private class TransactionIntervalSumIterator implements Iterator<TransactionIntervalSum>{
		private final Iterator<WalletTransactionSum> aggregateIterator;
		public TransactionIntervalSumIterator(WalletTransactionSumProvider aggregateIterator){
			this.aggregateIterator = aggregateIterator;
		}
		public boolean hasNext() {
			return aggregateIterator.hasNext();
		}

		public TransactionIntervalSum next() {
			return aggregateIterator.next();
		}		
	}
}
