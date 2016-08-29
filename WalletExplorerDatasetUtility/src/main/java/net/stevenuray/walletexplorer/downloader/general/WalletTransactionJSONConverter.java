package net.stevenuray.walletexplorer.downloader.general;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction.TransactionDirection;

public class WalletTransactionJSONConverter implements Converter<WalletTransaction,JSONObject>{
	private Converter<TransactionOutput,JSONObject> transactionOutputConverter = new TransactionOutputJSONConverter();
	
	public WalletTransaction from(JSONObject jsonObject) {
		DateTime transactionTime = getTransactionTime(jsonObject);		
		String txid = jsonObject.getString("txid");
		double balance = jsonObject.getDouble("balance");
		TransactionDirection transactionDirection = getTransactionDirection(jsonObject);		
		List<TransactionOutput> transactionOutputs = getTransactionOutputs(jsonObject);		
		WalletTransaction walletTransaction = 
				new WalletTransaction(transactionTime,txid,balance,transactionDirection,transactionOutputs);
		return walletTransaction;
	}

	public JSONObject to(WalletTransaction walletTransaction) {
		JSONObject jsonObject = new JSONObject();
		long transactionTimestampInSeconds = getTransactionTimestampInSeconds(walletTransaction);
		jsonObject.put("time",transactionTimestampInSeconds);
		jsonObject.put("txid", walletTransaction.getTxid());
		jsonObject.put("balance", walletTransaction.getBalance());
		jsonObject.put("type", getTypeString(walletTransaction));
		JSONArray transactionOutputs = getTransactionOutputs(walletTransaction);
		jsonObject.put("outputs",transactionOutputs);
		return jsonObject;
	}

	private JSONArray getTransactionOutputs(WalletTransaction walletTransaction) {
		JSONArray transactionOutputsJSONArray = new JSONArray();
		Collection<TransactionOutput> transactionOutputs = walletTransaction.getWalletTransactionOutputsUnmodifiable();
		Iterator<TransactionOutput> transactionOutputIterator = transactionOutputs.iterator();
		while(transactionOutputIterator.hasNext()){
			TransactionOutput transactionOutput = transactionOutputIterator.next();
			JSONObject transactionOutputJson = transactionOutputConverter.to(transactionOutput);
			transactionOutputsJSONArray.add(transactionOutputJson);
		}
		return transactionOutputsJSONArray;
	}

	private String getTypeString(WalletTransaction walletTransaction) {
		TransactionDirection transactionDirection = walletTransaction.getTransactionDirection();
		if(transactionDirection.equals(TransactionDirection.RECEIVED)){
			return "received";
		}
		if(transactionDirection.equals(TransactionDirection.SENT)){
			return "sent";
		}
		throw new IllegalArgumentException("Could not find a type string for: "+transactionDirection);
	}

	private TransactionDirection getTransactionDirection(JSONObject jsonObject) {
		String transactionDirectionString = jsonObject.getString("type");
		if(transactionDirectionString.equals("received")){
			return TransactionDirection.RECEIVED;
		}		
		if(transactionDirectionString.equals("sent")){
			return TransactionDirection.SENT;
		}
		throw new IllegalArgumentException("Could not find a TransactionDirection for: "+transactionDirectionString);		
	}

	private List<TransactionOutput> getTransactionOutputs(JSONObject jsonObject) {
		List<TransactionOutput> transactionOutputs = new ArrayList<TransactionOutput>();
		if(jsonObject.has("outputs")){
			JSONArray outputArray = jsonObject.getJSONArray("outputs");
			for(int i = 0; i < outputArray.size(); i++){
				JSONObject transactionOutputJson = outputArray.getJSONObject(i);
				TransactionOutput transactionOutput = transactionOutputConverter.from(transactionOutputJson);
				transactionOutputs.add(transactionOutput);
			}		
			return transactionOutputs;
		} else{
			//Returning an empty list. 
			return transactionOutputs;
		}
	}

	private DateTime getTransactionTime(JSONObject jsonObject) {
		long unixTimestampMilliseconds = jsonObject.getLong("time") * 1000;
		DateTime transactionTime = new DateTime(unixTimestampMilliseconds);
		return transactionTime;
	}

	private long getTransactionTimestampInSeconds(WalletTransaction walletTransaction) {
		DateTime transactionTime = walletTransaction.getTransactionTime();
		long transactionUnixTimestampMilliseconds = transactionTime.getMillis();
		long transactionTimestampInSeconds = transactionUnixTimestampMilliseconds/1000;
		return transactionTimestampInSeconds;
	}
}