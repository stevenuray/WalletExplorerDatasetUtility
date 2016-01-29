package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/*TODO implement an interface that represents this returns the earliest time for a wallet, 
 * to improve modularity of persistence for the entire system. 
 */
/**Returns earliest transaction time if collection has entries, a DateTime starting at 
 * unix timestamp = 0 if not.
 * @author Steven Uray 2016-1-1
 */
public class WalletExplorerCollectionEarliestTimeQuerier extends WalletExplorerCollectionQuerier 
		implements Callable<DateTime>{
	public WalletExplorerCollectionEarliestTimeQuerier(WalletCollection walletCollection){
		super(walletCollection);
	}
	
	/*Returns earliest transaction time if collection has entries, a DateTime starting at 
	 * unix timestamp = 0 if not. If a collection that has no entries has the descending time sort 
	 * applied to it, it will cause MongoDB to run out of memory and throw an error. This behavior was 
	 * observed on 2016-01-12 with MongoDB 3.2. 
	 */
	public DateTime call() throws Exception {
		String collectionName = walletCollection.getCollectionName();
		if(isCollectionInDatabase(collectionName)){
			return getEarliestTransactionTime();
		} else{
			return new DateTime(0);
		}
	}
	
	private DateTime getEarliestTransactionTime(){
		Iterator<Document> ascendingTimeIterator = getAscendingTimeIterator();
		DateTime earliestTime = getEarliestTime(ascendingTimeIterator);
		return earliestTime;
	}
	
	private Iterator<Document> getAscendingTimeIterator(){
		MongoCollection<Document> collection = walletCollection.getCollection();		
		BasicDBObject ascendingTimeSort = getAscendingTimeSort();
		Iterator<Document> ascendingTimeIterator = collection.find().sort(ascendingTimeSort).iterator();
		return ascendingTimeIterator;
	}
	
	private BasicDBObject getAscendingTimeSort(){
		return new BasicDBObject().append(dateKey, 1);
	}

	private DateTime getEarliestTime(Document earliestWalletTransactionDocument) {
		long unixTimestampMilliseconds = earliestWalletTransactionDocument.getLong(dateKey);
		DateTime earliestTime = new DateTime(unixTimestampMilliseconds);
		return earliestTime;
	}

	private DateTime getEarliestTime(Iterator<Document> ascendingTimeIterator) {
		if(ascendingTimeIterator.hasNext()){
			return getEarliestTime(ascendingTimeIterator.next());
		} else{
			return new DateTime(0);
		}
	}
}