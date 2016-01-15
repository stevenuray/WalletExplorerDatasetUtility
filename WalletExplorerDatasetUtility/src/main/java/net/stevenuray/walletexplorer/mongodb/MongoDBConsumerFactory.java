package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataConsumer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataConsumerFactory;

public class MongoDBConsumerFactory<T> extends MongoDBPipelineComponentFactory<T> implements TimableWalletNameDataConsumerFactory<T>{
	
	public MongoDBConsumerFactory(Converter<T,Document> converter){
		super(converter);
	}
	
	public TimableDataConsumer<T> getDataConsumer(String walletName) {
		WalletCollection walletCollection = getWalletCollection(walletName);		
		TimableDataConsumer<T> consumer = new MongoDBConsumer<T>(walletCollection,super.getConverter());
		return consumer;
	}
}