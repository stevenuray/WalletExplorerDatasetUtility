package net.stevenuray.walletexplorer.mongodb.queries;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

public class WalletExplorerCollectionQuerier {
	protected final String dateKey = WalletTransactionDocumentConverter.DATE_KEY;
	protected final WalletCollection walletCollection;
	
	public WalletExplorerCollectionQuerier(WalletCollection walletCollection){
		this.walletCollection = walletCollection;
	}
	
	protected boolean isCollectionInDatabase(String collectionName) {
		MongoIterable<String> collectionMongoIterable = walletCollection.getDatabase().listCollectionNames();
		MongoCursor<String> collectionCursor = collectionMongoIterable.iterator();		
		while(collectionCursor.hasNext()){
			String name = collectionCursor.next();
			if(name.equalsIgnoreCase(collectionName)){
				return true; 
			}
		}
		return false;
	}
}