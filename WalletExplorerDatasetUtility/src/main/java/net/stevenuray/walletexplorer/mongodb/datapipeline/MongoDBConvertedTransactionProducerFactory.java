package net.stevenuray.walletexplorer.mongodb.datapipeline;

import org.bson.Document;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.mongodb.CollectionNameService;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;

public class MongoDBConvertedTransactionProducerFactory<ConvertedWalletTransaction> 
	extends MongoDBPipelineComponentFactory<ConvertedWalletTransaction> 
	implements TimableWalletNameDataProducerFactory<ConvertedWalletTransaction>{

	public MongoDBConvertedTransactionProducerFactory(Converter<ConvertedWalletTransaction, Document> converter) {
		super(converter);	
	}

	public TimableDataProducer<ConvertedWalletTransaction> getDataProducer(String walletName) {			
		WalletCollection convertedWalletCollection = getConvertedCollection(walletName);
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();		
		MongoDBProducer<ConvertedWalletTransaction> producer = 
				new MongoDBProducer<ConvertedWalletTransaction>(
						convertedWalletCollection,maxTimespan,super.getConverter());
		return producer;
	}
	
	public TimableDataProducer<ConvertedWalletTransaction> getDataProducer(String walletName,Interval timespan) {
		WalletCollection convertedWalletCollection = getConvertedCollection(walletName);			
		MongoDBProducer<ConvertedWalletTransaction> producer = 
				new MongoDBProducer<ConvertedWalletTransaction>(
						convertedWalletCollection,timespan,super.getConverter());
		return producer;
	}		
}