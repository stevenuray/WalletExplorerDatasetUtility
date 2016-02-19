package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

/**This interface separates the ability to consume data from what to do with the consumed data.
 * The purpose of this is to allow converters or aggregators to ignore what data consumers do or 
 * who they are.
 *
 * @param <T>
 */
public interface DataConsumer<T> extends DataPipelineComponent{
	void consume(Iterator<T> producer);
	void consume(T t);
}
