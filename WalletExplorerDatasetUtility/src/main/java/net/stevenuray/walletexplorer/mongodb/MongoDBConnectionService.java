package net.stevenuray.walletexplorer.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

//TODO make this class get it's configuration from a config file instead of hard coded variables.
public class MongoDBConnectionService {
	public static final int MONGODB_PORT = 27017;
	public static final String MONGODB_HOST = "localhost";	
	public static final String WALLET_DB_NAME ="walletexplorerData";
	private static final MongoClient MONGO_CLIENT = new MongoClient(MONGODB_HOST, MONGODB_PORT);
	
	public static MongoClient getMongoClient(){		
		return MONGO_CLIENT;
	}
	
	public static MongoDatabase getMongoDatabase(){		
		MongoDatabase mongoDatabase = MONGO_CLIENT.getDatabase(WALLET_DB_NAME);
		return mongoDatabase;
	}
}
