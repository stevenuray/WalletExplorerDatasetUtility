package net.stevenuray.walletexplorer.downloader;

import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

public class WalletExplorerQuerierFactory implements TimableWalletNameDataProducerFactory<WalletTransaction>{
	private final int maxQueueSize;
	
	public WalletExplorerQuerierFactory(int maxQueueSize){
		this.maxQueueSize = maxQueueSize;
	}
	
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName) {		
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxQueueSize);
		return downloader;
	}	
}