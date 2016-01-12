package net.stevenuray.walletexplorer.mongodb.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction.TransactionDirection;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.joda.time.DateTime;

public class WalletTransactionDocumentConverter implements Converter<WalletTransaction,Document>{
	public static final String DATE_KEY = "Transaction Date";
	private final Converter<TransactionOutput,Document> transactionOutputConverter;
	
	public WalletTransactionDocumentConverter(){
		//Default converter
		transactionOutputConverter = new TransactionOutputDocumentConverter();
	}
	
	public WalletTransactionDocumentConverter(
			Converter<TransactionOutput,Document> transactionOutputConverter){
		this.transactionOutputConverter = transactionOutputConverter;
	}
	
	public WalletTransaction from(Document document) {
		DateTime transactionTime = getTransactionTime(document);	
		String txid = document.getString("txid");
		double balance = document.getDouble("balance");
		TransactionDirection transactionDirection = getTransactionDirection(document);
		Collection<TransactionOutput> walletTransactionOutputs = getWalletTransactionOutputs(document);
		WalletTransaction walletTransaction = 
				new WalletTransaction(transactionTime,txid,balance,transactionDirection,walletTransactionOutputs);
		return walletTransaction;
	}

	public Document to(WalletTransaction transaction) {
		Document document = new Document();
		document.append("txid", transaction.getTxid());
		document.append("balance",transaction.getBalance());
		document.append(DATE_KEY, transaction.getTransactionTime().getMillis());
		String transactionDirectionString = transaction.getTransactionDirection().toString().toLowerCase();
		document.append("type", transactionDirectionString);		
		
		BsonArray outputsArray = getOutputsArray(transaction.getWalletTransactionOutputsUnmodifiable());
		document.append("outputs",outputsArray);	
		//TODO DEVELOPMENT
		//System.out.println("Current Time is: "+transaction.getTransactionTime());
		return document;
	}

	private BsonArray getOutputsArray(Collection<TransactionOutput> walletTransactionOutputs){
		BsonArray outputsArray = new BsonArray();
		Iterator<TransactionOutput> outputIterator = walletTransactionOutputs.iterator();
		while(outputIterator.hasNext()){
			TransactionOutput output = outputIterator.next();
			Document outputDocument = transactionOutputConverter.to(output);			
			BsonDocument bson = BsonDocument.parse(outputDocument.toJson());
			outputsArray.add(bson);
		}
		return outputsArray;
	}
	
	private TransactionDirection getTransactionDirection(Document document) {
		String transactionDirectionString = document.getString("type");
		if(transactionDirectionString.equals("received")){
			return TransactionDirection.RECEIVED;
		}		
		if(transactionDirectionString.equals("sent")){
			return TransactionDirection.SENT;
		}
		throw new IllegalArgumentException("Could not find a TransactionDirection for: "+transactionDirectionString);
	}
	
	private Collection<TransactionOutput> getTransactionOutputsFromBsonArray(Document document){
		Collection<TransactionOutput> walletTransactionOutputs = new ArrayList<TransactionOutput>();
		BsonArray documentOutputs = (BsonArray) document.get("outputs");
		for(int i = 0; i < documentOutputs.size(); i++){
			BsonDocument bsonDocument = (BsonDocument) documentOutputs.get(i);
			String jsonString = bsonDocument.toJson();
			Document walletTransactionDocument = Document.parse(jsonString);
			TransactionOutput output = transactionOutputConverter.from(walletTransactionDocument);
			walletTransactionOutputs.add(output);
		}
		return walletTransactionOutputs;
	}
	
	private DateTime getTransactionTime(Document document){
		long transactionUnixTimestampInMilliseconds = document.getLong("Transaction Date");
		DateTime transactionTime = new DateTime(transactionUnixTimestampInMilliseconds);	
		return transactionTime;
	}
	
	private Collection<TransactionOutput> getWalletTransactionOutputs(Document document) {		
		try{			
			return tryGetTransactionOutputsFromArrayList(document);
		} catch(ClassCastException e){
			//Will happen when loading from documents created via transactionOutputConverter()
			return getTransactionOutputsFromBsonArray(document);
		}		
	}
	
	private Collection<TransactionOutput> tryGetTransactionOutputsFromArrayList(Document document) throws ClassCastException{
		Collection<TransactionOutput> walletTransactionOutputs = new ArrayList<TransactionOutput>();
		@SuppressWarnings("unchecked")
		ArrayList<Document> documentOutputs = (ArrayList<Document>) document.get("outputs");			
		for(int i = 0; i < documentOutputs.size(); i++){
			Document documentOutput = (Document) documentOutputs.get(i);
			TransactionOutput output = transactionOutputConverter.from(documentOutput);
			walletTransactionOutputs.add(output);
		}
		return walletTransactionOutputs;
	}
}
