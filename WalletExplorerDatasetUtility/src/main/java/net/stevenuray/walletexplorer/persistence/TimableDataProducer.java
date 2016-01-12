package net.stevenuray.walletexplorer.persistence;

import org.joda.time.DateTime;

public interface TimableDataProducer<T> extends DataProducer<T> {
	public DateTime getEarliestTime();
	public DateTime getLatestTime();
	/**Returns a producer that produces data after the given time. 
	 * 
	 * @param earliestTime - Data before this time should not come from the producer. 
	 * @return - A producer that produces data after the given time. 
	 */
	public TimableDataProducer<T> fromTime(DateTime earliestTime);
}