package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.WalletNameDataProducerFactory;

public interface TimableWalletNameDataProducerFactory<T> extends WalletNameDataProducerFactory<T>{
	public TimableDataProducer<T> getDataProducer(String walletName);
}