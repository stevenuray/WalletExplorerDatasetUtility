package net.stevenuray.walletexplorer.mongodb.datapipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.converters.WalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataConsumer;
import net.stevenuray.walletexplorer.persistence.timable.TimeNotFoundException;

import org.apache.commons.collections4.IteratorUtils;
import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

public class MongoDBConsumer<T> extends MongoDBPipelineComponent<T> implements TimableDataConsumer<T>{
	private final String DATE_KEY = WalletTransactionDocumentConverter.DATE_KEY;
	private final Converter<T,Document> converter;
	
	public MongoDBConsumer(WalletCollection walletCollection,Converter<T,Document> converter){
		super(walletCollection);
		this.converter = converter;
	}
	
	public void consume(Iterator<T> producer) {			
		List<T> list = IteratorUtils.toList(producer);
		List<Document> convertedList = getConvertedList(list);
		MongoCollection<Document> mongoCollection = super.getWalletCollection().getCollection();
		try{
			mongoCollection.insertMany(convertedList);
		} catch(MongoBulkWriteException e){			
			if(e.getCode() == -3){
				/*Intentionally suppressing this exception, 
				 * as duplicate key errors are not considered a problem.				
				 */
			} else{
				throw e;
			}
		}		
	}
	
	public void consume(T item) {
		Document convertedDocument = convert(item);
		MongoCollection<Document> mongoCollection = super.getWalletCollection().getCollection();		 
		try{
			mongoCollection.insertOne(convertedDocument);
		} catch(MongoWriteException e){			
			if(e.getCode() == 11000){
				//Intentionally suppressing this exception, as duplicate key errors are not considered a problem.				
			} else{
				throw e;
			}
		}
	}
	
	@Override
	public void finish() {
		//MongoDB does not need to implement this method when consuming data. 		
	}
	
	public DateTime getEarliestTime() throws TimeNotFoundException {
		return super.tryToGetEarliestTime();
	}	
			
	public DateTime getLatestTime() throws TimeNotFoundException {
		return super.tryToGetLatestTime();
	}

	@Override
	public void start() {
		setupAscendingTimeIndex();	
	}
	
	private void setupAscendingTimeIndex() {
		MongoCollection<Document> collection = getWalletCollection().getCollection();
		Document indexDocument = new Document();
		indexDocument.append(DATE_KEY, 1);
		collection.createIndex(indexDocument);		
	} 

	private Document convert(T item){
		Document nextDocument = converter.to(item);
		return nextDocument;
	}

	private List<Document> getConvertedList(List<T> originalList){
		List<Document> documentList = new ArrayList<Document>();
		for(T item : originalList){
			Document document = converter.to(item);
			documentList.add(document);
		}
		return documentList;
	}		
}