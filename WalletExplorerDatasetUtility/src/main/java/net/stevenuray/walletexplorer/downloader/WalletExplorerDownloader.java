package net.stevenuray.walletexplorer.downloader;

import java.util.Iterator;

import org.joda.time.DateTime;

import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

/**Wraps a WalletExplorerDownloadIterator to implement DataProducer. 
 * @author Steven Uray 
 */
public class WalletExplorerDownloader implements DataProducer<WalletTransaction>{
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
}
