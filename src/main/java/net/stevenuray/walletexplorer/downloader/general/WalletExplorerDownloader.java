package net.stevenuray.walletexplorer.downloader.general;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimit;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

/**Wraps a WalletExplorerDownloadIterator to implement TimableDataProducer. 
 * @author Steven Uray 
 */
public class WalletExplorerDownloader implements TimableDataProducer<WalletTransaction>{
	private final WalletExplorerDownloadIterator downloadIterator;	
	private final Interval timespanLimit;
	private final RateLimit rateLimit;
	
	/**	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned. 
	 * @param rateLimit - Maximum queries to be sent in a given timespan. 
	 * The actual dataset may have a different timespan. 
	 */
	public WalletExplorerDownloader(String walletName,Interval timespanLimit,RateLimit rateLimit){		
		this.timespanLimit = timespanLimit;
		this.downloadIterator = new WalletExplorerDownloadIterator(walletName,timespanLimit,rateLimit);
		this.rateLimit = rateLimit;
	}
	
	@Override
	public void finish() {
		/*WalletExplorerDownloader does not need to implement this method, 
		 * because it does not need to 'finish' anything, close resources, etc. 
		 */
	}

	public TimableDataProducer<WalletTransaction> fromTime(DateTime earliestTime) {
		String walletName = downloadIterator.getWalletName();
		Interval adjustedTimespanLimit = new Interval(earliestTime,timespanLimit.getEnd());
		return new WalletExplorerDownloader(walletName,adjustedTimespanLimit,rateLimit);
	}

	public Iterator<WalletTransaction> getData() {
		return downloadIterator;
	}
 
	public DateTime getEarliestTime() {
		return downloadIterator.getEarliestTime();
	}

	public DateTime getLatestTime() {
		return downloadIterator.getLatestTime();
	}

	@Override
	public void start() {
		/*WalletExplorerDownloader does not need to implement this method, 
		 * because it does not need to "start" anything, open resources, etc. 
		 */
	}
}