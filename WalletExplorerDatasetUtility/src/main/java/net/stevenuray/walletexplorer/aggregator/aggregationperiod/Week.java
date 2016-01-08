package net.stevenuray.walletexplorer.aggregator.aggregationperiod;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class Week implements AggregationPeriod {
	private static final Period WEEK = Period.weeks(1);
	public Period getPeriod() {
		return WEEK;
	}

	public String getName() {
		return "Week";
	}

	public Interval getNextCompleteInterval(DateTime time) {
		DateTime startTime = getNextCompleteStartTime(time);
		DateTime endTime = getNextCompleteEndTime(time);
		Interval nextCompleteInterval = new Interval(startTime,endTime);
		return nextCompleteInterval;
	}
	
	private DateTime getNextCompleteEndTime(DateTime time) {
		DateTime nextCompleteStartTime = getNextCompleteStartTime(time);
		DateTime nextCompleteEndTime = nextCompleteStartTime.plus(WEEK);
		return nextCompleteEndTime;
	}

	private DateTime getNextCompleteStartTime(DateTime time) {
		int startOfMonthYear = time.getYear();
		int monthOfYear = time.getMonthOfYear();
		int dayOfWeek = time.getDayOfWeek();
		//TODO finish implementation
		return null;
		/*
		DateTime startOfWeek = new DateTime(startOfMonthYear,monthOfYear,time.getDayOfMonth(),0,0,0);
		DateTime nextCompleteStartTime = startOfMonth.plusMonths(1);
		return nextCompleteStartTime;
		*/
	}

}
