package net.stevenuray.walletexplorer.downloader;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.stevenuray.walletexplorer.conversion.collection.QueueLoader;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.FutureQueueConverterCallable;
import net.stevenuray.walletexplorer.conversion.objects.QueueConverterCallable;
import net.stevenuray.walletexplorer.dto.BulkOperationResult;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.ProducerConsumerPair;
import net.stevenuray.walletexplorer.persistence.PushToConsumerCallable;
import net.stevenuray.walletexplorer.persistence.WalletNameDataProducerConsumerFactory;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.Duration;

public class Downloader<T,U> {
	private static final Logger LOG = getLog();			
	//TODO pass this in via constructor. 
	private static final int MAX_QUEUE_SIZE = 1000;	
	private static final int MAX_THREADS = 5;
	private static final int MAXIMUM_INSERTS = 100;
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(Downloader.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Test Appender");			
		log.addAppender(appender);
		return log;
	}
	
	private final Converter<T,U> converter;	
	private int masterTotalInputTransactions = 0;
	private int masterTotalInsertedTransactions = 0;
	private final WalletNameDataProducerConsumerFactory<T,U> producerConsumerFactory;		
	private final Iterator<String> walletNames;
	
	public Downloader(
			WalletNameDataProducerConsumerFactory<T,U> producerConsumerFactory,
			Iterator<String> walletNames,Converter<T,U> converter){
		this.producerConsumerFactory = producerConsumerFactory;
		this.walletNames = walletNames;
		this.converter = converter;
	}

	public void downloadAndSaveAllWalletTransactions() {		
		int walletsDownloaded = 0; 
		while (walletNames.hasNext()) {
			try {				
				String nextWalletName = walletNames.next();
				LOG.info("Downloading Wallet: "+nextWalletName);
				downloadWalletTransactions(nextWalletName);
				walletsDownloaded++;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		LOG.info("Total Transactions Downloaded: " + masterTotalInputTransactions);
		LOG.info("Total Transactions Inserted: " + masterTotalInsertedTransactions);
		LOG.info("End Time: " + new Date());
	}

	private void downloadWalletTransactions(String walletName) throws Exception {	
		BulkOperationResult result = new BulkOperationResult();
		ProducerConsumerPair<T,U> producerConsumerPair = producerConsumerFactory.getProducerConsumerPair(walletName);
		DataConsumer<U> consumer = producerConsumerPair.getConsumer();		
		Iterator<T> producerIterator = producerConsumerPair.getProducerIterator();
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
				
		int transactionsDownloaded = 0; 
		while(producerIterator.hasNext()){			
			QueueLoader<T> queueLoader = new QueueLoader<T>(MAX_QUEUE_SIZE,producerIterator);
			//Api restrictions dictate we can only call the API with one thread at a time. 
			//Download -> Convert -> Consume
			BlockingQueue<T> downloadedQueue = queueLoader.call();				
			Future<BlockingQueue<U>> convertedQueueFuture = submitConversion(executor,downloadedQueue);
			Future<BulkOperationResult> consumerFuture = submitToConsumer(executor,convertedQueueFuture,consumer);
			
			//Waiting for this future so an exception will be thrown if there is a problem. 
			consumerFuture.get();		
			transactionsDownloaded+=MAX_QUEUE_SIZE;
			LOG.info("Transactions Downloaded for "+walletName+": "+transactionsDownloaded);
		}
		
		executor.shutdown();
		result.complete();
		logResult(walletName,result);		
	}
		
	private void logResult(String walletName,BulkOperationResult result){
		Duration resultDuration = result.getTimeSpan().toDuration();	
		String resultString = "Downloaded Wallet "+walletName+" in: ";		
		long resultSeconds = resultDuration.getStandardSeconds();
		LOG.info(resultString+resultSeconds+" Seconds.");		
	}
	
	private Future<BlockingQueue<U>> submitConversion(
			ExecutorService executor,BlockingQueue<T> originalQueue){
		QueueConverterCallable<T,U> converterCallable = 
				new QueueConverterCallable<T,U>(converter,originalQueue,MAX_QUEUE_SIZE);
		Future<BlockingQueue<U>> convertedQueueFuture = executor.submit(converterCallable);
		return convertedQueueFuture;
	}
	
	private Future<BulkOperationResult> submitToConsumer(
			ExecutorService executor,Future<BlockingQueue<U>> convertedQueueFuture,DataConsumer<U> consumer){
		PushToConsumerCallable<U> consumerPusher = 
				new PushToConsumerCallable<U>(convertedQueueFuture,consumer);
		Future<BulkOperationResult> consumerPushFuture = executor.submit(consumerPusher);
		return consumerPushFuture;
	}
}