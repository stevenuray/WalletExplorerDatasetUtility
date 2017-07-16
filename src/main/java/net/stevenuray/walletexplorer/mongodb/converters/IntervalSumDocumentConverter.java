package net.stevenuray.walletexplorer.mongodb.converters;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.conversion.objects.Converter;

import org.bson.Document;
import org.joda.time.Interval;

public class IntervalSumDocumentConverter implements Converter<IntervalSum,Document>{
	
	public Document to(IntervalSum intervalSum) {
		Document document = new Document();		
		document.append("timespan", intervalSum.getTimespan().toString());
		document.append("sum",intervalSum.getSum());
		return document;		
	}
	
	public IntervalSum from(Document document) {
		String timespanString = document.getString("timespan");
		Interval timespan = new Interval(timespanString);
		double sum = document.getDouble("sum");
		return new IntervalSum(timespan,sum);
	}
}