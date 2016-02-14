package net.stevenuray.walletexplorer.persistence.walletdatafactories;

import net.stevenuray.walletexplorer.persistence.DataProducer;

public interface WalletNameDataProducerFactory<T> {
	public DataProducer<T> getDataProducer(String walletName);	
}
