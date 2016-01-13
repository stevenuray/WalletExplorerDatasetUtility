package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.WalletNameDataConsumerFactory;

public interface TimableWalletNameDataConsumerFactory<T> extends WalletNameDataConsumerFactory<T> {
	public TimableDataConsumer<T> getDataConsumer(String walletName);
}
