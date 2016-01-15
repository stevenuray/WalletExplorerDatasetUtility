package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;

public class MongoDBProducerFactory<T> extends MongoDBPipelineComponentFactory<T> 
	implements TimableWalletNameDataProducerFactory<T>{

	public MongoDBProducerFactory(Converter<T, Document> converter) {
		super(converter);	
	}

	public TimableDataProducer<T> getDataProducer(String walletName) {
		WalletCollection walletCollection = getWalletCollection(walletName);
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();		
		MongoDBProducer<T> producer = new MongoDBProducer<T>(walletCollection,maxTimespan,super.getConverter());
		return producer;
	}

	public TimableDataProducer<T> getDataProducer(String walletName,Interval timespan) {
		WalletCollection walletCollection = getWalletCollection(walletName);			
		MongoDBProducer<T> producer = new MongoDBProducer<T>(walletCollection,timespan,super.getConverter());
		return producer;
	}	
}