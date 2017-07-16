package net.stevenuray.walletexplorer.conversion.currency;

import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;

import org.joda.time.DateTime;

public interface TransactionOutputConverter {
	ConvertedWalletTransactionOutput convert(TransactionOutput transactionOutput,DateTime transactionTime);
}
