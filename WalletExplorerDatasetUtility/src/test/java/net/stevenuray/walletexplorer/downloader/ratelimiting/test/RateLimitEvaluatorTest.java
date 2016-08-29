package net.stevenuray.walletexplorer.downloader.ratelimiting.test;

import static org.junit.Assert.*;
import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimit;
import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimitEvaluator;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class RateLimitEvaluatorTest {
	private final Duration rateTimespan = Duration.millis(100);
	private final RateLimit rateLimit = new RateLimit(10,rateTimespan);
	private RateLimitEvaluator evaluator;
	
	@Before
	public void setUp(){
		evaluator = new RateLimitEvaluator(rateLimit);
	}
	
	@Test
	public void returnsIsRateLimitExceededCorrectlyWhenItIsExceeded() {		
		recordEventsOnEvaluator(rateLimit.getQueryCount()+1);
		assertTrue(evaluator.isRateLimitExceeded());		
	}
	
	@Test
	public void returnsIsRateLimitExceededCorrectlyWhenItIsNotExceeded() {
		recordEventsOnEvaluator(rateLimit.getQueryCount()-1);
		assertFalse(evaluator.isRateLimitExceeded());
	}
	
	@Test
	public void returnsIsRateLimitExceededCorrectlyWhenEventsHaveExpired(){
		recordEventsOnEvaluator(rateLimit.getQueryCount()-1);
		try{
			Thread.sleep(rateLimit.getTimespan().getMillis()+100);
		} catch(Exception e){
			fail("Unexpected exception");
		}
		recordEventsOnEvaluator(rateLimit.getQueryCount()-1);
		assertFalse(evaluator.isRateLimitExceeded());
	}
	
	private void recordEventsOnEvaluator(int eventCount){
		for(int i = 0; i < eventCount; i++){
			evaluator.recordNewEvent();			
		}
	}
}
