package net.stevenuray.walletexplorer.executables;

import java.util.logging.Level;

import net.stevenuray.walletexplorer.conversion.collection.DatabaseConverter;
import net.stevenuray.walletexplorer.conversion.currency.CoindeskBpiConverter;
import net.stevenuray.walletexplorer.conversion.currency.HistoricalBTCToUSDConverter;
import net.stevenuray.walletexplorer.conversion.currency.HistoricalWalletTransactionConverter;
import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.DataPipelineFactories;
import net.stevenuray.walletexplorer.persistence.timable.BasicTimableWalletNameDataFactory;
import net.stevenuray.walletexplorer.walletnames.TextFileWalletNamesFactory;
import net.stevenuray.walletexplorer.walletnames.WalletNames;
import net.stevenuray.walletexplorer.walletnames.WalletNamesFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TransactionConverter {
	private final static Logger LOG = getLog();
	
	public static void main(String[] args) {
		disableMongoLogInfo();
		convertAllTransactionsInDatabase();
	}
	
	private static void convertAllTransactionsInDatabase(){
		LOG.info("*****STARTING NEW DATASET CONVERSION CYCLE!*****");			
		WalletNames walletNames = getWalletNamesOrQuit();		
		LOG.info("Wallets to convert: "+walletNames.size());
		Interval conversionTimespan = new Interval(new DateTime(0), new DateTime());		
		DatabaseConverter databaseConverter = getDatabaseConverter(walletNames,conversionTimespan);	
		databaseConverter.convertDatabase();
		LOG.info("*****DATASET CONVERSION COMPLETED!*****");		
	}
	
	private static DatabaseConverter getDatabaseConverter(WalletNames walletNames,Interval conversionTimespan) {		
		BasicTimableWalletNameDataFactory<WalletTransaction,ConvertedWalletTransaction> pipelineFactory = 
				DataPipelineFactories.getMongoDBToConvertedMongoDB();				
		Converter<WalletTransaction,ConvertedWalletTransaction> converter = getTransactionConverter();		
		int maxQueueSize = WalletExplorerConfig.MAX_QUEUE_LENGTH;
		int maxThreads = WalletExplorerConfig.MAX_THREADS;
		DatabaseConverter databaseConverter = new DatabaseConverter(
				pipelineFactory,converter,walletNames,maxQueueSize,maxThreads,conversionTimespan);		
		return databaseConverter;		
	}

	private static Converter<WalletTransaction,ConvertedWalletTransaction> getTransactionConverter() {
		HistoricalBTCToUSDConverter currencyConverter = new CoindeskBpiConverter();	
		Converter<WalletTransaction,ConvertedWalletTransaction> transactionConverter = 
				new HistoricalWalletTransactionConverter(currencyConverter);
		return transactionConverter;
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

	private static void disableMongoLogInfo(){
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.WARNING);
	}
	
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(TransactionConverter.class.getName());
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