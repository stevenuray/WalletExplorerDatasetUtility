package net.stevenuray.walletexplorer.downloader;

import org.joda.time.Interval;

import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class WalletExplorerQuerierFactory implements TimableWalletNameDataProducerFactory<WalletTransaction>{
	public WalletExplorerQuerierFactory(){
		
	}
	
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName) {	
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxTimespan);
		return downloader;
	}

	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName, Interval timespan) {
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,timespan);
		return downloader;
	}	
}