package net.stevenuray.walletexplorer.conversion.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.FutureQueueConverterCallable;
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
	private final ExecutorService executor;	
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
		
		//TODO pass in the number of threads via constructor or a config
		this.executor = Executors.newFixedThreadPool(5);			
		log.debug("Collection Converter Created!");
	}	

	public ConversionResults convertCollection() throws ExecutionException, InterruptedException{			
		Iterator<WalletTransaction> producerIterator = dataPipeline.getData();
		List<Future<BulkOperationResult>> consumerPushResultFutures = new ArrayList<Future<BulkOperationResult>>();
		int transactionsConverted = 0; 
		while(producerIterator.hasNext()){				
			Future<BlockingQueue<WalletTransaction>> unconvertedQueueFuture = submitQueueLoader(producerIterator);
			Future<BlockingQueue<ConvertedWalletTransaction>> convertedQueueFuture = 
					submitConversion(unconvertedQueueFuture,executor);
			Future<BulkOperationResult> consumerPushResultFuture = submitConvertedQueue(convertedQueueFuture,executor);			
			consumerPushResultFutures.add(consumerPushResultFuture);			
			
			BulkOperationResult result = consumerPushResultFuture.get();
			transactionsConverted += result.getOperations();
			log.info("Transactions Count: "+transactionsConverted);						
		}	
		
		endCollectionConversion(consumerPushResultFutures);
		return conversionResults;
	}
		
	private void endCollectionConversion(List<Future<BulkOperationResult>> consumerPushResultFutures) 
			throws InterruptedException, ExecutionException{
		updateConversionResults(consumerPushResultFutures);		
		tryShutdownExecutorOrLogError();
		conversionResults.endConversion();
		logConversionStatistics();
	}
	
	private long getConversionResultsInSeconds() {
		Interval conversionTimespan = conversionResults.getConversionTimespan();
		Duration conversionDuration = conversionTimespan.toDuration();
		long conversionResultsInSeconds = conversionDuration.getStandardSeconds();
		return conversionResultsInSeconds;
	}
	
	private void logConversionStatistics(){
		log.info("Transactions Conversion Successes: "+conversionResults.getSuccessfulConversions());
		log.info("Transactions Conversion Failures: "+conversionResults.getFailedConversions());
		log.info("Conversion Time in Seconds: "+getConversionResultsInSeconds());
	}
	
	private Future<BlockingQueue<ConvertedWalletTransaction>> submitConversion(
			Future<BlockingQueue<WalletTransaction>> unconvertedQueueFuture,ExecutorService executor) {			
		FutureQueueConverterCallable<WalletTransaction,ConvertedWalletTransaction> converterCallable = 
				new FutureQueueConverterCallable<>(converter,unconvertedQueueFuture,maxQueueSize);
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