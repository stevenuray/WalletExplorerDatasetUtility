package net.stevenuray.walletexplorer.walletattribute.dto;
/*
 * Signals start of current wallet transactions.
 */
public class WalletHeader implements WalletAttribute {
	private final String walletName;

	public WalletHeader(String walletName) {
		this.walletName = walletName;
	}

	public String getWalletName() {
		return walletName;
	}
	
}
