package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.WalletNameDataPipelineFactory;

public interface TimableWalletNameDataPipelineFactory<T,U> extends WalletNameDataPipelineFactory<T,U> {
	public TimableDataPipeline<T, U> getDataPipeline(String walletName);
}