package net.stevenuray.walletexplorer.executables;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.conversion.objects.DirectConverter;
import net.stevenuray.walletexplorer.downloader.Downloader;
import net.stevenuray.walletexplorer.downloader.WalletExplorerAPIConfigSingleton;
import net.stevenuray.walletexplorer.persistence.DataPipelineFactories;
import net.stevenuray.walletexplorer.persistence.timable.BasicTimableWalletNameDataPipelineFactory;
import net.stevenuray.walletexplorer.walletnames.TextFileWalletNamesFactory;
import net.stevenuray.walletexplorer.walletnames.WalletNames;
import net.stevenuray.walletexplorer.walletnames.WalletNamesFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;

public class TransactionDownloader {
	private static final Logger LOG = getLog();	
	
	public static void main(String[] args) {
		disableMongoLogInfo();		
		executeNewDownloadCycle();
	}
		
	private static int calculateWalletNamesLength(Iterator<String> walletNames){
		int count = 0; 
		while(walletNames.hasNext()){
			walletNames.next();
			count++;
		}
		return count;
	}
		
	private static void disableMongoLogInfo(){
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}

	private static void executeNewDownloadCycle(){
		logNewDownloadCycleStart();		
		WalletNames walletNames = getWalletNamesOrQuit();
		LOG.info("Wallets to download: "+walletNames.size());
		Iterator<String> walletNamesIterator = walletNames.iterator();
		Downloader<WalletTransaction,WalletTransaction> walletExplorerDownloader = getDownloader(walletNamesIterator);
		walletExplorerDownloader.downloadAndSaveAllWalletTransactions();
	}
	
	private static Downloader<WalletTransaction,WalletTransaction> getDownloader(Iterator<String> walletNames){
		BasicTimableWalletNameDataPipelineFactory<WalletTransaction,WalletTransaction> pipelineFactory = 
				DataPipelineFactories.getWalletExplorerToMongoDB();
		Converter<WalletTransaction,WalletTransaction> directConverter = new DirectConverter<>();
		Downloader<WalletTransaction,WalletTransaction> walletExplorerDownloader = 
				new Downloader<>(pipelineFactory,walletNames,directConverter);
		return walletExplorerDownloader;
	}

	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(Log4JTest.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Test Appender");			
		log.addAppender(appender);
		return log;
	}
		
	private static WalletNames getWalletNamesOrQuit() {
		WalletNamesFactory factory = new TextFileWalletNamesFactory();
		WalletNames walletNames = null;
		try{
			walletNames = factory.getWalletNames();
		} catch(Exception e){
			e.printStackTrace();
			LOG.fatal("Could not locate wallet names, and therefore could not convert collections!");
			System.exit(0);
		}
		return walletNames;
	}
	
	private static void logNewDownloadCycleStart(){
		LOG.info("*****BEGINNING DOWNLOAD OF TRANSACTIONS FROM WALLETEXPLORER.COM***** ");
		LOG.info("Attempting to download latest transactions from WalletExplorer.com's API!");
		LOG.info("Download Start is: "+new DateTime());		
	}			
}