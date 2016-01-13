package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.DataProducer;

import org.joda.time.DateTime;

public interface TimableDataProducer<T> extends DataProducer<T> {
	public DateTime getEarliestTime() throws TimeNotFoundException;
	public DateTime getLatestTime() throws TimeNotFoundException;
	//TODO perhaps add a TimeNotAvailable exception when invalid earliest times are given? 
	/**Returns a producer that produces data after the given time. 
	 * 
	 * @param earliestTime - Data before this time should not come from the producer. 
	 * @return - A producer that produces data after the given time. 
	 */	
	public TimableDataProducer<T> fromTime(DateTime earliestTime);
}