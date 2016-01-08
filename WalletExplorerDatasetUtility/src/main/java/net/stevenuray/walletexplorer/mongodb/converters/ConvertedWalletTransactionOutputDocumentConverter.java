package net.stevenuray.walletexplorer.mongodb.converters;

import net.stevenuray.walletexplorer.persistence.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;

import org.bson.Document;

public class ConvertedWalletTransactionOutputDocumentConverter 
	implements Converter<ConvertedWalletTransactionOutput,Document>{
	private final Converter<TransactionOutput,Document> transactionOutputConverter;
	
	public ConvertedWalletTransactionOutputDocumentConverter(){
		this.transactionOutputConverter = new TransactionOutputDocumentConverter();
	}
	
	public ConvertedWalletTransactionOutputDocumentConverter(
			Converter<TransactionOutput,Document> walletTransactionOutputConverter){
		this.transactionOutputConverter = walletTransactionOutputConverter;
	}
	
	public ConvertedWalletTransactionOutput from(Document convertedOutputDocument) {
		TransactionOutput transactionOutput = getTransactionOutput(convertedOutputDocument);
		double usdVolume = convertedOutputDocument.getDouble("usdVolume");
		return new ConvertedWalletTransactionOutput(transactionOutput,usdVolume);
	}

	public Document to(ConvertedWalletTransactionOutput convertedOutput) {
		Document document = new Document();
		TransactionOutput transactionOutput = convertedOutput.getTransactionOutput();
		Document transactionOutputDocument = getTransactionOutputDocument(transactionOutput);
		
		document.append("transactionOutput",transactionOutputDocument);
		document.append("usdVolume", convertedOutput.getUsdVolume());
		return document;
	}
	
	private TransactionOutput getTransactionOutput(Document convertedOutputDocument) {
		Document transactionOutputDocument = (Document) convertedOutputDocument.get("transactionOutput");
		return transactionOutputConverter.from(transactionOutputDocument);
	}
	
	private Document getTransactionOutputDocument(TransactionOutput transactionOutput) {		
		Document transactionOutputDocument = transactionOutputConverter.to(transactionOutput);
		return transactionOutputDocument;
	}
}