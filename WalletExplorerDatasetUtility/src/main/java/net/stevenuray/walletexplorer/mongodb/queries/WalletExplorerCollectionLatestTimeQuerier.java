package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;

import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/**Returns latest transaction time if collection has entries, a datetime starting at 
 * unix timestamp = 0 if not. 
 * @author Steven Uray 2015-10-12
 */
/*TODO implement an interface that represents this returns the latest time for a wallet, 
 * to improve modularity of persistence for the entire system. 
 */
//TODO refactor this to a common superclass with WalletExplorerCollectionEarliestTimeQuerier.

public class WalletExplorerCollectionLatestTimeQuerier implements Callable<DateTime> {
	private final String dateKey = WalletTransactionDocumentConverter.DATE_KEY;
	private final WalletCollection mongoCollectionConnection;
	
	public WalletExplorerCollectionLatestTimeQuerier(WalletCollection mongoCollectionConnection){
		this.mongoCollectionConnection = mongoCollectionConnection;
	}
	
	/**Returns latest transaction time if collection has entries, a datetime starting at 
	 * unix timestamp = 0 if not. 
	 */
	public DateTime call() throws Exception {		
		Iterator<Document> descendingTimeIterator = getDescendingTimeIterator();		
		DateTime latestWalletTransactionTime = getLatestTransactionTime(descendingTimeIterator);
		return latestWalletTransactionTime;
	}
	
	private Iterator<Document> getDescendingTimeIterator(){
		MongoCollection<Document> collection = mongoCollectionConnection.getCollection();		
		BasicDBObject descendingTimeSort = getDescendingTimeSort();
		Iterator<Document> descendingTimeIterator = collection.find().sort(descendingTimeSort).iterator();
		return descendingTimeIterator;
	}
	
	private BasicDBObject getDescendingTimeSort(){
		return new BasicDBObject().append(dateKey, -1);
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