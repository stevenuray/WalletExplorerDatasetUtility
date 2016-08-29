package net.stevenuray.walletexplorer.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**Collection class intended to contain all the objects necessary to connect to a specific 
 * mongoDB collection. 
 * @author Steven Uray 2015-10-12 
 */
public class WalletCollection {
	private final MongoCollection<Document> collection;
	private final String collectionName;
	private final MongoDatabase database;
	private final MongoClient mongoClient;
	public WalletCollection(MongoClient mongoClient,MongoDatabase database,
			String collectionName){
		this.mongoClient = mongoClient;
		this.database = database;
		this.collectionName = collectionName;		
		this.collection = this.database.getCollection(collectionName);
	}
	public WalletCollection(MongoDatabaseConnection mongoDatabaseConnection,String collectionName){
		this.mongoClient = mongoDatabaseConnection.getMongoClient();
		this.database = mongoDatabaseConnection.getMongoDatabase();
		this.collectionName = collectionName;
		this.collection = this.database.getCollection(collectionName);
	}	
	public MongoCollection<Document> getCollection(){
		return collection;
	}	
	public String getCollectionName() {
		return collectionName;
	}

	public MongoDatabase getDatabase(){
		return database;
	}
	
	public MongoClient getMongoClient(){
		return mongoClient;
	}
}