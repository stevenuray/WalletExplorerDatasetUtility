package net.stevenuray.walletexplorer.downloader;

import org.joda.time.DateTime;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class WalletExplorerClient {
	public static final int MAX_TRANSACTIONS_PER_QUERY = 100;
	protected static final int MAX_CONNECTION_ATTEMPTS = 3;
	
	public DateTime getEarliestTime(String walletName) {			
		long latestFromCount = getTxsCount(walletName);
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,latestFromCount);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams,walletName);		
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject);
		int lastValueIndex = subWalletTransactions.size()-1;
		JSONObject firstSubWalletTransaction = (JSONObject) subWalletTransactions.get(lastValueIndex);
		DateTime earliestTime = getTimeOfSubWalletTransaction(firstSubWalletTransaction);
		return earliestTime;				
	} 
	 
	public DateTime getLatestTime(String walletName) {		
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,0l);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams,walletName);		
		JSONArray subWalletTransactions = getSubWalletTransactionsFromResponse(latestQueryJsonObject); 
		JSONObject firstSubWalletTransaction = (JSONObject) subWalletTransactions.get(0);
		DateTime latestTime = getTimeOfSubWalletTransaction(firstSubWalletTransaction);
		return latestTime;			
	}
	
	public long getTxsCount(String walletName) {	
		MultivaluedMapImpl queryParams = getWalletExplorerQueryParams(walletName,0);	
		JSONObject latestQueryJsonObject = tryToGetQueryResponseOrThrowRuntimeException(queryParams,walletName);		
		return getWalletTransactionsCount(latestQueryJsonObject);		
	}

	public JSONObject sendQuery(MultivaluedMapImpl queryParams, String walletName){
		ClientResponse response = getClientResponse(queryParams);
		JSONObject responseJson = getJSONObjectFromWalletExplorerResponseOrThrowException(response);
		throwWalletNotFoundExceptionIfNecessary(responseJson,walletName);
		return responseJson;
	}
	
	private ClientResponse getClientResponse(MultivaluedMapImpl queryParams) {
		WebResource webResource = getWalletExplorerWebResource();
		ClientResponse response = 
				webResource.queryParams(queryParams).type("application/json").get(ClientResponse.class);
		return response;
	}
	
	private JSONObject getJSONObjectFromWalletExplorerResponseOrThrowException(ClientResponse response) {
		String jsonStr = response.getEntity(String.class);
		try{			
			JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonStr);
			return json;
		} catch(JSONException e){
			System.err.println("Could not make json string from: "+jsonStr);
			throw e;
		}
	}
	
	private WebResource getWalletExplorerWebResource() {
		Client client = Client.create();		
		String walletExplorerUrl = WalletExplorerAPIConfigSingleton.API_URL;
		WebResource webResource = client.resource(walletExplorerUrl);
		return webResource;
	}
	
	private int getWalletTransactionsCount(JSONObject responseJson) {							
		int txsCount = responseJson.getInt("txs_count");
		return txsCount;
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
	
	protected void throwWalletNotFoundExceptionIfNecessary(JSONObject responseJson, String walletName)
			throws WalletNotFoundException {
		if (!Boolean.valueOf(responseJson.getString("found"))) {			
			String errorMessage = "Wallet " + walletName + " not found";			
			throw new WalletNotFoundException(errorMessage);
		}
		return; 
	}
	
	/**Intermittent networking errors can be encountered while querying the API. 
	 * These are intentionally suppressed unless MAX_CONNECTION_ATTEMPTS exceptions are thrown in a row.  
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	protected JSONObject tryToGetQueryResponse(MultivaluedMapImpl queryParams,String walletName) throws Exception {
		Exception lastException = null;
		for(int i = 0; i < MAX_CONNECTION_ATTEMPTS; i++){
			try{
				return sendQuery(queryParams,walletName);
			} catch(Exception e){
				//intentionally ignoring exception unless it is on the final attempt.
				lastException = e;				 
			}
		}
		
		throw lastException;
	}
	
	protected JSONObject tryToGetQueryResponseOrThrowRuntimeException(
			MultivaluedMapImpl queryParams,String walletName) {
		try{
			JSONObject latestQueryJsonObject = tryToGetQueryResponse(queryParams,walletName);
			return latestQueryJsonObject;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}