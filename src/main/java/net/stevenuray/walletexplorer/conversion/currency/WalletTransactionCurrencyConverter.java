package net.stevenuray.walletexplorer.conversion.currency;

import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public interface WalletTransactionCurrencyConverter {
	ConvertedWalletTransaction convert(WalletTransaction walletTransaction);
}
