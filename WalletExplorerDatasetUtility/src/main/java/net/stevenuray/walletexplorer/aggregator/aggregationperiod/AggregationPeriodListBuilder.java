package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**It is common for users of AggregationPeriod objects to want a list 
 * of complete Intervals within a larger Interval. For example, months within a year. 
 * @author Steven Uray 2015-10-29
 */
public class AggregationPeriodListBuilder {
	private final Interval totalTimespan;
	private final AggregationPeriod aggregationPeriod;
	
	/**
	 * @param totalTimespan - The interval within all intervals in a returned list should exist. For example,
	 * this interval could be from the start of 2014 to the start of 2015, and the intervals in the list
	 * could be months within 2014, January, February, etc. 
	 * @param aggregationPeriod - The aggregation period each Interval within the timespan should be. 
	 * Note unexpected behavior may occur if totalTimespan is of a duration shorter than aggregationPeriod.
	 */
	public AggregationPeriodListBuilder(Interval totalTimespan,AggregationPeriod aggregationPeriod){		
		this.totalTimespan = totalTimespan;
		this.aggregationPeriod = aggregationPeriod;			
	}
	
	public AggregationPeriodListBuilder(AggregationTimespan aggregationTimespan){
		this.totalTimespan = aggregationTimespan.getTimespan();
		this.aggregationPeriod = aggregationTimespan.getAggregationPeriod();
	}
	
	public List<Interval> getAscendingPeriodList(){
		//Setup
		List<Interval> ascendingList = new ArrayList<Interval>();
		DateTime currentTime = totalTimespan.getStart();
				
		//Creating the first interval, a special case compared to the ones after. 
		Interval firstInterval = aggregationPeriod.getNextCompleteInterval(currentTime);		
		currentTime = firstInterval.getEnd();
		DateTime lastTime = firstInterval.getStart();
		
		//Adding each subsequent interval until the end is reached. 
		while(currentTime.isBefore(totalTimespan.getEnd())){
			//Adding the interval			 
			Interval currentInterval = new Interval(lastTime,currentTime);
			ascendingList.add(currentInterval);
			
			//Setting up for the next loop. 
			lastTime = currentTime;
			Duration periodDuration = aggregationPeriod.getPeriod().toDurationFrom(currentTime);
			currentTime = currentTime.plus(periodDuration);
		}
		
		return ascendingList;
	}
	
}
