package net.stevenuray.walletexplorer.mongodb.converters.test;

import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestConvertedWalletTransactions;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.bson.Document;
import org.junit.Test;

public class ConvertedWalletTransactionDocumentConverterTest {
	private final ConvertedWalletTransactionDocumentConverter converter = 
			new ConvertedWalletTransactionDocumentConverter();
	
	/*TODO add test for when converted outputs come out of database as an ArrayList<Document>
	instead of the BsonArray they go into the database with. 
	*/
	
	@Test
	public void roundTripsWithoutException() {		
		//Setup
		ConvertedWalletTransaction transaction = TestConvertedWalletTransactions.getConvertedWalletTransaction();
				
		//Exercise + Verify
		Document transactionDocument = converter.to(transaction);
		@SuppressWarnings("unused")
		ConvertedWalletTransaction transactionFromDocument = converter.from(transactionDocument);			
	}
}
