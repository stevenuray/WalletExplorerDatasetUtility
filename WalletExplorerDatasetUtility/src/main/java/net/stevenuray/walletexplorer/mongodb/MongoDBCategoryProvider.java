package net.stevenuray.walletexplorer.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.categories.CategoryProvider;
import net.stevenuray.walletexplorer.categories.WalletCategory;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.converters.ConvertedWalletTransactionDocumentConverter;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;

import org.bson.Document;
import org.joda.time.Interval;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBCategoryProvider implements CategoryProvider<ConvertedWalletTransaction> {		
	public Iterator<DataProducer<ConvertedWalletTransaction>> getDataProducers(
			WalletCategory walletCategory,Interval timespan) {		
		List<DataProducer<ConvertedWalletTransaction>> dataProducersList = 
				new ArrayList<DataProducer<ConvertedWalletTransaction>>();
		Iterator<String> walletNameIterator = walletCategory.getWalletNameIterator();
		while(walletNameIterator.hasNext()){
			String nextWalletName = walletNameIterator.next();
			DataProducer<ConvertedWalletTransaction> dataProducer = getDataProducer(nextWalletName,timespan);
			dataProducersList.add(dataProducer);
		}
		Iterator<DataProducer<ConvertedWalletTransaction>> dataProducerIterator = dataProducersList.iterator();
		return dataProducerIterator;
	}

	private DataProducer<ConvertedWalletTransaction> getDataProducer(String nextWalletName,Interval timespan) {
		WalletCollection walletCollection = getConvertedWalletCollection(nextWalletName);		
		Converter<ConvertedWalletTransaction,Document> converter = new ConvertedWalletTransactionDocumentConverter();
		MongoDBProducer<ConvertedWalletTransaction> mongoProducer = 
				new MongoDBProducer<ConvertedWalletTransaction>(walletCollection,timespan,converter);
		return mongoProducer;
	}
	
	//TODO replace with a factory of some kind
	private static WalletCollection getConvertedWalletCollection(String walletName){
		CollectionNameService collectionNameService = new CollectionNameService();
		//TODO swap for forex currency symbol passed in by argument here. 
		String convertedWalletName = collectionNameService.getConvertedCollectionName(walletName, "USD");		
		WalletCollection convertedWalletCollection = getCollection(convertedWalletName);		
		return convertedWalletCollection;
	}
	
	private static WalletCollection getCollection(String walletName){		
		MongoClient mongoClient = MongoDBConnectionService.getMongoClient();
		MongoDatabase database = MongoDBConnectionService.getMongoDatabase();		
		WalletCollection walletCollection = new WalletCollection(mongoClient,database,walletName);
		return walletCollection;
	}
}