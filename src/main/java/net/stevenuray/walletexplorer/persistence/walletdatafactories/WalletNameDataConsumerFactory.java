package net.stevenuray.walletexplorer.persistence.walletdatafactories;

import net.stevenuray.walletexplorer.persistence.DataConsumer;

public interface WalletNameDataConsumerFactory<T> {
	public DataConsumer<T> getDataConsumer(String walletName);
}