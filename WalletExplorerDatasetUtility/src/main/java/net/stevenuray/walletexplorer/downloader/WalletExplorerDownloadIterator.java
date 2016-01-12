package net.stevenuray.walletexplorer.downloader;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.joda.time.DateTime;

import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

/**Wraps a WalletExplorerQuerier to turn it into an iterator. 
 * Will throw an unchecked FailureToRetrieveDataException if there is problems 
 * getting data from WalletExplorer. 
 * @author Steven Uray  
 */
public class WalletExplorerDownloadIterator implements Iterator<WalletTransaction>{
	private int currentIndex = 0;
	private List<WalletTransaction> currentList;
	private final WalletExplorerQuerier querier;
	
	public WalletExplorerDownloadIterator(String walletName,int maxQueueSize){
		querier = new WalletExplorerQuerier(walletName);			
	}
	
	public WalletExplorerDownloadIterator(String walletName,int maxQueueSize,DateTime endTime){
		querier = new WalletExplorerQuerier(walletName,endTime);
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
		//Will happen on the very first call
		if(currentList == null){
			tryToGetNewListOfDownloadedTransactionsOrThrowException();
		}
		
		if(currentIndex < currentList.size()){
			return getNextWalletTransactionFromListAndAdjustIndex();			
		} else{
			//TODO possibly implement a better response.
			tryToGetNewListOfDownloadedTransactionsOrThrowException();
			currentIndex = 0; 
			return getNextWalletTransactionFromListAndAdjustIndex();
		}		
	}
	
	private WalletTransaction getNextWalletTransactionFromListAndAdjustIndex(){
		WalletTransaction nextTransaction = currentList.get(currentIndex);
		currentIndex++;
		return nextTransaction;
	}
	
	private void tryToGetNewListOfDownloadedTransactionsOrThrowException(){
		try{
			currentList = querier.getNextWalletTransactions();
		} catch(Exception e){
			throw new FailureToRetrieveDataException();
		}
	}
}