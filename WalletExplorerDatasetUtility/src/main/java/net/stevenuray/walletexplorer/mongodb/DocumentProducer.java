package net.stevenuray.walletexplorer.mongodb;

import java.util.Iterator;

import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.persistence.AscendingTimeIterator;
import net.stevenuray.walletexplorer.persistence.AscendingTimeIteratorInstance;
import net.stevenuray.walletexplorer.persistence.DataProducer;

import org.bson.Document;
import org.joda.time.Interval;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/*TODO factor DATE_KEY into a time related subclass. The top level class for DocumentProducer should not 
 * impose a date requirement on it's data. 
 */
public class DocumentProducer implements DataProducer<Document>{
	private final WalletCollection walletCollection;
	private final Interval queryTimespan;
	private static final String DATE_KEY = WalletTransactionDocumentConverter.DATE_KEY;
	
	public DocumentProducer(WalletCollection walletCollection,Interval queryTimespan){
		this.walletCollection = walletCollection;
		this.queryTimespan = queryTimespan;		
	}

	public Iterator<Document> getData() {		
		return getAscendingTransactionDateIterator();
	}
	
	private BasicDBObject getAscendingTimeSort(){			
		BasicDBObject ascendingTimeSort = new BasicDBObject().append(DATE_KEY, 1);
		return ascendingTimeSort;
	}
	
	private AscendingTimeIterator<Document> getAscendingTransactionDateIterator(){
		MongoCollection<Document> collection = walletCollection.getCollection();		
		BasicDBObject ascendingTimeSort = getAscendingTimeSort();
		BasicDBObject transactionsInQueryTimespan = getTransactionsInQueryTimespan();
		FindIterable<Document> findIterable = collection.find(transactionsInQueryTimespan).sort(ascendingTimeSort);
		Iterator<Document> cursor = findIterable.iterator();		
		AscendingTimeIterator<Document> ascendingTimeCursor = new AscendingTimeIteratorInstance<Document>(cursor);
		return ascendingTimeCursor;
	}
	
	private BasicDBObject getTransactionsInQueryTimespan(){
		BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
		builder.add("$gte", queryTimespan.getStartMillis());
		builder.add("$lte", queryTimespan.getEndMillis());
		BasicDBObject timespanBounds = (BasicDBObject) builder.get();		
		BasicDBObject timespanQuery = new BasicDBObject(DATE_KEY,timespanBounds);
		return timespanQuery;		
	}	
}