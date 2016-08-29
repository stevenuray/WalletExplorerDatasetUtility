package net.stevenuray.walletexplorer.downloader.ratelimiting;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

public class RateLimitEvaluator {
	private final RateLimit rateLimit;
	//I wish this could be type safe :(
	private final Comparator<Object> dateTimeComparator = DateTimeComparator.getInstance();	
	private Queue<DateTime> eventTimes = new PriorityQueue<DateTime>(dateTimeComparator);
	
	public RateLimitEvaluator(RateLimit rateLimit){
		this.rateLimit = rateLimit;
	}
	 
	public void recordNewEvent(){
		eventTimes.add(new DateTime());
	}
	
	public boolean isRateLimitExceeded(){
		removeExpiredEventsFromQueue();
		return (eventTimes.size() > rateLimit.getQueryCount());
	}

	private void removeExpiredEventsFromQueue() {
		while(isNextEventWithinRateLimitTimespan() == false){
			eventTimes.poll();
		}		
	}	
	
	private boolean isNextEventWithinRateLimitTimespan(){
		if(eventTimes.size() == 0){
			return true;
		}
		DateTime nextEvent = eventTimes.peek();		
		DateTime oldestDateWithinRateTimeSpan = new DateTime().minus(rateLimit.getTimespan());
		return nextEvent.isAfter(oldestDateWithinRateTimeSpan);		
	}
}