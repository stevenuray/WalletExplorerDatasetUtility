package net.stevenuray.walletexplorer.persistence;

import org.joda.time.DateTime;

/**Special ProducerConsumerPair that uses the TimableExtension to DataProducer when 
 * returning objects. The Consumer tells the Producer what data it 
 * already has based on time, so the Producer can avoid sending redundant data to the Consumer. 
 * @author Steven Uray 
 *
 * @param <T>
 * @param <U>
 */
public class TimableProducerConsumerPair<T,U> {
	private final TimableDataProducer<T> producer;
	private final TimableDataConsumer<U> consumer;
	
	public TimableDataProducer<T> getProducer() {
		return producer;
	}

	public TimableDataConsumer<U> getConsumer() {
		return consumer;
	}
	
	/*
	public TimableProducerConsumerPair<T,U> getTimableProducerConsumerPair(){
		DateTime latestConsumerTime = consumer.getEarliestTime();
		
		//DataProducer<T> producer = 
	}
	*/

	public TimableProducerConsumerPair(TimableDataProducer<T> producer,TimableDataConsumer<U> consumer) {
		this.producer = producer;
		this.consumer = consumer;
	}
}