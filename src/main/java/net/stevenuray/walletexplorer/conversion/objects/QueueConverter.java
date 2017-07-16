package net.stevenuray.walletexplorer.conversion.objects;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueConverter<T,U> {
	private final Converter<T,U> converter;	
	
	public QueueConverter(Converter<T,U> converter){
		this.converter = converter;
	}
	
	public BlockingQueue<U> getConvertedQueue(BlockingQueue<T> originalQueue, int maxQueueSize) 
			throws InterruptedException{
		BlockingQueue<U> convertedQueue = new ArrayBlockingQueue<U>(maxQueueSize);
		for(T original : originalQueue){
			U converted = converter.to(original);
			convertedQueue.put(converted);
		}		
		return convertedQueue;
	}
}
