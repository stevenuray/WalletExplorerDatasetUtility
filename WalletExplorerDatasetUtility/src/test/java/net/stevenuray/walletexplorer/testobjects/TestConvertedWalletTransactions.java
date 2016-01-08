package net.stevenuray.walletexplorer.testobjects;

import java.util.Collection;

import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

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