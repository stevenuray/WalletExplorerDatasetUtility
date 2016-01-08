package net.stevenuray.walletexplorer.conversion.currency;

import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

public interface WalletTransactionCurrencyConverter {
	ConvertedWalletTransaction convert(WalletTransaction walletTransaction);
}
