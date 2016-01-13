package net.stevenuray.walletexplorer.persistence;

import net.stevenuray.walletexplorer.persistence.timable.BasicTimableWalletNameDataPipelineFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataConsumerFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

/**Factory that creates WalletNameDataPipelineFactory objects. Aka factory of factories.
 * Used to make selection of data sources and destinations easier for main or high level classes. 
 * @author Steven Uray 
 */
public class DataPipelineFactories {	
	public static BasicTimableWalletNameDataPipelineFactory<WalletTransaction, WalletTransaction> 
		getWalletExplorerToMongoDB() {
		TimableWalletNameDataConsumerFactory<WalletTransaction> consumerFactory = DataPipelines.getMongoDBConsumer();
		TimableWalletNameDataProducerFactory<WalletTransaction> producerFactory = 
				DataPipelines.getWalletExplorerProducer();
		return new BasicTimableWalletNameDataPipelineFactory<WalletTransaction,WalletTransaction>(
				producerFactory,consumerFactory);
	}
	
	/*TODO continue implementing once producer factory has been set up for MongoDB.	 
	public static BasicTimableWalletNameDataPipelineFactory<WalletTransaction,ConvertedWalletTransaction> 
		getMongoDBRoundTrip(){
		TimableWalletNameDataConsumerFactory<WalletTransaction> consumerFactory = DataPipelines.getMongoDBConsumer();
		
	}
	*/
}