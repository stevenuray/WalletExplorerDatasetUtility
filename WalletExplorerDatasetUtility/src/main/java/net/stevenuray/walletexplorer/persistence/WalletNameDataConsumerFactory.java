package net.stevenuray.walletexplorer.persistence;

public interface WalletNameDataConsumerFactory<T> {
	public DataConsumer<T> getDataConsumer(String walletName);
}
