package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class Month implements AggregationPeriod {
	private static final Period MONTH = Period.months(1);
	public String getName() {
		return "Month";
	}

	public Interval getNextCompleteInterval(DateTime time) {
		DateTime startTime = getNextCompleteStartTime(time);
		DateTime endTime = getNextCompleteEndTime(time);
		Interval nextCompleteInterval = new Interval(startTime,endTime);
		return nextCompleteInterval;		
	}

	public Period getPeriod() {
		return MONTH;
	}

	private DateTime getNextCompleteEndTime(DateTime time) {
		DateTime nextCompleteStartTime = getNextCompleteStartTime(time);
		DateTime nextCompleteEndTime = nextCompleteStartTime.plus(MONTH);
		return nextCompleteEndTime;
	}

	private DateTime getNextCompleteStartTime(DateTime time) {
		int startOfMonthYear = time.getYear();
		int monthOfYear = time.getMonthOfYear();
		DateTime startOfMonth = new DateTime(startOfMonthYear,monthOfYear,1,0,0,0);
		DateTime nextCompleteStartTime = startOfMonth.plusMonths(1);
		return nextCompleteStartTime;
	}
}
