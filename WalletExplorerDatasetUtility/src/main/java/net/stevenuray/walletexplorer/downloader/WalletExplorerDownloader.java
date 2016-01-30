package net.stevenuray.walletexplorer.downloader;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

/**Wraps a WalletExplorerDownloadIterator to implement TimableDataProducer. 
 * @author Steven Uray 
 */
public class WalletExplorerDownloader implements TimableDataProducer<WalletTransaction>{
	private final WalletExplorerDownloadIterator downloadIterator;
	private final int maxQueueSize;
	private final Interval timespanLimit;
	/**	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param maxQueueSize - The maximum size of the download queue. Larger means faster but more memory.
	 * @param timespanLimit - Transactions must be within this timespan to be returned. 
	 * The actual dataset may have a different timespan. 
	 */
	public WalletExplorerDownloader(String walletName,int maxQueueSize,Interval timespanLimit){
		this.maxQueueSize = maxQueueSize;
		this.timespanLimit = timespanLimit;
		this.downloadIterator = new WalletExplorerDownloadIterator(walletName,maxQueueSize,timespanLimit);
	}
	
	@Override
	public void finish() {
		//WalletExplorerDownloader does not need to implement this optional method.		
	}

	public TimableDataProducer<WalletTransaction> fromTime(DateTime earliestTime) {
		String walletName = downloadIterator.getWalletName();
		Interval adjustedTimespanLimit = new Interval(earliestTime,timespanLimit.getEnd());
		return new WalletExplorerDownloader(walletName,maxQueueSize,adjustedTimespanLimit);
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
		//WalletExplorerDownloader does not need to implement this optional method. 		
	}
}