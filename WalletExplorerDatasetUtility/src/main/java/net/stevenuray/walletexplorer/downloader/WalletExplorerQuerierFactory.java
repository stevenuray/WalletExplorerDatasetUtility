package net.stevenuray.walletexplorer.downloader;

import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.persistence.WalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

public class WalletExplorerQuerierFactory implements WalletNameDataProducerFactory<WalletTransaction>{
	private final int maxQueueSize;
	
	public WalletExplorerQuerierFactory(int maxQueueSize){
		this.maxQueueSize = maxQueueSize;
	}
	
	public DataProducer<WalletTransaction> getDataProducer(String walletName) {
		/*TODO figure out how to get information from the consumer to the producer to control what data is produced
		 * based on what data the consumer already has.		
		 */
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxQueueSize);
		return downloader;
	}	
}