package net.stevenuray.walletexplorer.downloader;

import java.util.Iterator;

import org.joda.time.DateTime;

import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

/**Wraps a WalletExplorerDownloadIterator to implement TimableDataProducer. 
 * @author Steven Uray 
 */
public class WalletExplorerDownloader implements TimableDataProducer<WalletTransaction>{
	private final WalletExplorerDownloadIterator downloadIterator;
	
	public WalletExplorerDownloader(String walletName,int maxQueueSize){
		this.downloadIterator = new WalletExplorerDownloadIterator(walletName,maxQueueSize);
	}
	
	public WalletExplorerDownloader(String walletName,int maxQueueSize,DateTime endTime){
		this.downloadIterator = new WalletExplorerDownloadIterator(walletName,maxQueueSize,endTime);
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

	public TimableDataProducer<WalletTransaction> fromTime(DateTime earliestTime) {
		// TODO Auto-generated method stub
		return null;
	}
}
