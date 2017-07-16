package net.stevenuray.walletexplorer.mongodb.converters;

import net.stevenuray.walletexplorer.aggregation.WalletTransactionSum;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.conversion.objects.Converter;

import org.bson.Document;
import org.joda.time.Interval;

public class WalletTransactionSumDocumentConverter implements Converter<WalletTransactionSum,Document> {	
	private final Converter<IntervalSum,Document> intervalSumConverter;
	//TODO refactor this out. It was put in so DocumentProducer can query it in it's existing form.
	private static final String DATE_KEY = WalletTransactionDocumentConverter.DATE_KEY;
	
	public WalletTransactionSumDocumentConverter(){
		this.intervalSumConverter = new IntervalSumDocumentConverter();
	}
	
	public WalletTransactionSumDocumentConverter(Converter<IntervalSum,Document> intervalSumConverter){
		this.intervalSumConverter = intervalSumConverter;
	}
	
	public Document to(WalletTransactionSum transactionSum) {
		Document document = new Document();		
		IntervalSum transactionIntervalSum = transactionSum.getTransactionIntervalSum();
		Document aggregateTransactionVolumeDocument = intervalSumConverter.to(transactionIntervalSum);
		
		document.append("walletName", transactionSum.getName());
		document.append("timespan", transactionSum.getTimespan().toString());		
		document.append("aggregateTransactionVolume", aggregateTransactionVolumeDocument);	
		document.append(DATE_KEY,transactionSum.getTimespan().getStartMillis());
		return document;		
	}

	public WalletTransactionSum from(Document transactionSumDocument) {
		String walletName = transactionSumDocument.getString("walletName");
		String intervalString = transactionSumDocument.getString("timespan");
		Interval timespan = new Interval(intervalString);
		Document intervalSumDocument = (Document) transactionSumDocument.get("aggregateTransactionVolume");
		IntervalSum intervalSum = intervalSumConverter.from(intervalSumDocument);
		WalletTransactionSum walletTransactionSum = new WalletTransactionSum(walletName, timespan, intervalSum);
		return walletTransactionSum;
	}
}