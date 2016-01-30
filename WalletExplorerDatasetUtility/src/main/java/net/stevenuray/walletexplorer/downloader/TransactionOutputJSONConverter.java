package net.stevenuray.walletexplorer.downloader;

import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;

public class TransactionOutputJSONConverter implements Converter<TransactionOutput,JSONObject>{

	public JSONObject to(TransactionOutput transactionOutput) {
		JSONObject jsonObject = new JSONObject();
		double amount = transactionOutput.getAmount();
		String wallet_id = transactionOutput.getWalletId();
		jsonObject.put("amount",amount);
		jsonObject.put("wallet_id",wallet_id);
		return jsonObject;
	}

	public TransactionOutput from(JSONObject jsonObject) {
		double amount = jsonObject.getDouble("amount");
		String wallet_id = jsonObject.getString("wallet_id");
		TransactionOutput transactionOutput = new TransactionOutput(amount,wallet_id);
		return transactionOutput;
	}	
}