package net.stevenuray.walletexplorer.mongodb.converters.test;

import static org.junit.Assert.*;
import net.stevenuray.walletexplorer.aggregator.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.mongodb.converters.IntervalSumDocumentConverter;
import net.stevenuray.walletexplorer.testobjects.TestIntervalSums;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

public class IntervalSumDocumentConverterTest {
	private IntervalSumDocumentConverter converter = new IntervalSumDocumentConverter();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void roundTripsWithoutException() {
		//Setup
		IntervalSum intervalSum = TestIntervalSums.getIntervalSum();
		
		//Verify + Exercise
		Document intervalSumDocument = converter.to(intervalSum);
		@SuppressWarnings("unused")
		IntervalSum intervalSumFromDocument = converter.from(intervalSumDocument);		
	}
	
	@Test
	public void returnsNonNullDocument(){
		//Setup
		IntervalSum intervalSum = TestIntervalSums.getIntervalSum();

		//Verify 
		Document intervalSumDocument = converter.to(intervalSum);
		
		//Exercise
		assertNotNull(intervalSumDocument);	
	}
	
	@Test
	public void returnsNonNullObject(){
		//Setup
		IntervalSum intervalSum = TestIntervalSums.getIntervalSum();

		//Verify 
		Document intervalSumDocument = converter.to(intervalSum);
		IntervalSum intervalSumFromDocument = converter.from(intervalSumDocument);	
		
		//Exercise
		assertNotNull(intervalSumFromDocument);	
	}
}