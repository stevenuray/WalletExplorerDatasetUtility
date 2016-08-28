package net.stevenuray.walletexplorer.persistence;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.downloader.WalletExplorerQuerierFactory;
import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.mongodb.datapipeline.factories.MongoDBConsumerFactory;
import net.stevenuray.walletexplorer.mongodb.datapipeline.factories.MongoDBConvertedTransactionConsumerFactory;
import net.stevenuray.walletexplorer.mongodb.datapipeline.factories.MongoDBConvertedTransactionProducerFactory;
import net.stevenuray.walletexplorer.mongodb.datapipeline.factories.MongoDBProducerFactory;
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
	public static TimableWalletNameDataConsumerFactory<WalletTransaction> getMongoDBConsumer() {
		Converter<WalletTransaction,Document> converter = new WalletTransactionDocumentConverter();
		MongoDBConsumerFactory<WalletTransaction> factory = new MongoDBConsumerFactory<>(converter);
		return factory;
	}
	
	public static TimableWalletNameDataConsumerFactory<ConvertedWalletTransaction> getMongoDBConvertedConsumer() {
		Converter<ConvertedWalletTransaction,Document> converter = new ConvertedWalletTransactionDocumentConverter();
		MongoDBConvertedTransactionConsumerFactory<ConvertedWalletTransaction> factory = 
				new MongoDBConvertedTransactionConsumerFactory<>(converter);
		return factory;
	}
	
	public static TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> getMongoDBConvertedProducer(){
		Converter<ConvertedWalletTransaction,Document> converter = new ConvertedWalletTransactionDocumentConverter();
		TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> factory = 
				new MongoDBConvertedTransactionProducerFactory<>(converter);
		return factory;
	}
		
	public static TimableWalletNameDataProducerFactory<WalletTransaction> getMongoDBProducer(){
		Converter<WalletTransaction,Document> converter = new WalletTransactionDocumentConverter();
		MongoDBProducerFactory factory = new MongoDBProducerFactory(converter);
		return factory;
	}
	
	public static TimableWalletNameDataProducerFactory<WalletTransaction> getWalletExplorerProducer() {		
		TimableWalletNameDataProducerFactory<WalletTransaction> factory = new WalletExplorerQuerierFactory();
		return factory;
	}
}