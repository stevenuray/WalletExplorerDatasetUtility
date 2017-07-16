package net.stevenuray.walletexplorer.mongodb.converters.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestWalletTransactions;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction.TransactionDirection;

import org.bson.Document;
import org.joda.time.DateTime;
import org.junit.Test;

public class WalletTransactionDocumentConverterTest {
	private WalletTransactionDocumentConverter converter = new WalletTransactionDocumentConverter();
	
	@Test
	public void roundTripsWithoutException() {
		getWalletTransactionFromRoundtrip();			
	}
	
	@Test
	public void roundTripsTransactionDirectionCorrectly(){
		//Setup
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		
		//Exercise
		Document walletTransactionDocument = converter.to(walletTransaction);	
		WalletTransaction walletTransactionFromDocument = converter.from(walletTransactionDocument);
		
		//Verify
		TransactionDirection originalDirection = walletTransaction.getTransactionDirection();
		TransactionDirection convertedDirection = walletTransactionFromDocument.getTransactionDirection();
		assertEquals(originalDirection,convertedDirection);	
	}
	
	@Test
	public void roundTripsTransactionTimeCorrectly(){
		//Setup
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		
		//Exercise
		Document walletTransactionDocument = converter.to(walletTransaction);	
		WalletTransaction walletTransactionFromDocument = converter.from(walletTransactionDocument);
		
		//Verify
		DateTime originalTime = walletTransaction.getTransactionTime();
		DateTime convertedTime = walletTransactionFromDocument.getTransactionTime();
		assertEquals(originalTime,convertedTime);
	}
	
	@Test
	public void roundTripsTransactionIdCorrectly(){
		//Setup
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		
		//Exercise
		Document walletTransactionDocument = converter.to(walletTransaction);	
		WalletTransaction walletTransactionFromDocument = converter.from(walletTransactionDocument);
		
		//Verify
		String originalId = walletTransaction.getTxid();
		String convertedId = walletTransactionFromDocument.getTxid();
		assertEquals(originalId,convertedId);
	}
	
	@Test
	public void roundTripsBalanceCorrectly(){
		//Setup
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		
		//Exercise
		Document walletTransactionDocument = converter.to(walletTransaction);	
		WalletTransaction walletTransactionFromDocument = converter.from(walletTransactionDocument);
		
		//Verify			
		int scale = 10;
		RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		BigDecimal originalBalance = new BigDecimal(walletTransaction.getBalance());
		BigDecimal convertedBalance = new BigDecimal(walletTransactionFromDocument.getBalance());
		
		BigDecimal originalBalanceRounded = originalBalance.setScale(scale, roundingMode);		
		BigDecimal convertedBalanceRounded = convertedBalance.setScale(scale, roundingMode);
		assertEquals(originalBalanceRounded,convertedBalanceRounded);
	}

	private WalletTransaction getWalletTransactionFromRoundtrip(){	
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();				
		Document walletTransactionDocument = converter.to(walletTransaction);	
		WalletTransaction walletTransactionFromDocument = converter.from(walletTransactionDocument);
		return walletTransactionFromDocument;
	}	
}