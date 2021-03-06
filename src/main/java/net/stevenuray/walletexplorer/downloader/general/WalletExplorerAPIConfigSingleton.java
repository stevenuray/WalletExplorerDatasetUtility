package net.stevenuray.walletexplorer.downloader.general;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**Serves as a single point of configuration for configuration of calls to WalletExplorer.com's API. 
 * 
 * @author Steven Uray 
 */
//TODO factor these out into environment varibles or into a config file. 
public class WalletExplorerAPIConfigSingleton {
	public final static String LOGIN = "developer";
	public final static String CALLER = "WalletExplorerDatasetUtility";
	public final static Charset ENCODING = StandardCharsets.UTF_8;
	public final static String API_URL = "http://www.walletexplorer.com/api/1/wallet";	
}