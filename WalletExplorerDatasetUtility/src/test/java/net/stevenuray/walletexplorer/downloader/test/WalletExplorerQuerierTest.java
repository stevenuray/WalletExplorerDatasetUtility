package net.stevenuray.walletexplorer.downloader.test;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.downloader.DescendingTimeWalletExplorerQuerier;
import net.stevenuray.walletexplorer.downloader.WalletExplorerQuerier;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;

/**Playground for live testing against the WalletExplorer API. 
 * @author Steven Uray 
 */
public class WalletExplorerQuerierTest {
	public static void main(String[] args) {
		String walletName = "BitX.co";
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();
		WalletExplorerQuerier querier = new DescendingTimeWalletExplorerQuerier(walletName,maxTimespan);
		DateTime earliestTime = querier.getEarliestTime();
		DateTime latestTime = querier.getLatestTime();
		System.out.println("EarliestTime: "+earliestTime);
		System.out.println("LatestTime: "+latestTime);
	}
}