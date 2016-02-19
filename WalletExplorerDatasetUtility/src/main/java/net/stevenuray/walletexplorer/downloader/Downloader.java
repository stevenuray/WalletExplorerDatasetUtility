package net.stevenuray.walletexplorer.downloader;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.stevenuray.walletexplorer.conversion.collection.QueueLoader;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.QueueConverterCallable;
import net.stevenuray.walletexplorer.dto.BulkOperationResult;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.persistence.ConsumerPusher;
import net.stevenuray.walletexplorer.persistence.walletdatafactories.WalletNameDataPipelineFactory;
import net.stevenuray.walletexplorer.walletnames.WalletNames;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.Duration;

public class Downloader<T,U> {
	private static final Logger LOG = getLog();			
		
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
	private final WalletNameDataPipelineFactory<T,U> dataPipelineFactory;		
	private final WalletNames walletNames;
	
	public Downloader(
			WalletNameDataPipelineFactory<T,U> producerConsumerFactory,
			WalletNames walletNames,Converter<T,U> converter){
		this.dataPipelineFactory = producerConsumerFactory;
		this.walletNames = walletNames;
		this.converter = converter;
	}

	public void downloadAndSaveAllWalletTransactions() {		
		int walletsDownloaded = 0; 
		Iterator<String> walletNamesIterator = walletNames.iterator();
		while (walletNamesIterator.hasNext()) {
			String nextWalletName = walletNamesIterator.next();
			try {								
				LOG.info("Downloading Wallet: "+nextWalletName);
				downloadWalletTransactions(nextWalletName);
				walletsDownloaded++;
				LOG.info("Wallets Downloaded: "+walletsDownloaded);
			} catch (Exception e) {
				LOG.error("Failed to download wallet: "+nextWalletName);
				e.printStackTrace();
				continue;
			}
		}
		
		LOG.info("Total Transactions Downloaded: " + masterTotalInputTransactions);
		LOG.info("Total Transactions Inserted: " + masterTotalInsertedTransactions);
		LOG.info("End Time: " + new Date());
	}

	private void downloadWalletTransactions(String walletName) throws Exception {	
		BulkOperationResult result = new BulkOperationResult();
		DataPipeline<T, U> dataPipeline = dataPipelineFactory.getDataPipeline(walletName);
		dataPipeline.start();
		DataConsumer<U> consumer = dataPipeline.getConsumer();		
		Iterator<T> producerIterator = dataPipeline.getData();
		ExecutorService executor = Executors.newFixedThreadPool(WalletExplorerConfig.MAX_THREADS);
				
		int transactionsDownloaded = 0; 
		while(producerIterator.hasNext()){			
			QueueLoader<T> queueLoader = new QueueLoader<T>(WalletExplorerConfig.MAX_QUEUE_LENGTH,producerIterator);
			//Api restrictions dictate we can only call the API with one thread at a time. 
			//Download -> Convert -> Consume
			BlockingQueue<T> downloadedQueue = queueLoader.call();				
			Future<BlockingQueue<U>> convertedQueueFuture = submitConversion(executor,downloadedQueue);
			Future<BulkOperationResult> consumerFuture = submitToConsumer(executor,convertedQueueFuture,consumer);
			
			//Waiting for this future so an exception will be thrown if there is a problem. 
			//TODO verify this wait does not make the concurrency useless, fix it if it does. 
			consumerFuture.get();		
			transactionsDownloaded+=downloadedQueue.size();
			LOG.info("Transactions Downloaded for "+walletName+": "+transactionsDownloaded);
		}
		
		executor.shutdown();
		dataPipeline.finish();
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
				new QueueConverterCallable<T,U>(converter,originalQueue,WalletExplorerConfig.MAX_QUEUE_LENGTH);
		Future<BlockingQueue<U>> convertedQueueFuture = executor.submit(converterCallable);
		return convertedQueueFuture;
	}
	
	private Future<BulkOperationResult> submitToConsumer(
			ExecutorService executor,Future<BlockingQueue<U>> convertedQueueFuture,DataConsumer<U> consumer){
		ConsumerPusher<U> consumerPusher = 
				new ConsumerPusher<U>(convertedQueueFuture,consumer);
		Future<BulkOperationResult> consumerPushFuture = executor.submit(consumerPusher);
		return consumerPushFuture;
	}
}