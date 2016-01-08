package net.stevenuray.walletexplorer.testobjects;

import java.util.Collection;

import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction.TransactionDirection;

import org.joda.time.DateTime;

public class TestWalletTransactions {
	public static WalletTransaction getWalletTransaction(){
		DateTime transactionTime = new DateTime(2015,12,19,1,2,33);
		String transactionId = "cbf56b2214f2a21dcdce1e3f91eea2418eddf49bf2fc5cdd4fa698ffc76fd6ed";
		double walletBalance = 30.26550187;
		TransactionDirection transactionDirection = TransactionDirection.RECEIVED;
		Collection<TransactionOutput> transactionOutputs = TestTransactionOutputs.getOutputs();
		WalletTransaction walletTransaction = new WalletTransaction(
				transactionTime, transactionId, walletBalance, transactionDirection, transactionOutputs);
		return walletTransaction;
	}
}
