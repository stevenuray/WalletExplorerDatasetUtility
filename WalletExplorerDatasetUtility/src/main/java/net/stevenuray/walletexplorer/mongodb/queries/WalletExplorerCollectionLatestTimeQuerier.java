package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/*TODO implement an interface that represents this returns the latest time for a wallet, 
 * to improve modularity of persistence for the entire system. 
 */
/**Returns latest transaction time if collection has entries, a DateTime starting at 
 * unix timestamp = 0 if not.
 * @author Steven Uray 
 */
public class WalletExplorerCollectionLatestTimeQuerier extends WalletExplorerCollectionQuerier 
	implements Callable<DateTime> {
	
	public WalletExplorerCollectionLatestTimeQuerier(WalletCollection walletCollection){
		super(walletCollection);
	}
	
	/*Returns latest transaction time if collection has entries, a DateTime starting at 
	 * unix timestamp = 0 if not. If a collection that has no entries has the descending time sort 
	 * applied to it, it will cause MongoDB to run out of memory and throw an error. This behavior was 
	 * observed on 2016-01-12 with MongoDB 3.2. 
	 */
	public DateTime call() throws Exception {		
		String collectionName = walletCollection.getCollectionName();
		if(isCollectionInDatabase(collectionName)){
			return getLatestTransactionTime();
		} else{
			return new DateTime(0);
		}
	}
	
	private Iterator<Document> getDescendingTimeIterator(){
		MongoCollection<Document> collection = walletCollection.getCollection();		
		BasicDBObject descendingTimeSort = getDescendingTimeSort();
		Iterator<Document> descendingTimeIterator = collection.find().sort(descendingTimeSort).iterator();
		return descendingTimeIterator;
	}
	
	private BasicDBObject getDescendingTimeSort(){
		return new BasicDBObject().append(dateKey, -1);
	}
	
	private DateTime getLatestTransactionTime(){
		Iterator<Document> descendingTimeIterator = getDescendingTimeIterator();		
		DateTime latestWalletTransactionTime = getLatestTransactionTime(descendingTimeIterator);
		return latestWalletTransactionTime;
	}
	
	private DateTime getLatestTransactionTime(Document latestWalletTransactionDocument){	
		long unixTimestampMilliseconds = latestWalletTransactionDocument.getLong(dateKey);
		DateTime latestWalletTransactionTime = new DateTime(unixTimestampMilliseconds);
		return latestWalletTransactionTime;	
	}
	
	private DateTime getLatestTransactionTime(Iterator<Document> descendingTimeIterator){		
		if(descendingTimeIterator.hasNext()){
			return getLatestTransactionTime(descendingTimeIterator.next());
		} else{
			//This will happen if the collection has no entries yet. 
			return new DateTime(0);
		}			
	}
}