package net.stevenuray.walletexplorer.mongodb.converters.test;

import net.stevenuray.walletexplorer.mongodb.converters.TransactionOutputDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestTransactionOutputs;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;

import org.bson.Document;
import org.junit.Test;

public class TransactionOutputDocumentConverterTest {
	private final TransactionOutputDocumentConverter converter = new TransactionOutputDocumentConverter();
	
	@Test
	public void roundTripsWithoutException() {
		//Setup
		TransactionOutput transactionOutput = TestTransactionOutputs.getOutput();
		
		//Exercise + Verify
		Document transactionOutputDocument = converter.to(transactionOutput);
		@SuppressWarnings("unused")
		TransactionOutput transactionOutputFromDocument = converter.from(transactionOutputDocument);
	}
}
