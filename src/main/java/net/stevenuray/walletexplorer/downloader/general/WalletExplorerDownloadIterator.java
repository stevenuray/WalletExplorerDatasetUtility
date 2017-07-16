package net.stevenuray.walletexplorer.downloader.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.downloader.dto.FailureToRetrieveDataException;
import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimit;
import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimitEvaluator;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

/**Wraps a WalletExplorerQuerier to turn it into an iterator. 
 * Will throw an unchecked FailureToRetrieveDataException if there is problems 
 * getting data from WalletExplorer. 
 * Note may exhibit unexpected behavior due to rate limiting in effect on the WalletExplorer API. 
 * @author Steven Uray  
 */
public class WalletExplorerDownloadIterator implements Iterator<WalletTransaction>{
private static final Logger LOG = getLog();			
	
	//TODO get log in non-programmatic way, i.e by .properties or .xml file. 
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(WalletExplorerDownloadIterator.class.getName());
		log.setAdditivity(false);
		log.setLevel(org.apache.log4j.Level.INFO);
		String pattern = "%d | %-5p| %m%n";
		Layout layout = new PatternLayout(pattern);	
		Appender appender = new ConsoleAppender(layout);		
		appender.setName("Test Appender");			
		log.addAppender(appender);
		return log;
	}
	
	private int currentIndex = 0;		
	private List<WalletTransaction> currentPage = new ArrayList<>();
	private final DescendingTimeWalletExplorerQuerier querier;
	private final String walletName;
	private final RateLimitEvaluator rateLimitEvaluator;
	
	/**	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned. 
	 * @param rateLimit - Maximum queries sent in a given period of time. 
	 * The actual dataset may have a different timespan. 
	 */
	public WalletExplorerDownloadIterator(String walletName,Interval timespanLimit,RateLimit rateLimit){
		this.walletName = walletName;
		querier = new DescendingTimeWalletExplorerQuerier(walletName,timespanLimit);
		this.rateLimitEvaluator = new RateLimitEvaluator(rateLimit);
	}
	
	public DateTime getEarliestTime() {
		return querier.getEarliestTime();
	}
	
	public DateTime getLatestTime() {
		return querier.getLatestTime();
	}

	public String getWalletName() {
		return walletName;
	}
	
	public boolean hasNext() {
		if(querier.isDownloading()){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * @throws FailureToRetrieveDataException - If there was a problem getting data from WalletExplorer. 
	 * This is most commonly due to networking errors. 
	 */
	public WalletTransaction next() {	
		if(shouldDownloadNewPageOfTransactions()){
			tryToDownloadNewPageOfTransactions(); 			
		} 		
		return getNextWalletTransactionFromPageAndAdjustIndex();
	}
	
	private void tryToDownloadNewPageOfTransactions(){
		sleepUntilRateLimitIsNotExceeded();		
		tryToGetNewPageOfDownloadedTransactionsOrThrowException();
		currentIndex = 0;
		rateLimitEvaluator.recordNewEvent();
	}
		
	private void sleepUntilRateLimitIsNotExceeded(){
		boolean printedMessage = false;
		while(rateLimitEvaluator.isRateLimitExceeded()){
			try{
				if(printedMessage == false){				
					LOG.info("Waiting until rate limit is not exceeded");
					printedMessage = true;
				} 
				Thread.sleep(10);
			} catch(InterruptedException e){
				LOG.error("Interrupted while pausing to wait for rate limit to not be exceeded!",e); 
				return;
			}
		}		
	}
		
	private boolean shouldDownloadNewPageOfTransactions(){
		return (currentIndex >= currentPage.size());
	}

	private WalletTransaction getNextWalletTransactionFromPageAndAdjustIndex(){				
		WalletTransaction nextTransaction = currentPage.get(currentIndex);
		currentIndex++;
		return nextTransaction;		
	}

	private void tryToGetNewPageOfDownloadedTransactionsOrThrowException(){
		try{
			currentPage = querier.getNextWalletTransactions();
		} catch(Exception e){
			LOG.error("Failed to download new page of transactions!", e);
			throw new FailureToRetrieveDataException(e);
		}
	}
}