package net.stevenuray.walletexplorer.categories.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriod;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodFactory;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodListBuilder;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationPeriodFactory.AggregationSize;

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