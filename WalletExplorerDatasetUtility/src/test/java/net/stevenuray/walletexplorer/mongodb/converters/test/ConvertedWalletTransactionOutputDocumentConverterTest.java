package net.stevenuray.walletexplorer.mongodb.converters.test;

import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionOutputDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestConvertedWalletTransactionOutputs;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;

import org.bson.Document;
import org.junit.Test;

public class ConvertedWalletTransactionOutputDocumentConverterTest {
	private final ConvertedWalletTransactionOutputDocumentConverter converter = 
			new ConvertedWalletTransactionOutputDocumentConverter();
	@Test
	public void roundTripsWithoutException() {
		//Setup
		ConvertedWalletTransactionOutput output = 
				TestConvertedWalletTransactionOutputs.getConvertedTransactionOutput();
		
		//Exercise + Verify
		Document outputDocument = converter.to(output);
		@SuppressWarnings("unused")
		ConvertedWalletTransactionOutput outputFromDocument = converter.from(outputDocument);	
	}
}