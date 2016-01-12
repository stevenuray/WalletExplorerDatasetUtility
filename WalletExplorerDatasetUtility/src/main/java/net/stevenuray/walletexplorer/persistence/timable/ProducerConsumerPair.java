package net.stevenuray.walletexplorer.persistence.timable;

import java.util.Iterator;

import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataProducer;

/**Represents a source of data and a destination of data, for a complete segment on a data pipeline. 
 * @author Steven Uray 2015-12-19
 */
public class ProducerConsumerPair<T,U> {
	private final DataConsumer<U> consumer;
	private final DataProducer<T> producer;
	
	public ProducerConsumerPair(DataProducer<T> producer,DataConsumer<U> consumer){
		this.producer = producer;
		this.consumer = consumer;
	}

	/**Wraps a call to consumer().consume(Iterator<U> iterator) for convenience	.
	 * @param iterator
	 */
	public void consume(Iterator<U> iterator){
		consumer.consume(iterator);
	}

	/**Wraps a call to consumer().consume() for convenience. 
	 * @param u
	 */
	public void consume(U u){
		consumer.consume(u);		
	}	
	
	/**Wraps a call to producer.getData() for convenience.	 
	 * @return - The iterator of the data producer. 
	 */
	public Iterator<T> getData(){
		return producer.getData();
	}
		
	public DataConsumer<U> getConsumer() {
		return consumer;
	}
	
	public DataProducer<T> getProducer() {
		return producer;
	}
		
	public Iterator<T> getProducerIterator(){		
		Iterator<T> producerIterator = producer.getData();
		return producerIterator;
	}	
}