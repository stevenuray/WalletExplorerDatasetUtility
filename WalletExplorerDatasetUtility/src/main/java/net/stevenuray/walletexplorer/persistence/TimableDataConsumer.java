package net.stevenuray.walletexplorer.persistence;

import org.joda.time.DateTime;

public interface TimableDataConsumer<T> extends DataConsumer<T> {
	public DateTime getEarliestTime();
	public DateTime getLatestTime();
}
