package net.stevenuray.walletexplorer.persistence;

public interface WalletNameDataPipelineFactory<T,U> {
	public DataPipeline<T, U> getDataPipeline(String walletName);
}