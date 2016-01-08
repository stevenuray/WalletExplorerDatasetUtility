package net.stevenuray.walletexplorer.walletattribute.dto;
/*
 * Signals all transaction processing completed.
 */
public class WalletTrailer implements WalletAttribute {
	private final String walletName;

	public WalletTrailer(String walletName) {
		this.walletName = walletName;
	}

	public String getWalletName() {
		return walletName;
	}
	
}
