package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;

import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/*TODO implement an interface that represents this returns the earliest time for a wallet, 
 * to improve modularity of persistence for the entire system. 
 */
//TODO refactor this to a common superclass with WalletExplorerCollectionLatestTimeQuerier.
/**Returns earliest transaction time if collection has entries, a datetime starting at 
 * unix timestamp = 0 if not. 
 * @author Steven Uray 2016-1-1
 */
public class WalletExplorerCollectionEarliestTimeQuerier implements Callable<DateTime>{
	private final String dateKey = WalletTransactionDocumentConverter.DATE_KEY;
	private final WalletCollection walletCollection;
	
	public WalletExplorerCollectionEarliestTimeQuerier(WalletCollection walletCollection){
		this.walletCollection = walletCollection;
	}

	public DateTime call() throws Exception {
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