package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bson.Document;
import org.joda.time.Interval;

public class IntervalSum {
	private final Interval timespan;
	public Interval getTimespan(){
		return timespan;
	}
	private final double sum;
	public Double getSum(){
		return sum;
	}
	
	public IntervalSum(Interval timespan,double sum){
		this.timespan = timespan;
		this.sum = sum;
	}
	
	public IntervalSum(Document document){
		timespan = new Interval(document.get("timespan"));
		sum = document.getDouble("sum");
	}
	
	public Document toDocument() {
		Document document = new Document();
		document.append("timespan", timespan.toString());
		document.append("sum",sum);
		return document;		
	}
	
	public String toPrettyString(){
		BigDecimal sumBD = new BigDecimal(sum);
		BigDecimal sumRounded = sumBD.setScale(2, RoundingMode.HALF_EVEN);
		StringBuilder builder = new StringBuilder();
		builder.append("Timespan: "+timespan);
		builder.append(" ");
		builder.append("Sum: "+sumRounded);
		return builder.toString();
	}
}
