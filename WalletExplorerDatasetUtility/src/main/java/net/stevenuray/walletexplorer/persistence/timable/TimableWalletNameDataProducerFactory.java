package net.stevenuray.walletexplorer.persistence.timable;

import org.joda.time.Interval;

import net.stevenuray.walletexplorer.persistence.WalletNameDataProducerFactory;

public interface TimableWalletNameDataProducerFactory<T> extends WalletNameDataProducerFactory<T>{
	/**Returns a DataProducer<T> that will return all data regardless of time.*/	 
	public TimableDataProducer<T> getDataProducer(String walletName);
	public TimableDataProducer<T> getDataProducer(String walletName,Interval timespan);	
}