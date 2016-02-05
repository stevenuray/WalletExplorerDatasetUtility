package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

import org.joda.time.Interval;

/**Data type representing an Interval, indicating the timespan of an entire aggregation dataset, and 
 * an AggregationPeriod, representing size of each aggregation. For example, if the timespan was a year 
 * the Interval would contain dates from January to December. The aggregation period could be months 
 * within that year, twelve in total.  
 * @author Steven Uray 2015-11-01
 */
public class AggregationTimespan {
	private final AggregationPeriod aggregationPeriod;
	private final Interval timespan;
	public AggregationTimespan(Interval timespan, AggregationPeriod aggregationPeriod){
		this.timespan = timespan;
		this.aggregationPeriod = aggregationPeriod;
	}
	public AggregationPeriod getAggregationPeriod(){
		return aggregationPeriod;
	}
	
	public Interval getTimespan(){
		return timespan;
	}
}
