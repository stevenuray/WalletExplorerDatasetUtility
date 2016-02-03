package net.stevenuray.walletexplorer.aggregation;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;

import org.bson.Document;
import org.joda.time.Interval;

public class WalletTransactionSum implements TransactionIntervalSum{	
	private final Interval timespan;
	private final IntervalSum aggregateTransactionVolume;
	private final String walletName;
	
	//TODO remove and replace with WalletTransactionSumDocumentConverter
	public WalletTransactionSum(Document document){
		walletName = document.getString("walletName");
		timespan = new Interval(document.get("timespan"));
		Document aggregateSumDocument = (Document) document.get("aggregateTransactionVolume");
		aggregateTransactionVolume = new IntervalSum(aggregateSumDocument);
	}
	
	public WalletTransactionSum(String walletName,Interval timespan, IntervalSum aggregateTransactionVolume){	
		this.walletName = walletName;
		this.timespan = timespan;
		this.aggregateTransactionVolume = aggregateTransactionVolume;
	}
		
	public IntervalSum getAggregateTransactionVolume(){
		return aggregateTransactionVolume;
	}

	public String getName() {
		return walletName;
	}
	
	public Interval getTimespan(){
		return timespan;
	}
	
	public IntervalSum getTransactionIntervalSum() {
		return getAggregateTransactionVolume();
	}
	
	//TODO remove and replace with WalletTransactionSumDocumentConverter
	public Document toDocument() {
		Document document = new Document();		
		document.append("walletName", walletName);
		document.append("timespan", timespan.toString());
		document.append("aggregateTransactionVolume", aggregateTransactionVolume.toDocument());	
		document.append("endUnixTimestampMilliseconds",timespan.getEndMillis());
		return document;
	}
	
	public String toString(){
		return toPrettyString();
	}
	
	public String toPrettyString(){
		return aggregateTransactionVolume.toPrettyString();
	}	
}