package net.stevenuray.walletexplorer.mongodb;

import com.mongodb.MongoClient;

/**According to MongoDB official driver documentation, there should be only one MongoClient object
 * for the entire JVM. This singleton was created to assist in fulfilling that requirement.
 * It also acts as a single point of configuration for connecting to the MongoDB database containing 
 * the relevant data. 
 * @author Steven Uray 2015-10-21
 */
public class MongoDBClientSingleton {
	private final static String MONGODB_HOST = "localhost";
	private final static int MONGODB_PORT = 27017;
	private static MongoClient instance;
	public static MongoClient getInstance(){
		if(instance == null){
			setNewInstance();
		}
		return instance;
	}
	/**If the current MongoClient enters into a bad state, this function can be called
	 * in an attempt to reset it. 
	 */
	public static void resetInstance(){
		setNewInstance();
	}
	
	private static void setNewInstance() {
		instance = new MongoClient(MONGODB_HOST,MONGODB_PORT);		
	}
}
