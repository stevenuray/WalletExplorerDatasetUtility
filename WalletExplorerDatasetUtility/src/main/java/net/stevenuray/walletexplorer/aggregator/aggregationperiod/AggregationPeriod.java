package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public interface AggregationPeriod {
	public Period getPeriod();
	public String getName();
	/**Returns when the next complete period will occur. 
	 * For example, if using the Month implementation, and providing a time halfway through the June,
	 * will return an interval starting at the first day of July, and ending at the last day of July.
	 * If using the Week implementation, and providing a time on Tuesday, will return an interval
	 * starting on Sunday, and ending next Sunday. 
	 * @param time
	 * @return
	 */
	public Interval getNextCompleteInterval(DateTime time);		
}
