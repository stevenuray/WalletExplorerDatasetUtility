package net.stevenuray.walletexplorer.conversion.collection;

import net.stevenuray.walletexplorer.mongodb.CollectionNameService;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ConvertedCollectionService implements ConvertedCollectionProvider {
		
	public WalletCollection getConvertedCollection(WalletCollection unconvertedCollection, String currencySymbol) {
		String unconvertedName = unconvertedCollection.getCollectionName();	
		String convertedName = getConvertedName(unconvertedName,currencySymbol);		
		MongoClient mongoClient = unconvertedCollection.getMongoClient();
		MongoDatabase database = unconvertedCollection.getDatabase();
		WalletCollection convertedCollection = new WalletCollection(mongoClient,database,convertedName);
		return convertedCollection;
	}
	
	private String getConvertedName(String unconvertedName,String currencySymbol){		
		CollectionNameService collectionNameService = new CollectionNameService();
		String convertedName = collectionNameService.getConvertedCollectionName(unconvertedName, currencySymbol);
		return convertedName;
	}
}
