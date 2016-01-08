package net.stevenuray.walletexplorer.walletattribute.dto;

public class TransactionOutput {
	private final double amount;
	private final String walletId;
	
	public TransactionOutput(double amount, String walletId){
		this.amount = amount;
		this.walletId = walletId;
	}
	
	public double getAmount(){
		return amount;
	}

	public String getWalletId(){
		return walletId;
	}	
}
