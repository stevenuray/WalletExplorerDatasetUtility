package net.stevenuray.walletexplorer.conversion.collection;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class QueueLoader<T> implements Callable<BlockingQueue<T>>{
	private final int maxQueueSize;
	private Iterator<T> source;
	
	public QueueLoader(int maxQueueSize,Iterator<T> source){
		this.maxQueueSize = maxQueueSize;
		this.source = source;
	}

	public BlockingQueue<T> call() throws Exception {
		BlockingQueue<T> queue = new ArrayBlockingQueue<T>(maxQueueSize);
		while(source.hasNext() && queue.size() < maxQueueSize){
			queue.add(source.next());
		}
		source = null;
		return queue;
	}
}
