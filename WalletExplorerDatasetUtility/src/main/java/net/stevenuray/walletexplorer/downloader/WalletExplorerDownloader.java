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
	private final Interval timespanLimit;
	/**	 
	 * @param walletName - Wallet name of the desired blockchain entity, i.e Bitstamp, Bitfinex, etc.  
	 * @param timespanLimit - Transactions must be within this timespan to be returned. 
	 * The actual dataset may have a different timespan. 
	 */
	public WalletExplorerDownloader(String walletName,Interval timespanLimit){		
		this.timespanLimit = timespanLimit;
		this.downloadIterator = new WalletExplorerDownloadIterator(walletName,timespanLimit);
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
		return new WalletExplorerDownloader(walletName,adjustedTimespanLimit);
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