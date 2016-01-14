package net.stevenuray.walletexplorer.mongodb;

import java.util.Iterator;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.IteratorAdapter;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.timable.AscendingTimeIterator;
import net.stevenuray.walletexplorer.persistence.timable.AscendingTimeIteratorInstance;

import org.bson.Document;
import org.joda.time.Interval;

import com.mongodb.MongoTimeoutException;

//TODO implement TimableDataProducer.
public class MongoDBProducer<T> implements DataProducer<T>{
	private final WalletCollection sourceCollection;
	private final Interval transactionTimespan;
	private final Converter<T,Document> converter;
		
	public MongoDBProducer(
			WalletCollection sourceCollection, Interval transactionTimespan,Converter<T,Document> converter){
		this.sourceCollection = sourceCollection;
		this.transactionTimespan = transactionTimespan;
		this.converter = converter;
	}
	
	public Iterator<T> getData() {				
		IteratorAdapter<Document,T> adapter = getAdapter();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		AscendingTimeIterator<T> source = new AscendingTimeIteratorInstance(adapter);
		return source;
	}
		
	private IteratorAdapter<Document,T> getAdapter(){		
		Iterator<Document> documentSource = tryGetDocuments();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		IteratorAdapter<Document,T> adapter = new IteratorAdapter(documentSource,converter);
		return adapter;
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
	private Iterator<Document> getDocuments(){		
		DataProducer<Document> documentProducer = new DocumentProducer(sourceCollection, transactionTimespan);
		Iterator<Document> documentIterator = documentProducer.getData();
		return documentIterator;
	}
}