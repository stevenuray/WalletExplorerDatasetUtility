package net.stevenuray.walletexplorer.downloader.general;

import org.joda.time.Interval;

import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimit;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class WalletExplorerQuerierFactory implements TimableWalletNameDataProducerFactory<WalletTransaction>{
	public WalletExplorerQuerierFactory(){
		
	}
	
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName) {	
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();
		RateLimit rateLimit = WalletExplorerConfig.getRateLimit();
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,maxTimespan,rateLimit);
		return downloader;
	}

	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName, Interval timespan) {
		RateLimit rateLimit = WalletExplorerConfig.getRateLimit();
		WalletExplorerDownloader downloader = new WalletExplorerDownloader(walletName,timespan,rateLimit);
		return downloader;
	}	
}