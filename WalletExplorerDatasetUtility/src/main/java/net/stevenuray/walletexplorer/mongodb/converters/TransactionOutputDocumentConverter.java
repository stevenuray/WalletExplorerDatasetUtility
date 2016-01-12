package net.stevenuray.walletexplorer.mongodb.converters;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;

import org.bson.Document;

public class TransactionOutputDocumentConverter implements Converter<TransactionOutput,Document>{

	public Document to(TransactionOutput t) {
		Document document = new Document();
		document.append("amount", t.getAmount());
		document.append("walletId",t.getWalletId());
		return document;
	}

	public TransactionOutput from(Document u) {
		double amount = u.getDouble("amount");
		String walletId = u.getString("walletId");
		return new TransactionOutput(amount,walletId);
	}
}
