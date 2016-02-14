package net.stevenuray.walletexplorer.persistence.walletdatafactories;

import net.stevenuray.walletexplorer.persistence.DataPipeline;

public interface WalletNameDataPipelineFactory<T,U> {
	public DataPipeline<T, U> getDataPipeline(String walletName);
}