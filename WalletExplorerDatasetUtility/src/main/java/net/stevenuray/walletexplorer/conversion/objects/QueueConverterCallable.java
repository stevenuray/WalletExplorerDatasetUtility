package net.stevenuray.walletexplorer.conversion.objects;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**Wraps QueueConverter in a Callable, making it useful for concurrent operations.  
 * @author Steven Uray  
 */
public class QueueConverterCallable<T,U> implements Callable<BlockingQueue<U>>{
	private final QueueConverter<T,U> queueConverter;
	private final BlockingQueue<T> originalQueue;
	private final int maxQueueSize;
	
	public QueueConverterCallable(Converter<T,U> converter,BlockingQueue<T> originalQueue,int maxQueueSize){
		queueConverter = new QueueConverter<T,U>(converter);		
		this.originalQueue = originalQueue;
		this.maxQueueSize = maxQueueSize;
	}

	public BlockingQueue<U> call() throws Exception {		
		return queueConverter.getConvertedQueue(originalQueue, maxQueueSize);
	}	
}