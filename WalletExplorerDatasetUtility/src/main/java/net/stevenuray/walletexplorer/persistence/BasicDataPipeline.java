package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

/**Represents a source of data and a destination of data, for a complete segment on a data pipeline. 
 * @author Steven Uray 2015-12-19
 */
public class BasicDataPipeline<T,U> implements DataPipeline<T, U> {
	private final DataConsumer<U> consumer;
	private final DataProducer<T> producer;
	
	public BasicDataPipeline(DataProducer<T> producer,DataConsumer<U> consumer){
		this.producer = producer;
		this.consumer = consumer;
	}

	/* (non-Javadoc)
	 * @see net.stevenuray.walletexplorer.persistence.ProducerConsumerPair#consume(java.util.Iterator)
	 */
	public void consume(Iterator<U> iterator){
		consumer.consume(iterator);
	}

	/* (non-Javadoc)
	 * @see net.stevenuray.walletexplorer.persistence.ProducerConsumerPair#consume(U)
	 */
	public void consume(U u){
		consumer.consume(u);		
	}	
	
	/* (non-Javadoc)
	 * @see net.stevenuray.walletexplorer.persistence.ProducerConsumerPair#getConsumer()
	 */
	public DataConsumer<U> getConsumer() {
		return consumer;
	}
		
	/* (non-Javadoc)
	 * @see net.stevenuray.walletexplorer.persistence.ProducerConsumerPair#getData()
	 */
	public Iterator<T> getData(){
		return producer.getData();
	}
	
	/* (non-Javadoc)
	 * @see net.stevenuray.walletexplorer.persistence.ProducerConsumerPair#getProducer()
	 */
	public DataProducer<T> getProducer() {
		return producer;
	}		
}