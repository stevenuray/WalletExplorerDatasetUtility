package net.stevenuray.walletexplorer.downloader;

import org.joda.time.Interval;

import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class WalletExplorerQuerierFactory implements TimableWalletNameDataProducerFactory<WalletTransaction>{
	private final int maxQueueSize;
	
	public WalletExplorerQuerierFactory(int maxQueueSize){
		this.maxQueueSize = maxQueueSize;
	}
	
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName) {	
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxQueueSize,maxTimespan);
		return downloader;
	}

	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName, Interval timespan) {
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxQueueSize,timespan);
		return downloader;
	}

	protected String getConvertedWalletName(String walletName, String currencySymbol) {
		return null;
	}	
}