package net.stevenuray.walletexplorer.downloader;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.joda.time.DateTime;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**One WalletExplorerQuerier instance should be used per download of a wallet. Re-use is not intended. 
 * 
 * Note multiple instances of WalletExplorerQuerier should not be used concurrently. 
 * Ales Janda (Creator of WalletExplorer) has requested users of his API limit their use to one thread at a time 
 * in May of 2015. 
 * @author Steven Uray 
 */
public class WalletExplorerQuerier{	
	private static final int MAX_CONNECTION_ATTEMPTS = 3;
	private static final int MAX_TRANSACTIONS_PER_QUERY = 100;	
	private boolean downloading = true; 
	private DateTime endTime;
	private int queryFromCount = 0;
	private int txsCount;
	private final String walletName;
	
	/**Queries for transactions regardless of their time. 	 
	 * @param walletName
	 */
	public WalletExplorerQuerier(String walletName){
		this.walletName = walletName;
		this.endTime = new DateTime(0);				
	}
	
	/**Does not query for, or return, transactions before a given time. Useful for non-redundant downloading.	 
	 * @param walletName
	 * @param endTime
	 */
	public WalletExplorerQuerier(String walletName,DateTime endTime){
		this.walletName = walletName;
		this.endTime = endTime;		
	}
	
	public List<WalletTransaction> getNextWalletTransactions() throws Exception{
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,queryFromCount);			
		JSONObject latestQueryJsonObject = tryToGetQueryResponse(queryParams);	
		updateCountersAfterResponse(latestQueryJsonObject);
		List<WalletTransaction> walletTransactions = convertWalletTransactionsFromResponse(latestQueryJsonObject);
		downloading = shouldContinueDownloading(latestQueryJsonObject);
		return walletTransactions;
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
	
	private JSONArray getSubWalletTransactionsFromResponse(JSONObject latestQueryJsonObject){
		JSONArray subWalletTransactions = (JSONArray) latestQueryJsonObject.get("txs");
		return subWalletTransactions;
	}
	
	private MultivaluedMapImpl getWalletExplorerQueryParams(String walletName,int fromCount){		 
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
		
		/*Note: This loop works in every case except one, if transactions for a wallet name
		 * are not fully downloaded on their initial run, they will never be downloaded!
		 * A quick solution to this is to delete the collection if there is an error and hope
		 * it downloads properly on the next run. 
		 */			
		for(int k = 0; k < subWalletTransactions.size(); k++){
			/*Breaking early if we already have transactions earlier than the current transaction.
			 * Note: As of 2015-10-12 WalletExplorer API returns transactions in descending time only!
			 */
			JSONObject subWalletTransaction = (JSONObject) subWalletTransactions.get(k);			
			long unixTimestamp = subWalletTransaction.getLong("time");
			DateTime currentTime = new DateTime(unixTimestamp*1000);			
			if(currentTime.isBefore(endTime)){					
				return true;
			}
		}
		
		return false;
	}
		
	private boolean shouldContinueDownloading(JSONObject latestQueryJsonObject){		
		if(isDownloadComplete()){
			return false;
		}
		
		if(isLatestResponseAfterEndTime(latestQueryJsonObject)){
			return false; 
		}
			
		return true;
	}
	
	private void throwWalletNotFoundExceptionIfNecessary(String walletName,JSONObject responseJson) 
			throws WalletNotFoundException{
		if (!Boolean.valueOf(responseJson.getString("found"))) {
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
		throwWalletNotFoundExceptionIfNecessary(walletName,responseJson);
		return responseJson;
	}
	
	private int tryToGetWalletTransactionsCount() throws Exception{
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,0);		
		JSONObject responseJson = tryToGetQueryResponse(queryParams);						
		int txsCount = getWalletTransactionsCount(responseJson);
		return txsCount;
	}

	private void updateCountersAfterResponse(JSONObject latestQueryJsonObject){
		queryFromCount += MAX_TRANSACTIONS_PER_QUERY;
		txsCount = getWalletTransactionsCount(latestQueryJsonObject);		
	}

	public DateTime getEarliestTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public DateTime getLatestTime() {
		// TODO Auto-generated method stub
		return null;
	}
}