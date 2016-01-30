package net.stevenuray.walletexplorer.conversion.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.QueueConverterCallable;
import net.stevenuray.walletexplorer.dto.BulkOperationResult;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.persistence.PushToConsumerCallable;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Interval;

public class CollectionConverter {	
	private final ConversionResults conversionResults = new ConversionResults();	
	private final Converter<WalletTransaction,ConvertedWalletTransaction> converter;
	private final DataPipeline<WalletTransaction, ConvertedWalletTransaction> dataPipeline;
	private final ThreadPoolExecutor executor;	
	private final Logger log;	
	private final int maxQueueSize;

	public CollectionConverter(
			DataPipeline<WalletTransaction, ConvertedWalletTransaction> dataPipeline,
			Converter<WalletTransaction,ConvertedWalletTransaction> converter,int maxQueueSize,Interval conversionTimespan,
			Logger log){			
		this.dataPipeline = dataPipeline;
		this.converter = converter;	
		this.maxQueueSize = maxQueueSize;		
		this.log = log;
		
		//TODO pass in the number of threads via constructor or a config.			
		this.executor = getExecutor(5);
		log.debug("Collection Converter Created!");
	}	

	public ConversionResults convertCollection() throws ExecutionException, InterruptedException{		
		dataPipeline.start();
		Iterator<WalletTransaction> producerIterator = dataPipeline.getData();
		List<Future<BulkOperationResult>> consumerPushResultFutures = new ArrayList<Future<BulkOperationResult>>();
		int transactionsLoaded = 0; 
		while(producerIterator.hasNext()){				
			Future<BlockingQueue<WalletTransaction>> unconvertedQueueFuture = submitQueueLoader(producerIterator);
			/*TODO find a way for multiple threads to call Mongo's implementation of DataProducer 
			 * without causing a MongoCursor errors, then remove this wait as it reduces concurrent performance. 		 
			 */
			/*Waiting for the QueueLoader to finish before submitting the future to the executor.
			 * failure to do this will break the Mongo implementation of DataProducer by creating 
			 * multiple users of a MongoCursor. 
			 */
			BlockingQueue<WalletTransaction> unconvertedQueue = unconvertedQueueFuture.get();
			transactionsLoaded += unconvertedQueue.size();
			log.info("Transactions Loaded: "+transactionsLoaded);
			Future<BlockingQueue<ConvertedWalletTransaction>> convertedQueueFuture = 
					submitConversion(unconvertedQueue,executor);
			Future<BulkOperationResult> consumerPushResultFuture = submitConvertedQueue(convertedQueueFuture,executor);			
			consumerPushResultFutures.add(consumerPushResultFuture);						
			/*
			BulkOperationResult result = consumerPushResultFuture.get();
			transactionsConverted += result.getOperations();
			log.info("Transactions Count: "+transactionsConverted);		
			*/							
		}	
		
		finishCollectionConversion(consumerPushResultFutures);		
		return conversionResults;
	}

	private void finishCollectionConversion(List<Future<BulkOperationResult>> consumerPushResultFutures) 
			throws InterruptedException, ExecutionException{
		updateConversionResults(consumerPushResultFutures);		
		tryShutdownExecutorOrLogError();
		dataPipeline.finish();
		conversionResults.endConversion();
		logConversionStatistics();		
	}
		
	private long getConversionResultsInSeconds() {
		Interval conversionTimespan = conversionResults.getConversionTimespan();
		Duration conversionDuration = conversionTimespan.toDuration();
		long conversionResultsInSeconds = conversionDuration.getStandardSeconds();
		return conversionResultsInSeconds;
	}
	
	private ThreadPoolExecutor getExecutor(int maxThreads) {
		long keepAliveSeconds = 60;
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
		RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				maxThreads,maxThreads,keepAliveSeconds,TimeUnit.SECONDS,queue,handler);		
		return threadPoolExecutor;
	}
	
	private void logConversionStatistics(){
		log.info("Transactions Conversion Successes: "+conversionResults.getSuccessfulConversions());
		log.info("Transactions Conversion Failures: "+conversionResults.getFailedConversions());
		log.info("Conversion Time in Seconds: "+getConversionResultsInSeconds());
	}
	
	private Future<BlockingQueue<ConvertedWalletTransaction>> submitConversion(
			BlockingQueue<WalletTransaction> unconvertedQueue,ExecutorService executor) 
					throws InterruptedException, ExecutionException {					
		QueueConverterCallable<WalletTransaction,ConvertedWalletTransaction> converterCallable = 
				new QueueConverterCallable<>(converter,unconvertedQueue,maxQueueSize);
		return executor.submit(converterCallable);
	}
	
	private Future<BulkOperationResult> submitConvertedQueue(
			Future<BlockingQueue<ConvertedWalletTransaction>> convertedQueueFuture,ExecutorService executor) {
		DataConsumer<ConvertedWalletTransaction> consumer = dataPipeline.getConsumer();
		PushToConsumerCallable<ConvertedWalletTransaction> consumerPusher = 
				new PushToConsumerCallable<>(convertedQueueFuture,consumer);
		return executor.submit(consumerPusher);
	}

	private Future<BlockingQueue<WalletTransaction>> submitQueueLoader(Iterator<WalletTransaction> producerIterator){
		QueueLoader<WalletTransaction> queueLoader = 
				new QueueLoader<WalletTransaction>(maxQueueSize,producerIterator);
		Future<BlockingQueue<WalletTransaction>> queueFuture = executor.submit(queueLoader);
		return queueFuture;
	}

	private void tryShutdownExecutorOrLogError(){
		executor.shutdown();
		try {
			executor.awaitTermination(1,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("Executor shutdown was interrupted! "
					+ "This may cause a memory leak or other unexpected errors!");
		}
	}

	private void updateConversionResults(List<Future<BulkOperationResult>> consumerPushResultFutures) 
			throws InterruptedException, ExecutionException{
		for(Future<BulkOperationResult> consumerPushResultFuture : consumerPushResultFutures){
			BulkOperationResult result = consumerPushResultFuture.get();
			for(int i = 0; i < result.getOperations(); i++){
				conversionResults.iterateSuccessfulConversions();
			}
		}
	}
}