package net.stevenuray.walletexplorer.persistence.timable;

import java.util.Iterator;

import net.stevenuray.walletexplorer.persistence.DataPipeline;

import org.joda.time.DateTime;

/**Special ProducerConsumerPair that uses the TimableExtension to DataProducer when 
 * returning objects. The Consumer tells the Producer what data it 
 * already has based on time, so the Producer can avoid sending redundant data to the Consumer. 
 * @author Steven Uray 
 *
 * @param <T>
 * @param <U>
 */
public class TimableDataPipeline<T,U> implements DataPipeline<T,U>{
	private final TimableDataProducer<T> producer;
	private final TimableDataConsumer<U> consumer;
	
	public TimableDataProducer<T> getProducer() {
		return producer;
	}

	public TimableDataConsumer<U> getConsumer() {
		return consumer;
	}
		
	public TimableDataPipeline<T,U> getTimableProducerConsumerPair(){
		return new TimableDataPipeline<T,U>(producer,consumer);		
	}
	
	/**Note this constructor will change the producer in response to what data the consumer says it needs.	 
	 * @param producer
	 * @param consumer
	 */
	public TimableDataPipeline(TimableDataProducer<T> producer,TimableDataConsumer<U> consumer) {
		DateTime latestConsumerTime = consumer.getLatestTime();		
		this.producer = producer.fromTime(latestConsumerTime);
		this.consumer = consumer;
	}

	public void consume(Iterator<U> iterator) {
		consumer.consume(iterator);		
	}

	public void consume(U u) {
		consumer.consume(u);		
	}

	public Iterator<T> getData() {
		return producer.getData();
	}
}