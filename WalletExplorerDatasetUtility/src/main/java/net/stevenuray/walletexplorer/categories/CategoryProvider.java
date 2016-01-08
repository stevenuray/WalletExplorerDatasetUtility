package net.stevenuray.walletexplorer.categories;

import java.util.Iterator;

import net.stevenuray.walletexplorer.persistence.DataProducer;

import org.joda.time.Interval;

/**Provides a user with an iterator that will return all objects of a datatype for a wallet name within a 
 * given category.
 * @author Steven Uray 2016-01-02
 */
public interface CategoryProvider<T> {
	public Iterator<DataProducer<T>> getDataProducers(WalletCategory walletCategory,Interval timespan);
}
