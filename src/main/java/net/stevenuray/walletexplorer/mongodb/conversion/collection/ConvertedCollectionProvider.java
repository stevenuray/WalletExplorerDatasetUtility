package net.stevenuray.walletexplorer.mongodb.conversion.collection;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;

public interface ConvertedCollectionProvider {
	WalletCollection getConvertedCollection(WalletCollection unconvertedCollection, String currencySymbol);
}
