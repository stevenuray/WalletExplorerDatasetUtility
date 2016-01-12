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
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.ProducerConsumerPair;
import net.stevenuray.walletexplorer.persistence.WalletNameDataProducerConsumerFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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

	private void downloadWalletTransactions(String walletName) throws InterruptedException, ExecutionException {	
		ProducerConsumerPair<T,U> producerConsumerPair = producerConsumerFactory.getProducerConsumerPair(walletName);
		DataConsumer<U> consumer = producerConsumerPair.getConsumer();		
		Iterator<T> producerIterator = producerConsumerPair.getProducerIterator();
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
		
		int transactionsDownloaded = 0; 
		while(producerIterator.hasNext()){			
			QueueLoader<T> queueLoader = new QueueLoader<T>(MAX_QUEUE_SIZE,producerIterator);
			Future<BlockingQueue<T>> queueFuture = executor.submit(queueLoader);
			tryConvertAndPushToConsumer(queueFuture,consumer);		
			transactionsDownloaded+=MAX_QUEUE_SIZE;
			LOG.info("Transactions Downloaded for "+walletName+": "+transactionsDownloaded);
		}
		
		executor.shutdown();
		/*
		LOG.info("Downloading Wallet Transactions After: "+walletEndTime+" From: "+nextWalletName);		
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);		
				
		logDownloadResult(nextWalletName,downloadResultFuture);
		executor.shutdown();
		LOG.info("Download Complete For: "+nextWalletName);		
		*/	
	}	
	
	private BlockingQueue<U> getConvertedQueue(BlockingQueue<T> originalQueue) throws InterruptedException {
		BlockingQueue<U> convertedQueue = new ArrayBlockingQueue<U>(MAX_QUEUE_SIZE);
		for(T original : originalQueue){
			U converted = converter.to(original);
			convertedQueue.put(converted);
		}		
		return convertedQueue;
	}

	private void tryConvertAndPushToConsumer(Future<BlockingQueue<T>> queueFuture,DataConsumer<U> consumer)
			throws InterruptedException, ExecutionException{
		BlockingQueue<T> queue = queueFuture.get();
		BlockingQueue<U> convertedQueue = getConvertedQueue(queue);
		/*Bulk insertion is attempted first for performance. If this fails, individual 
		 * insertion is attempted so any transactions within a block of transactions that can make it 
		 * into the database will make it into the database. Failure to insert individual transactions 
		 * when a bulk transaction insert fails will result in a dataset that is incomplete. 
		 */
		try{
			tryPushToConsumerInBulk(convertedQueue,consumer);
		} catch(Exception e){
			//TODO DEVELOPMENT 
			e.printStackTrace();
			tryPushToConsumerIndividually(convertedQueue,consumer);
		}
	}

	private void tryPushToConsumerInBulk(BlockingQueue<U> convertedQueue,DataConsumer<U> consumer) {
		Iterator<U> iterator = convertedQueue.iterator();
		consumer.consume(iterator);		
	}

	private void tryPushToConsumerIndividually(BlockingQueue<U> convertedQueue,DataConsumer<U> consumer) {
		Iterator<U> iterator = convertedQueue.iterator();
		while(iterator.hasNext()){
			U next = iterator.next();
			consumer.consume(next);
		}
	}
}