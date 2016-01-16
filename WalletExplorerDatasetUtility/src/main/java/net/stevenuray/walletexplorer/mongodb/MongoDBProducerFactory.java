package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;

public class MongoDBProducerFactory<ConvertedWalletTransaction> 
	extends MongoDBPipelineComponentFactory<ConvertedWalletTransaction> 
	implements TimableWalletNameDataProducerFactory<ConvertedWalletTransaction>{

	public MongoDBProducerFactory(Converter<ConvertedWalletTransaction, Document> converter) {
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

	private WalletCollection getConvertedCollection(String walletName){
		//TODO swap for forex currency symbol passed in by argument here.	
		String convertedWalletName = getConvertedWalletName(walletName,"USD");
		WalletCollection walletCollection = getWalletCollection(convertedWalletName);
		return walletCollection;
	}

	private String getConvertedWalletName(String walletName,String currencySymbol) {
		CollectionNameService collectionNameService = new CollectionNameService();		 
		String convertedWalletName = collectionNameService.getConvertedCollectionName(walletName, currencySymbol);
		return convertedWalletName;
	}	
}