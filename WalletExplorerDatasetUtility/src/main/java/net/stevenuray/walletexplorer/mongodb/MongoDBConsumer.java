package net.stevenuray.walletexplorer.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionEarliestTimeQuerier;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionLatestTimeQuerier;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.TimableDataConsumer;

import org.apache.commons.collections4.IteratorUtils;
import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

public class MongoDBConsumer<T> implements TimableDataConsumer<T>{
	private final Converter<T,Document> converter;
	private final WalletCollection destinationWalletCollection;
	
	public MongoDBConsumer(WalletCollection destinationWalletCollection,Converter<T,Document> converter){
		this.destinationWalletCollection = destinationWalletCollection;
		this.converter = converter;
	}
	
	public void consume(Iterator<T> producer) {			
		List<T> list = IteratorUtils.toList(producer);
		List<Document> convertedList = getConvertedList(list);
		MongoCollection<Document> mongoCollection = destinationWalletCollection.getCollection();
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
		MongoCollection<Document> mongoCollection = destinationWalletCollection.getCollection();		 
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
	
	public DateTime getEarliestTime() {
		WalletExplorerCollectionEarliestTimeQuerier earliestTimeQuerier = 
				new WalletExplorerCollectionEarliestTimeQuerier(destinationWalletCollection);
		//TODO refactor this exception to make it thrown upwards. 
		DateTime earliestTransactionTime = null;
		try{
			earliestTransactionTime = earliestTimeQuerier.call();
		} catch(Exception e){
			System.out.println("Could not find the latest transaction time for: "+
					destinationWalletCollection.getCollectionName()+"!");
			e.printStackTrace();
			return new DateTime(0);
		}
		return earliestTransactionTime;
	}	
	
	public DateTime getLatestTime() {
		WalletExplorerCollectionLatestTimeQuerier latestTimeQuerier =
				new WalletExplorerCollectionLatestTimeQuerier(destinationWalletCollection);
		DateTime latestTransactionTime = null;
		//TODO refactor this exception to make it thrown upwards. 
		try {
			latestTransactionTime = latestTimeQuerier.call();
		} catch (Exception e) {			
			System.out.println("Could not find the latest transaction time for: "+
					destinationWalletCollection.getCollectionName()+"!");
			e.printStackTrace();
			return new DateTime(0);
		}
		//TODO DEVELOPMENT
		return new DateTime(0);
		//return latestTransactionTime;
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