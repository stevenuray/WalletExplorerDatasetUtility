package net.stevenuray.walletexplorer.wallettransactions.dto;

import net.sf.json.JSONArray;

public class WalletTransactions{
	private final String wallet;
	private final String walletId;
	private final int txsCount;
	private final JSONArray transactions;

	public WalletTransactions(String wallet, String walletId, int txsCount,
			JSONArray transactions) {
		this.wallet = wallet;
		this.walletId = walletId;
		this.txsCount=txsCount;
		this.transactions = transactions;
	}
	
	public String getWallet() {
		return wallet;
	}

	public String getWalletId() {
		return walletId;
	}

	public int getTxsCount() {
		return txsCount;
	}

	public JSONArray getTransactions() {
		return transactions;
	}	
}