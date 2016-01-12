package net.stevenuray.walletexplorer.persistence;

import org.joda.time.DateTime;

public interface TimableDataProducer {
	public DateTime getEarliestTime();
	public DateTime getLatestTime();
}
