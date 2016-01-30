package net.stevenuray.walletexplorer.testobjects;

import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.downloader.WalletTransactionJSONConverter;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;


public class TestWalletExplorerResponses {
	public static JSONObject getWalletTransactionJsonObject(){		
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		Converter<WalletTransaction,JSONObject> testConverter = new WalletTransactionJSONConverter();
		JSONObject json = testConverter.to(walletTransaction);
		return json;
	}
}
