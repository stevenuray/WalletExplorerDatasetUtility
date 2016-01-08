package net.stevenuray.walletexplorer.mongodb;

import net.stevenuray.walletexplorer.dto.MongoDatabaseConnection;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**Collection class intended to contain all the objects necessary to connect to a specific 
 * mongoDB collection. 
 * @author Steven Uray 2015-10-12 
 */
public class WalletCollection {
	private final MongoClient mongoClient;
	public MongoClient getMongoClient(){
		return mongoClient;
	}
	private final MongoDatabase database;
	public MongoDatabase getDatabase(){
		return database;
	}
	private final MongoCollection<Document> collection;
	public MongoCollection<Document> getCollection(){
		return collection;
	}	
	private final String collectionName;	
	public String getCollectionName() {
		return collectionName;
	}

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
}