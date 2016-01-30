package net.stevenuray.walletexplorer.mongodb;

import java.util.Iterator;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.IteratorAdapter;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.timable.AscendingTimeIterator;
import net.stevenuray.walletexplorer.persistence.timable.AscendingTimeIteratorInstance;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimeNotFoundException;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.mongodb.MongoTimeoutException;

public class MongoDBProducer<T> extends MongoDBPipelineComponent<T> implements TimableDataProducer<T>{	
	private final Converter<T,Document> converter;
	private final Interval transactionTimespan;
	private final DataProducer<Document> documentProducer;
	
	public MongoDBProducer(
			WalletCollection walletCollection, Interval transactionTimespan,Converter<T,Document> converter){
		super(walletCollection);
		this.transactionTimespan = transactionTimespan;
		this.converter = converter;
		documentProducer = new DocumentProducer(super.getWalletCollection(), transactionTimespan);
	}
	
	@Override
	public void finish() {
		documentProducer.finish();	
	}
		
	public TimableDataProducer<T> fromTime(DateTime earliestTime) {
		Interval adjustedTransactionTimespan = new Interval(earliestTime,this.transactionTimespan.getEnd());
		return new MongoDBProducer<T>(super.getWalletCollection(),adjustedTransactionTimespan,this.converter);
	}
	
	public Iterator<T> getData() {				
		IteratorAdapter<Document,T> adapter = getAdapter();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		AscendingTimeIterator<T> source = new AscendingTimeIteratorInstance(adapter);
		return source;
	}
	
	public DateTime getEarliestTime() throws TimeNotFoundException {
		return super.tryToGetEarliestTime();
	}

	public DateTime getLatestTime() throws TimeNotFoundException {
		return super.tryToGetLatestTime();
	}

	@Override
	public void start() {
		documentProducer.start();	
	}

	private IteratorAdapter<Document,T> getAdapter(){		
		Iterator<Document> documentSource = tryGetDocuments();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		IteratorAdapter<Document,T> adapter = new IteratorAdapter(documentSource,converter);
		return adapter;
	}

	private Iterator<Document> getDocuments(){		
		Iterator<Document> documentIterator = documentProducer.getData();
		return documentIterator;
	}

	private Iterator<Document> tryGetDocuments(){
		try{
			return getDocuments();
		} catch(MongoTimeoutException e){
			//TODO refactor this.
			String causeMessage = "Cannot find MongoDB server.";
			String suggestionOne = " Ensure MongoDB's daemon is running, and accessable.";
			String suggestionTwo = " If this fails, check MongoDB configuration settings.";
			String errorMessage = causeMessage+suggestionOne+suggestionTwo;
			System.out.println(errorMessage);
			throw e;
		}
	}
}