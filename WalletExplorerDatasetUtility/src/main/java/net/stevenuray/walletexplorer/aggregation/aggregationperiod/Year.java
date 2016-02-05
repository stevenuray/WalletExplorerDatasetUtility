package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class Year implements AggregationPeriod{

	@Override
	public Period getPeriod() {
		return Period.years(1);
	}

	@Override
	public String getName() {
		return "Year";
	}

	@Override
	public Interval getNextCompleteInterval(DateTime time) {
		DateTime nextCompleteStartTime = getNextCompleteStartTime(time);
		DateTime nextCompleteEndTime = getNextCompleteEndTime(time);
		return new Interval(nextCompleteStartTime,nextCompleteEndTime);		
	}

	private DateTime getNextCompleteEndTime(DateTime time) {
		int startYear = time.getYear();
		int endYear = startYear+2;
		return new DateTime(endYear,1,1,0,0,0);
	}

	private DateTime getNextCompleteStartTime(DateTime time) {
		int startYear = time.getYear();
		int nextYear = startYear+1;
		return new DateTime(nextYear,1,1,0,0,0);		
	}	
}