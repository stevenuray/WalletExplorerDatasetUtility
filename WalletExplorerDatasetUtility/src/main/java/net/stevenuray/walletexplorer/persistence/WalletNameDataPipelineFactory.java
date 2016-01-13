package net.stevenuray.walletexplorer.persistence;

public interface WalletNameDataPipelineFactory<T,U> {
	public DataPipeline<T, U> getProducerConsumerPair(String walletName);
}