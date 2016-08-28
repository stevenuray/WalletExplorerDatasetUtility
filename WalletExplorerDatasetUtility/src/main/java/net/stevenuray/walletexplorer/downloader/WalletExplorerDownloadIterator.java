package net.stevenuray.walletexplorer.downloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

/**Wraps a WalletExplorerQuerier to turn it into an iterator. 
 * Will throw an unchecked FailureToRetrieveDataException if there is problems 
 * getting data from WalletExplorer. 
 * @author Steven Uray  
 */
public class WalletExplorerDownloadIterator implements Iterator<WalletTransaction>{
	private int currentIndex = 0;		
	private List<WalletTransaction> currentPage = new ArrayList<>();
	private final DescendingTimeWalletExplorerQuerier querier;
	private final String walletName;
	
	/**	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned. 
	 * The actual dataset may have a different timespan. 
	 */
	public WalletExplorerDownloadIterator(String walletName,Interval timespanLimit){
		this.walletName = walletName;
		querier = new DescendingTimeWalletExplorerQuerier(walletName,timespanLimit);
	}
	
	public DateTime getEarliestTime() {
		return querier.getEarliestTime();
	}
	
	public DateTime getLatestTime() {
		return querier.getLatestTime();
	}

	public String getWalletName() {
		return walletName;
	}
	
	public boolean hasNext() {
		if(querier.isDownloading()){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * @throws FailureToRetrieveDataException - If there was a problem getting data from WalletExplorer. 
	 * This is most commonly due to networking errors. 
	 */
	public WalletTransaction next() {	
		if(shouldDownloadNewPageOfTransactions()){
			//TODO possibly implement a better response.
			tryToGetNewPageOfDownloadedTransactionsOrThrowException();
			currentIndex = 0; 			
		} 		
		return getNextWalletTransactionFromPageAndAdjustIndex();
	}
		
	private boolean shouldDownloadNewPageOfTransactions(){
		return (currentIndex >= currentPage.size());
	}

	private WalletTransaction getNextWalletTransactionFromPageAndAdjustIndex(){				
		WalletTransaction nextTransaction = currentPage.get(currentIndex);
		currentIndex++;
		return nextTransaction;		
	}

	private void tryToGetNewPageOfDownloadedTransactionsOrThrowException(){
		try{
			currentPage = querier.getNextWalletTransactions();
		} catch(Exception e){
			throw new FailureToRetrieveDataException(e);
		}
	}
}