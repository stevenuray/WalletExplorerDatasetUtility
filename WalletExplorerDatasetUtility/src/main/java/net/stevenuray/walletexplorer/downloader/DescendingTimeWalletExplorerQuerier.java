package net.stevenuray.walletexplorer.downloader;

import java.util.List;

import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.joda.time.Interval;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**One WalletExplorerQuerier instance should be used per download of a wallet. Re-use is not intended. 
 * 
 * Note multiple instances of WalletExplorerQuerier should not be used concurrently. 
 * Ales Janda (Creator of WalletExplorer) requested users of his API limit their use to one thread at a time 
 * in May of 2015. 
 * @author Steven Uray 
 */
//TODO break this class down into smaller classes.
public class DescendingTimeWalletExplorerQuerier extends WalletExplorerQuerier{	
	/**Does not query for, or return, transactions before a given time. Useful for non-redundant downloading.	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned.  
	 */
	public DescendingTimeWalletExplorerQuerier(String walletName,Interval timespanLimit){
		super(walletName,timespanLimit);
	}
	
	/*Note this object calls WalletExplorer in time ascending order of transactions. 
	 * If the timespanLimit of this object is quite early it will need to take a long time 
	 * to return the first object.	
	 */
	public List<WalletTransaction> getNextWalletTransactions() throws Exception{
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,queryFromCount);			
		JSONObject latestQueryJsonObject = tryToGetQueryResponse(queryParams);	
		updateCountersAfterResponse(latestQueryJsonObject);
		if(isLatestResponseEqualToOrAfterStartTime(latestQueryJsonObject)){
			List<WalletTransaction> walletTransactions = convertWalletTransactionsFromResponse(latestQueryJsonObject);
			downloading = shouldContinueDownloading(latestQueryJsonObject);
			return walletTransactions;
		} else{
			//Keep downloading until transactions are equal to or after the start limit.			
			return getNextWalletTransactions();
		}
	}
	
	private boolean isDownloadComplete(){
		if(queryFromCount >= txsCount){
			return true;
		} else{
			return false;
		}
	}
	
	private boolean shouldContinueDownloading(JSONObject latestQueryJsonObject){		
		if(isDownloadComplete()){
			return false;
		}
		
		if(isLatestResponseEqualToOrBeforeStartTime(latestQueryJsonObject)){
			return false; 
		}
			
		return true;
	}
		
	private void updateCountersAfterResponse(JSONObject latestQueryJsonObject){
		queryFromCount += MAX_TRANSACTIONS_PER_QUERY;
		txsCount = getWalletTransactionsCount(latestQueryJsonObject);		
	}
}