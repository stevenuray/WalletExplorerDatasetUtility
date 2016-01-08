package net.stevenuray.walletexplorer.conversion.collection;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;

public interface ConvertedCollectionProvider {
	WalletCollection getConvertedCollection(WalletCollection unconvertedCollection, String currencySymbol);
}
