package net.stevenuray.walletexplorer.downloader;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**One WalletExplorerQuerier instance should be used per download of a wallet. Re-use is not intended. 
 * 
 * Note multiple instances of WalletExplorerQuerier should not be used concurrently. 
 * Ales Janda (Creator of WalletExplorer) requested users of his API limit their use to one thread at a time 
 * in May of 2015. 
 * @author Steven Uray 
 */
//TODO break this class down into smaller classes.
public class WalletExplorerQuerier{	
	private static final int FROM_COUNT_MANDATORY_DIVISIBLE = 100;
	private static final int MAX_CONNECTION_ATTEMPTS = 3;	
	private static final int MAX_TRANSACTIONS_PER_QUERY = 100;
	private boolean downloading = true; 
	private Interval timespanLimit;
	private long queryFromCount = 0;
	private long txsCount;
	private final String walletName;
		
	/**Does not query for, or return, transactions before a given time. Useful for non-redundant downloading.	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned.  
	 */
	public WalletExplorerQuerier(String walletName,Interval timespanLimit){
		this.walletName = walletName;
		this.timespanLimit = timespanLimit;		
	}
	
	public DateTime getEarliestTime() {
		setTxsCountIfItHasNotBeenSet();		
		long latestFromCount = getLastValidTransactionFromCount();
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,latestFromCount);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams);		
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		int lastValueIndex = subWalletTransactions.size()-1;
		JSONObject firstSubWalletTransaction = (JSONObject) subWalletTransactions.get(lastValueIndex);
		DateTime earliestTime = getTimeOfSubWalletTransaction(firstSubWalletTransaction);
		return earliestTime;				
	}
	
	public DateTime getLatestTime() {		
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,0l);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams);		
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		JSONObject firstSubWalletTransaction = (JSONObject) subWalletTransactions.get(0);
		DateTime latestTime = getTimeOfSubWalletTransaction(firstSubWalletTransaction);
		return latestTime;			
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
	
	public boolean isDownloading() {
		return downloading;
	}
	
	private List<WalletTransaction> convertWalletTransactions(JSONArray subWalletTransactions) 
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
	
	private List<WalletTransaction> convertWalletTransactionsFromResponse(JSONObject latestQueryJsonObject) 
			throws InterruptedException{
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		List<WalletTransaction> walletTransactions = convertWalletTransactions(subWalletTransactions);	
		return walletTransactions;
	}
	
	private ClientResponse getClientResponse(MultivaluedMapImpl queryParams){
		WebResource webResource = getWalletExplorerWebResource();
		ClientResponse response = 
				webResource.queryParams(queryParams).type("application/json").get(ClientResponse.class);
		return response;
	}

	private JSONObject getJSONObjectFromWalletExplorerResponse(ClientResponse response){
		String jsonStr = response.getEntity(String.class);
		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonStr);
		return json;
	}
	
	private long getLastTxsCountRemainder(){
		return txsCount % FROM_COUNT_MANDATORY_DIVISIBLE;
	}
	
	/**WalletExplorer.com API only accepts fromCount parameters that are divisible by 100. 
	 * Note: txsCount must be set before this function is called for it to return correctly. 
	 * @return
	 */
	private long getLastValidTransactionFromCount(){
		throwIllegalStateExceptionIfTxsCountHasNotBeenSet();
		long remainder = getLastTxsCountRemainder();
		long lastValidTransactionFromCount = txsCount - remainder;
		return lastValidTransactionFromCount;
	}
	
	private JSONArray getSubWalletTransactionsFromResponse(JSONObject latestQueryJsonObject){
		JSONArray subWalletTransactions = (JSONArray) latestQueryJsonObject.get("txs");
		return subWalletTransactions;
	}
	
	private DateTime getTimeOfSubWalletTransaction(JSONObject subWalletTransaction){
		long unixTimestamp = subWalletTransaction.getLong("time");
		DateTime subwalletTransactionTime = new DateTime(unixTimestamp*1000);
		return subwalletTransactionTime;
	}
	
	private MultivaluedMapImpl getWalletExplorerQueryParams(String walletName,long fromCount){		 
		String caller = WalletExplorerAPIConfigSingleton.CALLER;
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams = new MultivaluedMapImpl();
		queryParams.add("wallet", walletName);
		queryParams.add("from", fromCount);
		queryParams.add("count", MAX_TRANSACTIONS_PER_QUERY);
		queryParams.add("caller", caller);
		return queryParams;
	}
		
	private WebResource getWalletExplorerWebResource(){
		Client client = Client.create();		
		String walletExplorerUrl = WalletExplorerAPIConfigSingleton.API_URL;
		WebResource webResource = client.resource(walletExplorerUrl);
		return webResource;
	}
	
	private int getWalletTransactionsCount(JSONObject responseJson){							
		int txsCount = responseJson.getInt("txs_count");
		return txsCount;
	}
	
	private boolean isDownloadComplete(){
		if(queryFromCount >= txsCount){
			return true;
		} else{
			return false;
		}
	}
	
	private boolean isLatestResponseAfterEndTime(JSONObject latestQueryJsonObject){
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
		
	private boolean isLatestResponseEqualToOrAfterStartTime(JSONObject latestQueryJsonObject){
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
	
	private boolean isLatestResponseEqualToOrBeforeStartTime(JSONObject latestQueryJsonObject){
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
	
	private void setTxsCount() {
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,0);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams);		
		txsCount = getWalletTransactionsCount(latestQueryJsonObject);
	}
		
	private void setTxsCountIfItHasNotBeenSet(){
		if(txsCount == 0){
			setTxsCount();
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
		
	private void throwIllegalStateExceptionIfTxsCountHasNotBeenSet(){
		if(txsCount == 0){
			String errorMessage = "txsCount must be set before this function is called for it to return correctly!";
			throw new IllegalStateException(errorMessage);
		}
	}
	
	private void throwWalletNotFoundExceptionIfNecessary(JSONObject responseJson) 
			throws WalletNotFoundException{
		if (!Boolean.valueOf(responseJson.getString("found"))) {
			@SuppressWarnings("unused")
			String errorMessage = "Wallet " + walletName + " not found";
			//TODO use errorMessage in a log somehow
			throw new WalletNotFoundException();
		}
		return; 
	}
	
	/**Intermittent networking errors can be encountered while querying the API. 
	 * These are intentionally suppressed unless MAX_CONNECTION_ATTEMPTS exceptions are thrown in a row.  
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	private JSONObject tryToGetQueryResponse(MultivaluedMapImpl queryParams) throws Exception{
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

	private JSONObject tryToGetQueryResponseAttempt(MultivaluedMapImpl queryParams)throws Exception{
		ClientResponse response = getClientResponse(queryParams);
		JSONObject responseJson = getJSONObjectFromWalletExplorerResponse(response);
		throwWalletNotFoundExceptionIfNecessary(responseJson);
		return responseJson;
	}
	
	private JSONObject tryToGetQueryResponseOrThrowRuntimeException(MultivaluedMapImpl queryParams){
		try{
			JSONObject latestQueryJsonObject = tryToGetQueryResponse(queryParams);
			return latestQueryJsonObject;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private void updateCountersAfterResponse(JSONObject latestQueryJsonObject){
		queryFromCount += MAX_TRANSACTIONS_PER_QUERY;
		txsCount = getWalletTransactionsCount(latestQueryJsonObject);		
	}
}