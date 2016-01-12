package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.WalletNameDataConsumerFactory;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataConsumer;

public class MongoDBConsumerFactory<T> implements WalletNameDataConsumerFactory<T>{
	private final Converter<T,Document> converter;
	
	public MongoDBConsumerFactory(Converter<T,Document> converter){
		this.converter = converter;
	}
	
	public TimableDataConsumer<T> getDataConsumer(String walletName) {
		WalletCollection walletCollection = getMongoCollection(walletName);		
		MongoDBConsumer<T> consumer = new MongoDBConsumer<T>(walletCollection,converter);
		return consumer;
	}
	
	private WalletCollection getMongoCollection(String walletName) {
		MongoClient mongoClient = MongoDBClientSingleton.getInstance();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();			
		WalletCollection mongoCollection = new WalletCollection(mongoClient,database,walletName);
		return mongoCollection;
	}
}