package net.stevenuray.walletexplorer.downloader.test;

import net.sf.json.JSONObject;
import net.stevenuray.walletexplorer.downloader.WalletTransactionJSONConverter;
import net.stevenuray.walletexplorer.testobjects.TestWalletExplorerResponses;




import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.junit.Test;

public class WalletTransactionJSONConverterTest {
	private WalletTransactionJSONConverter converter = new WalletTransactionJSONConverter();
	
	@Test
	public void roundTripsFromJSONWithoutException() {
		JSONObject firstJsonObject = TestWalletExplorerResponses.getWalletTransactionJsonObject();
		WalletTransaction walletTransaction = converter.from(firstJsonObject);
		@SuppressWarnings("unused")
		JSONObject roundTripJsonObject = converter.to(walletTransaction);
		//If we got here without an exception, the test should pass. 
	}

}
