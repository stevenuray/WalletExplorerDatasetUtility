package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import net.stevenuray.walletexplorer.conversion.objects.Converter;

public class MongoDBPipelineComponentFactory<T> {
	private final Converter<T,Document> converter;

	public MongoDBPipelineComponentFactory(Converter<T,Document> converter) {
		this.converter = converter;
	}

	public Converter<T, Document> getConverter() {
		return converter;
	}

	protected WalletCollection getWalletCollection(String walletName) {
		MongoClient mongoClient = MongoDBClientSingleton.getInstance();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();			
		WalletCollection mongoCollection = new WalletCollection(mongoClient,database,walletName);
		return mongoCollection;
	}
}