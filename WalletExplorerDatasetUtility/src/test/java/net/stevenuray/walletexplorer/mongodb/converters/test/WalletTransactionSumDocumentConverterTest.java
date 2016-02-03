package net.stevenuray.walletexplorer.mongodb.converters.test;

import static org.junit.Assert.*;
import net.stevenuray.walletexplorer.aggregation.WalletTransactionSum;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionSumDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestWalletTransactionSums;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

public class WalletTransactionSumDocumentConverterTest {
	private WalletTransactionSumDocumentConverter converter = new WalletTransactionSumDocumentConverter();
	
	@Test
	public void returnsNonNullDocument() {
		//Setup
		WalletTransactionSum walletTransactionSum = TestWalletTransactionSums.getWalletTransactionSum();
		
		//Exercise
		Document walletTransactionSumDocument = converter.to(walletTransactionSum);
		
		//Verify
		assertNotNull(walletTransactionSumDocument);		
	}

	@Test
	public void returnsNonNullObject() {
		//Setup
		WalletTransactionSum walletTransactionSum = TestWalletTransactionSums.getWalletTransactionSum();
		
		//Exercise
		Document walletTransactionSumDocument = converter.to(walletTransactionSum);
		WalletTransactionSum walletTransactionSumFromDocument = converter.from(walletTransactionSumDocument);
		
		//Verify
		assertNotNull(walletTransactionSumFromDocument);		
	}
	
	@Test
	public void roundTripsWithoutException() {
		//Setup
		WalletTransactionSum walletTransactionSum = TestWalletTransactionSums.getWalletTransactionSum();
		
		//Exercise + Verify
		Document walletTransactionSumDocument = converter.to(walletTransactionSum);
		@SuppressWarnings("unused")
		WalletTransactionSum walletTransactionSumFromDocument = converter.from(walletTransactionSumDocument);		
	}
	
	@Before
	public void setUp() throws Exception {
	}
}