package net.stevenuray.walletexplorer.mongodb.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.persistence.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransactionOutput;

import org.apache.commons.collections4.IteratorUtils;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.joda.time.DateTime;

public class ConvertedWalletTransactionDocumentConverter implements Converter<ConvertedWalletTransaction,Document>{
	private static final Converter<ConvertedWalletTransactionOutput,Document> CONVERTED_TRANSACTION_OUTPUT_CONVERTER =
			new ConvertedWalletTransactionOutputDocumentConverter();
	
	public ConvertedWalletTransaction from(Document convertedWalletTransactionDocument) {
		String txid = convertedWalletTransactionDocument.getString("txid");		
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = 
				getConvertedTransactionOutputs(convertedWalletTransactionDocument);
		DateTime transactionTime = getTransactionTime(convertedWalletTransactionDocument);
		ConvertedWalletTransaction convertedWalletTransaction = 
				new ConvertedWalletTransaction(txid,convertedOutputs,transactionTime);		
		return convertedWalletTransaction;
	}

	public Document to(ConvertedWalletTransaction transaction) {		
		Document document = new Document();
		BsonArray convertedOutputsDocument = getConvertedOutputs(transaction);
		String dateKey = WalletTransactionDocumentConverter.DATE_KEY;
		
		document.append("txid", transaction.getTxid());
		document.append("convertedOutputs", convertedOutputsDocument);
		document.append("transactionOutputVolumeSumInUsd",transaction.getTransactionOutputVolumeSumInUsd());
		document.append(dateKey, transaction.getTransactionTime().getMillis());
		return document;
	}

	private void addBsonValueToListAsConvertedTransactionOutput(
			BsonValue outputBsonValue,List<ConvertedWalletTransactionOutput> list) {
		BsonDocument outputBsonDocument = outputBsonValue.asDocument();
		String outputJsonString = outputBsonDocument.toJson();
		Document outputDocument = Document.parse(outputJsonString);
		ConvertedWalletTransactionOutput output = CONVERTED_TRANSACTION_OUTPUT_CONVERTER.from(outputDocument);
		list.add(output);
	}

	private void addConvertedOutputToListAsBsonDocument(
			ConvertedWalletTransactionOutput convertedOutput,BsonArray list){
		Document convertedOutputDocument = CONVERTED_TRANSACTION_OUTPUT_CONVERTER.to(convertedOutput);
		BsonDocument bsonDocument = BsonDocument.parse(convertedOutputDocument.toJson());
		list.add(bsonDocument);	
	}

	private BsonArray getConvertedOutputs(ConvertedWalletTransaction transaction) {
		BsonArray convertedOutputs = new BsonArray();			
		List<ConvertedWalletTransactionOutput> convertedOutputsList = getConvertedOutputsList(transaction);		
		for(ConvertedWalletTransactionOutput convertedOutput : convertedOutputsList){
			addConvertedOutputToListAsBsonDocument(convertedOutput,convertedOutputs);
		}
		return convertedOutputs;
	}
	
	private List<ConvertedWalletTransactionOutput> getConvertedOutputsList(ConvertedWalletTransaction transaction) {
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = transaction.getConvertedOutputs();
		Iterator<ConvertedWalletTransactionOutput> iterator = convertedOutputs.iterator();		
		List<ConvertedWalletTransactionOutput> convertedOutputsList = IteratorUtils.toList(iterator);
		return convertedOutputsList;
	}
		
	private Collection<ConvertedWalletTransactionOutput> getConvertedTransactionOutputs(
			Document convertedWalletTransactionDocument) {	
		/*ConvertedWalletTransactions go into database as a BsonArray, come out as an ArrayList<Document>
		 * for reasons not fully understood. Working around this issue with the below try/catch. 
		 */
		try{
			return getConvertedTransactionOutputsFromBsonArray(convertedWalletTransactionDocument);
		} catch(ClassCastException e){
			return getConvertedTransactionOutputsFromArrayList(convertedWalletTransactionDocument);
		}
	}
	
	private Collection<ConvertedWalletTransactionOutput> getConvertedTransactionOutputsFromArrayList(
			Document convertedWalletTransactionDocument){
		@SuppressWarnings("unchecked")
		ArrayList<Document> originalList = 
				(ArrayList<Document>) convertedWalletTransactionDocument.get("convertedOutputs");
		ArrayList<ConvertedWalletTransactionOutput> convertedList = 
				new ArrayList<ConvertedWalletTransactionOutput>();			
		for(Document outputDocument : originalList){
			ConvertedWalletTransactionOutput convertedOutput = 
					CONVERTED_TRANSACTION_OUTPUT_CONVERTER.from(outputDocument);
			convertedList.add(convertedOutput);				
		}
		return convertedList;
	}
	
	private Collection<ConvertedWalletTransactionOutput> getConvertedTransactionOutputsFromBsonArray(
			Document convertedWalletTransactionDocument){
		BsonArray bsonArray = (BsonArray) convertedWalletTransactionDocument.get("convertedOutputs");
		List<ConvertedWalletTransactionOutput> convertedTransactionOutputs = 
				new ArrayList<ConvertedWalletTransactionOutput>();
		for(BsonValue bsonValue : bsonArray.getValues()){
			addBsonValueToListAsConvertedTransactionOutput(bsonValue,convertedTransactionOutputs);
		}
		return convertedTransactionOutputs;	
	}
	
	private DateTime getTransactionTime(Document convertedWalletTransactionDocument) {
		String dateKey = WalletTransactionDocumentConverter.DATE_KEY;
		long unixTimeStampMilliseconds = convertedWalletTransactionDocument.getLong(dateKey);
		DateTime transactionTime = new DateTime(unixTimeStampMilliseconds);
		return transactionTime;
	}	
}