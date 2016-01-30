package net.stevenuray.walletexplorer.persistence;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.downloader.WalletExplorerQuerierFactory;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.mongodb.MongoDBConsumerFactory;
import net.stevenuray.walletexplorer.mongodb.MongoDBProducerFactory;
import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataConsumerFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.bson.Document;

/**Contains both data producers and data consumers for the entire WalletExplorerUtility system. 
 * New data producers and data consumers should be added here after their implementation is complete. 
 * @author Steven Uray
 */
public class DataPipelines {	
	//TODO mongodb non-converted producer. 
	
	public static TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> getMongoDBConvertedProducer(){
		Converter<ConvertedWalletTransaction,Document> converter = new ConvertedWalletTransactionDocumentConverter();
		MongoDBProducerFactory<ConvertedWalletTransaction> factory = 
				new MongoDBProducerFactory<ConvertedWalletTransaction>(converter);
		return factory;
	}
	
	public static TimableWalletNameDataConsumerFactory<WalletTransaction> getMongoDBConsumer() {
		Converter<WalletTransaction,Document> converter = new WalletTransactionDocumentConverter();
		MongoDBConsumerFactory<WalletTransaction> factory = new MongoDBConsumerFactory<WalletTransaction>(converter);
		return factory;
	}
	
	public static TimableWalletNameDataProducerFactory<WalletTransaction> getWalletExplorerProducer() {
		int maxQueueLength = WalletExplorerConfig.MAX_QUEUE_LENGTH;
		TimableWalletNameDataProducerFactory<WalletTransaction> factory = new WalletExplorerQuerierFactory(maxQueueLength);
		return factory;
	}
}