package net.stevenuray.walletexplorer.downloader.test;

import org.joda.time.DateTime;

import net.stevenuray.walletexplorer.downloader.WalletExplorerQuerier;

/**Playground for live testing against the WalletExplorer API. 
 * @author Steven Uray 
 */
public class WalletExplorerQuerierTest {
	public static void main(String[] args) {
		String walletName = "BitX.co";
		WalletExplorerQuerier querier = new WalletExplorerQuerier(walletName);
		DateTime earliestTime = querier.getEarliestTime();
		DateTime latestTime = querier.getLatestTime();
		System.out.println("EarliestTime: "+earliestTime);
		System.out.println("LatestTime: "+latestTime);
	}
}