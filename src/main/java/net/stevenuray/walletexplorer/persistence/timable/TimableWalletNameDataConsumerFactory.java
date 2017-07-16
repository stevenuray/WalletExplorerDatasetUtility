package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.walletdatafactories.WalletNameDataConsumerFactory;

public interface TimableWalletNameDataConsumerFactory<T> extends WalletNameDataConsumerFactory<T> {
	public TimableDataConsumer<T> getDataConsumer(String walletName);
}
