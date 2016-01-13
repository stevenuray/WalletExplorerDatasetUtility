package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

public interface DataPipeline<T, U> {
	/**Wraps a call to consumer().consume(Iterator<U> iterator) for convenience	.
	 * @param iterator
	 */
	public abstract void consume(Iterator<U> iterator);

	/**Wraps a call to consumer().consume() for convenience. 
	 * @param u
	 */
	public abstract void consume(U u);

	/**Wraps a call to producer.getData() for convenience.	 
	 * @return - The iterator of the data producer. 
	 */
	public abstract Iterator<T> getData();

	public abstract DataConsumer<U> getConsumer();

	public abstract DataProducer<T> getProducer();
}