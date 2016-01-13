package net.stevenuray.walletexplorer.persistence.timable;

import org.joda.time.DateTime;

public class BasicTimableWalletNameDataPipelineFactory<T,U> implements
		TimableWalletNameDataPipelineFactory<T, U> {
	private final TimableWalletNameDataProducerFactory<T> producerFactory;
	private final TimableWalletNameDataConsumerFactory<U> consumerFactory;
	
	public BasicTimableWalletNameDataPipelineFactory(
			TimableWalletNameDataProducerFactory<T> producerFactory,
			TimableWalletNameDataConsumerFactory<U> consumerFactory){
		this.producerFactory = producerFactory;
		this.consumerFactory = consumerFactory;
	}
	
	public TimableDataPipeline<T, U> getProducerConsumerPair(String walletName) {			
		TimableDataProducer<T> originalProducer = producerFactory.getDataProducer(walletName);	
		TimableDataConsumer<U> consumer = consumerFactory.getDataConsumer(walletName);	
		TimableDataPipeline<T,U> pair = new TimableDataPipeline<T,U>(originalProducer,consumer);
		return pair;
	}
}