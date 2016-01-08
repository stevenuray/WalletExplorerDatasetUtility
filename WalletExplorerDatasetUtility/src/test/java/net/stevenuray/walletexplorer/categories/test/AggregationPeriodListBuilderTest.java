package net.stevenuray.walletexplorer.categories.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriodFactory;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriodListBuilder;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriodFactory.AggregationSize;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Test;

public class AggregationPeriodListBuilderTest {
	private DateTime end = new DateTime(2014,4,1,0,0,0);
	//AggregationPeriod parameter.
	private AggregationSize month = AggregationSize.MONTH;
	private AggregationPeriod monthPeriod = AggregationPeriodFactory.getAggregationPeriod(month);
	
	//Interval parameter.
	private DateTime start = new DateTime(2013,12,31,0,0,0);
	private Interval totalTimespan = new Interval(start,end);
	
	@Test
	public void returnsCorrectAscendingList(){
		//Setup			
		AggregationPeriodListBuilder builder = new AggregationPeriodListBuilder(totalTimespan,monthPeriod);
		
		//Exercise + Verify
		List<Interval> expectedIntervals = getExpectedIntervals();
		List<Interval> returnedIntervals = builder.getAscendingPeriodList();
		assertEquals(expectedIntervals,returnedIntervals);
	}

	@Test(expected=IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionIfTimespanIsShorterThanAggregationPeriodDuration() {
		//Setup		
		//Interval parameter.
		Duration totalTimespanDuration = Duration.standardDays(7);
		DateTime weekAgo = new DateTime().minus(totalTimespanDuration);
		DateTime rightNow = new DateTime();
		Interval totalTimespan = new Interval(weekAgo,rightNow);
		
		//AggregationPeriod parameter.
		AggregationSize month = AggregationSize.MONTH;
		AggregationPeriod monthPeriod = AggregationPeriodFactory.getAggregationPeriod(month);
		
		//Exercise + Verify
		fail("not yet fully implemented");	
	}

	private List<Interval> getExpectedIntervals() {
		DateTime januaryStart = new DateTime(2014,1,1,0,0,0);
		DateTime febuaryStart = new DateTime(2014,2,1,0,0,0);
		DateTime marchStart = new DateTime(2014,3,1,0,0,0);
		
		Interval january = new Interval(januaryStart,febuaryStart);
		Interval febuary = new Interval(febuaryStart,marchStart);
		
		List<Interval> expectedIntervals = new ArrayList<Interval>();
		expectedIntervals.add(january);
		expectedIntervals.add(febuary);
		return expectedIntervals;
	}
}