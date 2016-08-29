package net.stevenuray.walletexplorer.downloader.general;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class WalletExplorerQuerier {

	protected static final int FROM_COUNT_MANDATORY_DIVISIBLE = 100;
	protected static final int MAX_CONNECTION_ATTEMPTS = 3;
	protected static final int MAX_TRANSACTIONS_PER_QUERY = 100;
	protected final WalletExplorerClient client = new WalletExplorerClient();
	protected boolean downloading = true;
	protected long queryFromCount = 0;
	protected Interval timespanLimit;
	protected Long txsCount;
	protected final String walletName;

	public WalletExplorerQuerier(String walletName,Interval timespanLimit) {
		this.walletName = walletName;
		this.timespanLimit = timespanLimit;		
	}

	public DateTime getEarliestTime() {
		return client.getEarliestTime(walletName);				
	}

	public DateTime getLatestTime() {		
		return client.getLatestTime(walletName);		
	}

	public boolean isDownloading() {
		return downloading;
	}

	protected List<WalletTransaction> convertWalletTransactions(JSONArray subWalletTransactions)
			throws InterruptedException {
		List<WalletTransaction> walletTransactions = new ArrayList<WalletTransaction>();
		Converter<WalletTransaction,JSONObject> converter = new WalletTransactionJSONConverter();
		for(int i = 0; i < subWalletTransactions.size(); i++){
			JSONObject walletTransactionJson = subWalletTransactions.getJSONObject(i);
			WalletTransaction walletTransaction = converter.from(walletTransactionJson);
			walletTransactions.add(walletTransaction);
		}		
		return walletTransactions;
	}

	protected List<WalletTransaction> convertWalletTransactionsFromResponse(JSONObject latestQueryJsonObject)
			throws InterruptedException {
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		List<WalletTransaction> walletTransactions = convertWalletTransactions(subWalletTransactions);	
		return walletTransactions;
	}

	protected long getLastTxsCountRemainder() {
		return txsCount % FROM_COUNT_MANDATORY_DIVISIBLE;
	}

	/**WalletExplorer.com API only accepts fromCount parameters that are divisible by 100. 
	 * Note: txsCount must be set before this function is called for it to return correctly. 
	 * @return
	 */
	protected long getLastValidTransactionFromCount() {
		throwIllegalStateExceptionIfTxsCountHasNotBeenSet();
		long remainder = getLastTxsCountRemainder();
		long lastValidTransactionFromCount = txsCount - remainder;
		return lastValidTransactionFromCount;
	}

	protected JSONArray getSubWalletTransactionsFromResponse(JSONObject latestQueryJsonObject) {
		JSONArray subWalletTransactions = (JSONArray) latestQueryJsonObject.get("txs");
		return subWalletTransactions;
	}

	protected DateTime getTimeOfSubWalletTransaction(JSONObject subWalletTransaction) {
		long unixTimestamp = subWalletTransaction.getLong("time");
		DateTime subwalletTransactionTime = new DateTime(unixTimestamp*1000);
		return subwalletTransactionTime;
	}

	protected MultivaluedMapImpl getWalletExplorerQueryParams(String walletName, long fromCount) {		 
		String caller = WalletExplorerAPIConfigSingleton.CALLER;
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams = new MultivaluedMapImpl();
		queryParams.add("wallet", walletName);
		queryParams.add("from", fromCount);
		queryParams.add("count", MAX_TRANSACTIONS_PER_QUERY);
		queryParams.add("caller", caller);
		return queryParams;
	}

	protected Long getWalletTransactionsCount(JSONObject responseJson) {							
		Long txsCount = responseJson.getLong("txs_count");
		return txsCount;
	}

	protected boolean isLatestResponseAfterEndTime(JSONObject latestQueryJsonObject) {
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);				
		for(int k = 0; k < subWalletTransactions.size(); k++){			
			JSONObject subWalletTransaction = (JSONObject) subWalletTransactions.get(k);			
			DateTime subWalletTransactionTime = getTimeOfSubWalletTransaction(subWalletTransaction);		
			if(subWalletTransactionTime.isAfter(timespanLimit.getEnd())){
				return true;
			}
		}

		return false;
	}

	protected boolean isLatestResponseEqualToOrAfterStartTime(JSONObject latestQueryJsonObject) {
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);					
		for(int k = 0; k < subWalletTransactions.size(); k++){			
			JSONObject subWalletTransaction = (JSONObject) subWalletTransactions.get(k);			
			DateTime currentTime = getTimeOfSubWalletTransaction(subWalletTransaction);		
			if(currentTime.isEqual(timespanLimit.getStart())){
				return true;
			}
			if(currentTime.isAfter(timespanLimit.getStart())){					
				return true;
			}
		}

		return false;
	}

	protected boolean isLatestResponseEqualToOrBeforeStartTime(JSONObject latestQueryJsonObject) {
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		for(int k = 0; k < subWalletTransactions.size(); k++){			
			JSONObject subWalletTransaction = (JSONObject) subWalletTransactions.get(k);			
			DateTime subWalletTransactionTime = getTimeOfSubWalletTransaction(subWalletTransaction);
			if(subWalletTransactionTime.isEqual(timespanLimit.getStart())){
				return true;
			}
			if(subWalletTransactionTime.isBefore(timespanLimit.getStart())){
				return true;
			}
		}

		return false;
	}

	protected void setTxsCount() {				
		txsCount = client.getTxsCount(walletName);
	}

	protected void setTxsCountIfItHasNotBeenSet() {
		if(txsCount == null){
			setTxsCount();
		}
	}

	protected void throwIllegalStateExceptionIfTxsCountHasNotBeenSet() {
		if(txsCount == 0){
			String errorMessage = "txsCount must be set before this function is called for it to return correctly!";
			throw new IllegalStateException(errorMessage);
		}
	}

	/**Intermittent networking errors can be encountered while querying the API. 
	 * These are intentionally suppressed unless MAX_CONNECTION_ATTEMPTS exceptions are thrown in a row.  
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	protected JSONObject tryToGetQueryResponse(MultivaluedMapImpl queryParams) throws Exception {
		Exception lastException = null;
		for(int i = 0; i < MAX_CONNECTION_ATTEMPTS; i++){
			try{
				return tryToGetQueryResponseAttempt(queryParams);
			} catch(Exception e){
				//intentionally ignoring exception unless it is on the final attempt.
				lastException = e;				 
			}
		}

		throw lastException;
	}

	protected JSONObject tryToGetQueryResponseAttempt(MultivaluedMapImpl queryParams) throws Exception {		
		return client.sendQuery(queryParams,walletName);
	}

	protected JSONObject tryToGetQueryResponseOrThrowRuntimeException(MultivaluedMapImpl queryParams) {
		try{
			JSONObject latestQueryJsonObject = tryToGetQueryResponse(queryParams);
			return latestQueryJsonObject;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}