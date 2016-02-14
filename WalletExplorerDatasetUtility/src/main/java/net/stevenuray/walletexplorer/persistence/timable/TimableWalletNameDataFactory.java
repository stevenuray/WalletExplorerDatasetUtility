package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.walletdatafactories.WalletNameDataPipelineFactory;

/**Builds a TimableDataPipeline given a wallet name from WalletExplorer.com. 
 * More specifically, given a wallet name, will produce a data pipeline with both a data source
 * and a data destination for that wallet name. The source could be a database, WalletExplorer.com itself, 
 * etc. The destination could be a database, or any other thing that consumes data. 
 * @author Steven Uray 
 */
public interface TimableWalletNameDataFactory<T,U> extends WalletNameDataPipelineFactory<T,U> {
	public TimableDataPipeline<T, U> getDataPipeline(String walletName);
}