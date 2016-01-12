package net.stevenuray.walletexplorer.conversion.objects;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**Accepts a Future<BlockingQueue<T>> and converts it. 
 * @author Steven Uray 
 */
public class FutureQueueConverterCallable<T,U> implements Callable<BlockingQueue<U>>{
	private final Future<BlockingQueue<T>> originalQueueFuture;
	private final int maxQueueSize;
	private final Converter<T,U> converter;
	
	public FutureQueueConverterCallable(
			Converter<T,U> converter,Future<BlockingQueue<T>> originalQueueFuture,int maxQueueSize){
		this.originalQueueFuture = originalQueueFuture;
		this.maxQueueSize = maxQueueSize;		
		this.converter = converter;
	}

	public BlockingQueue<U> call() throws Exception {
		BlockingQueue<T> originalQueue = originalQueueFuture.get();
		QueueConverter<T,U> queueConverter = new QueueConverter<T,U>(converter);
		BlockingQueue<U> convertedQueue = queueConverter.getConvertedQueue(originalQueue, maxQueueSize);		
		return convertedQueue;
	}
}
