package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.stevenuray.walletexplorer.dto.BulkOperationResult;

/**Pushing data to a DataConsumer may be time consuming, as this usually involves storing 
 * data in some way. This callable is meant to wrap the DataConsumer.consume() function to enable
 * this operation to be performed concurrently. 
 * @author Steven Uray 
 *
 * @param <T>
 */
public class PushToConsumerCallable<T> implements Callable<BulkOperationResult>{
	private final Future<BlockingQueue<T>> sourceFuture;
	private final DataConsumer<T> consumer;
	
	public PushToConsumerCallable(Future<BlockingQueue<T>> sourceFuture,DataConsumer<T> consumer){
		this.sourceFuture = sourceFuture;
		this.consumer = consumer;
	}

	public BulkOperationResult call() throws Exception {	
		BulkOperationResult result = new BulkOperationResult();
		BlockingQueue<T> blockingQueue = sourceFuture.get();		
		Iterator<T> source = blockingQueue.iterator();
		consumer.consume(source);
		result.complete();		
		return result;
	}
}