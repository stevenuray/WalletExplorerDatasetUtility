package net.stevenuray.walletexplorer.persistence;

public interface WalletNameDataProducerFactory<T> {
	public DataProducer<T> getDataProducer(String walletName);	
}
