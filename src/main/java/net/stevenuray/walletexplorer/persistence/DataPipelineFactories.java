package net.stevenuray.walletexplorer.persistence;

import net.stevenuray.walletexplorer.persistence.timable.BasicTimableWalletNameDataFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataConsumerFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

/**Factory that creates WalletNameDataPipelineFactory objects. Could also be described as a factory of factories.
 * Used to make selection of data sources and destinations easier for main or high level classes. 
 * @author Steven Uray 
 */
public class DataPipelineFactories {	
	public static BasicTimableWalletNameDataFactory<WalletTransaction, WalletTransaction> 
		getWalletExplorerToMongoDB() {
		TimableWalletNameDataConsumerFactory<WalletTransaction> consumer = DataPipelines.getMongoDBConsumer();
		TimableWalletNameDataProducerFactory<WalletTransaction> producer = DataPipelines.getWalletExplorerProducer();
		return new BasicTimableWalletNameDataFactory<WalletTransaction,WalletTransaction>(producer,consumer);
	}	
	 
	public static BasicTimableWalletNameDataFactory<WalletTransaction,ConvertedWalletTransaction> 
		getMongoDBToConvertedMongoDB(){
		TimableWalletNameDataProducerFactory<WalletTransaction> producer = DataPipelines.getMongoDBProducer();
		TimableWalletNameDataConsumerFactory<ConvertedWalletTransaction> consumer = 
				DataPipelines.getMongoDBConvertedConsumer();
		return new BasicTimableWalletNameDataFactory<WalletTransaction,ConvertedWalletTransaction>(producer,consumer);		
	}	
}