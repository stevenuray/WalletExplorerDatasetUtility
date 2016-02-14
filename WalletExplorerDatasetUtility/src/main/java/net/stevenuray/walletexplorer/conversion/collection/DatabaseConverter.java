package net.stevenuray.walletexplorer.conversion.collection;

import java.util.Iterator;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.persistence.walletdatafactories.WalletNameDataPipelineFactory;
import net.stevenuray.walletexplorer.walletnames.WalletNames;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class DatabaseConverter {
	private final Interval conversionTimespan;
	private final Converter<WalletTransaction,ConvertedWalletTransaction> converter;
	private final Logger log = getLog();
	//TODO set the collection converter log in a more appropriate way. 
	private final Logger collectionConverterLog = getCollectionConverterLog();
	private final int maxQueueSize;
	private final int maxThreads;
	private final WalletNameDataPipelineFactory<WalletTransaction,ConvertedWalletTransaction> pipelineFactory;
	private final WalletNames walletNames;
		
	public DatabaseConverter(
			WalletNameDataPipelineFactory<WalletTransaction,ConvertedWalletTransaction> pipelineFactory,
			Converter<WalletTransaction,ConvertedWalletTransaction> converter,WalletNames walletNames,
			int maxQueueSize, int maxThreads, Interval conversionTimespan){
		this.pipelineFactory = pipelineFactory;
		this.converter = converter;
		this.walletNames = walletNames;
		this.maxQueueSize = maxQueueSize;
		this.maxThreads = maxThreads;
		this.conversionTimespan = conversionTimespan;
	}
	
	public void convertDatabase(){
		int collectionsConverted = 0;
		Iterator<String> walletNamesIterator = walletNames.iterator();		
		while(walletNamesIterator.hasNext()){
			String nextWalletName = walletNamesIterator.next();
			convertWalletData(nextWalletName);
			collectionsConverted++;
			log.info("Collections Converted: "+collectionsConverted+" / "+walletNames.size());
		}
	}

	private void convertWalletData(String walletName) {
		log.info("Converting Wallet: "+walletName);
		CollectionConverter collectionConverter = getCollectionConverter(walletName);
		try{
			ConversionResults conversionResults = collectionConverter.convertCollection();			
		} catch(Exception e){
			e.printStackTrace();
			log.error("Conversion failed for wallet: "+walletName+" !");
		}
		
		log.info("Finished converting transactions for "+walletName);
	}

	private CollectionConverter getCollectionConverter(String nextWalletName) {
		DataPipeline<WalletTransaction,ConvertedWalletTransaction> dataPipeline = 
				pipelineFactory.getDataPipeline(nextWalletName);		
		CollectionConverter collectionConverter = new CollectionConverter(
				dataPipeline,converter,conversionTimespan,maxQueueSize,maxThreads,collectionConverterLog);		
		return collectionConverter;
	}
	
	private Logger getCollectionConverterLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(CollectionConverter.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%t | %d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Console Appender");			
		log.addAppender(appender);
		return log;
	}

	private Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(DatabaseConverter.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%t | %d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Console Appender");			
		log.addAppender(appender);
		return log;
	}
}