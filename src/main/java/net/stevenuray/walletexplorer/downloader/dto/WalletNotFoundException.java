package net.stevenuray.walletexplorer.downloader.dto;

/**Decorates an exception thrown by users of WalletExplorer's API. 
 * 
 * @author Steven Uray
 */
@SuppressWarnings("serial")
public class WalletNotFoundException extends IllegalArgumentException {
	public WalletNotFoundException(String errorMessage){
		super(errorMessage);
	}
}
