package net.stevenuray.walletexplorer.aggregation.aggregationperiod.test;

import static org.junit.Assert.*;
import net.stevenuray.walletexplorer.aggregation.aggregationperiod.Year;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

public class YearTest {

	@Test
	public void returnsNextCompleteIntervalCorrectly() {
		//Setup
		DateTime startTime = new DateTime(2014,01,30,0,0,0);
		
		//Exercise
		Year year = new Year();
		Interval returnedInterval = year.getNextCompleteInterval(startTime);
		
		//Verify
		DateTime expectedStart = new DateTime(2015,1,1,0,0,0);
		DateTime expectedEnd = new DateTime(2016,1,1,0,0,0);
		Interval expectedInterval = new Interval(expectedStart,expectedEnd);
		assertEquals(expectedInterval,returnedInterval);
	}
}