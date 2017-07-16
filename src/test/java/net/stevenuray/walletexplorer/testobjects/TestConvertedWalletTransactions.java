package net.stevenuray.walletexplorer.testobjects;

import java.util.Collection;

import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class TestConvertedWalletTransactions {
	public static ConvertedWalletTransaction getConvertedWalletTransaction(){
		WalletTransaction walletTransaction = TestWalletTransactions.getWalletTransaction();
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = 
				TestConvertedWalletTransactionOutputs.getConvertedTransactionOutputs();
		ConvertedWalletTransaction convertedWalletTransaction = 
				new ConvertedWalletTransaction(walletTransaction,convertedOutputs);
		return convertedWalletTransaction;
	}
}