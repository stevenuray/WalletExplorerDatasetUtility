package net.stevenuray.walletexplorer.persistence.timable;

import net.stevenuray.walletexplorer.persistence.DataConsumer;

import org.joda.time.DateTime;

public interface TimableDataConsumer<T> extends DataConsumer<T> {
	public DateTime getEarliestTime();
	public DateTime getLatestTime();
}
