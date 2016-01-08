package net.stevenuray.walletexplorer.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.persistence.Converter;
import net.stevenuray.walletexplorer.persistence.DataConsumer;

import org.apache.commons.collections4.IteratorUtils;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class MongoDBConsumer<T> implements DataConsumer<T>{
	private final MongoCollection<Document> destinationCollection;
	private final Converter<T,Document> converter;
	
	public MongoDBConsumer(
			MongoCollection<Document> destinationCollection,Converter<T,Document> converter){
		this.destinationCollection = destinationCollection;
		this.converter = converter;
	}
	
	public void consume(Iterator<T> producer) {			
		List<T> list = IteratorUtils.toList(producer);
		List<Document> convertedList = getConvertedList(list);
		destinationCollection.insertMany(convertedList);
	}
	
	public void consume(T item) {
		Document convertedDocument = convert(item);
		destinationCollection.insertOne(convertedDocument);
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