package net.stevenuray.walletexplorer.mongodb.datapipeline;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.CollectionNameService;
import net.stevenuray.walletexplorer.mongodb.MongoDBClientSingleton;
import net.stevenuray.walletexplorer.mongodb.MongoDBConnectionService;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;

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
		
	protected WalletCollection getConvertedCollection(String walletName){
		//TODO swap for forex currency symbol passed in by argument here.	
		String convertedWalletName = getConvertedWalletName(walletName,"USD");
		WalletCollection walletCollection = getWalletCollection(convertedWalletName);
		return walletCollection;
	}

	protected String getConvertedWalletName(String walletName,String currencySymbol) {
		CollectionNameService collectionNameService = new CollectionNameService();		 
		String convertedWalletName = collectionNameService.getConvertedCollectionName(walletName, currencySymbol);
		return convertedWalletName;
	}
}