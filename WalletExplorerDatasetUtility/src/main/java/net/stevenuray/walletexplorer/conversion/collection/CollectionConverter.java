package net.stevenuray.walletexplorer.conversion.collection;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.stevenuray.walletexplorer.conversion.currency.WalletTransactionCurrencyConverter;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.apache.log4j.Logger;
import org.joda.time.Interval;

public class CollectionConverter {	
	private final WalletTransactionCurrencyConverter converter;	
	private final DataPipeline<WalletTransaction, ConvertedWalletTransaction> producerConsumerPair;
	private final ConversionResults conversionResults = new ConversionResults();
	private final ExecutorService executor;	
	private final Logger log;
	private final Interval conversionTimespan;
	private final int maxQueueSize;

	public CollectionConverter(
			DataPipeline<WalletTransaction, ConvertedWalletTransaction> producerConsumerPair,
			WalletTransactionCurrencyConverter converter,int maxQueueSize,Interval conversionTimespan,
			Logger log){			
		this.producerConsumerPair = producerConsumerPair;
		this.converter = converter;	
		this.maxQueueSize = maxQueueSize;
		this.conversionTimespan = conversionTimespan;
		this.log = log;
		//TODO pass in the number of threads via constructor or a config
		this.executor = Executors.newFixedThreadPool(5);		

		//TODO pass this in via constructor when enabling conversions other than usd. 
		String currencySymbol = "USD";		
		log.info("Collection Converter Created!");
	}	

	public ConversionResults convertCollection() throws ExecutionException, InterruptedException{			
		Iterator<WalletTransaction> producerIterator = producerConsumerPair.getData();
		//TODO introduce more concurrency here.
		while(producerIterator.hasNext()){				
			QueueLoader<WalletTransaction> queueLoader = 
					new QueueLoader<WalletTransaction>(maxQueueSize,producerIterator);
			Future<BlockingQueue<WalletTransaction>> queueFuture = executor.submit(queueLoader);
			tryConvertAndPushToConsumer(queueFuture);				
		}	

		executor.shutdown();
		try {
			executor.awaitTermination(1,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("Executor shutdown was interrupted! "
					+ "This may cause a memory leak or other unexpected errors!");
		}
		log.info("Transactions Conversion Successes: "+conversionResults.getSuccessfulConversions());
		log.info("Transactions Conversion Failures: "+conversionResults.getFailedConversions());
		return conversionResults;
	}

	private BlockingQueue<ConvertedWalletTransaction> getConvertedQueue(BlockingQueue<WalletTransaction> queue){
		BlockingQueue<ConvertedWalletTransaction> convertedQueue = 
				new ArrayBlockingQueue<ConvertedWalletTransaction>(maxQueueSize);
		for(WalletTransaction walletTransaction : queue){
			ConvertedWalletTransaction convertedTransaction = converter.convert(walletTransaction);
			convertedQueue.add(convertedTransaction);			
		}
		return convertedQueue;
	}

	private void tryConvertAndPushToConsumer(Future<BlockingQueue<WalletTransaction>> queueFuture) 
			throws InterruptedException, ExecutionException{
		BlockingQueue<WalletTransaction> queue = queueFuture.get();
		BlockingQueue<ConvertedWalletTransaction> convertedQueue = getConvertedQueue(queue);
		/*Bulk insertion is attempted first for performance. If this fails, individual 
		 * insertion is attempted so any transactions within a block of transactions that can make it 
		 * into the database will make it into the database. Failure to insert individual transactions 
		 * when a bulk transaction insert fails will result in a dataset that is incomplete. 
		 */
		try{
			tryConvertAndPushToConsumerInBulk(convertedQueue);
		} catch(Exception e){
			tryConvertAndPushToConsumerIndividually(convertedQueue);
		}
	}			

	private void tryConvertAndPushToConsumerInBulk(BlockingQueue<ConvertedWalletTransaction> convertedQueue){
		Iterator<ConvertedWalletTransaction> convertedTransactionIterator = convertedQueue.iterator();		
		producerConsumerPair.consume(convertedTransactionIterator);	
		for(int i = 0; i < convertedQueue.size(); i++){
			conversionResults.iterateSuccessfulConversions();
		}
	}

	private void tryConvertAndPushToConsumerIndividually(BlockingQueue<ConvertedWalletTransaction> convertedQueue){
		for(ConvertedWalletTransaction convertedWalletTransaction : convertedQueue){
			try{
				producerConsumerPair.consume(convertedWalletTransaction);
				conversionResults.iterateSuccessfulConversions();
			} catch(Exception e){
				/*Note: The most common exception here is a duplicate primary key exception from the database. 
				 * This happens when transactions which have already been converted and inserted are inserted again. 
				 */
				conversionResults.iterateFailedConversions();							
			}
		}
	}
}