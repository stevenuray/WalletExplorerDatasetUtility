package net.stevenuray.walletexplorer.dto;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDatabaseConnection {
	private final MongoClient mongoClient;
	public MongoClient getMongoClient(){
		return mongoClient;
	}
	private final String databaseName;
	
	public MongoDatabaseConnection(MongoClient mongoClient,String databaseName){
		this.mongoClient = mongoClient;
		this.databaseName = databaseName;
	}
	
	public MongoDatabase getMongoDatabase(){
		return mongoClient.getDatabase(databaseName);		
	}
}
