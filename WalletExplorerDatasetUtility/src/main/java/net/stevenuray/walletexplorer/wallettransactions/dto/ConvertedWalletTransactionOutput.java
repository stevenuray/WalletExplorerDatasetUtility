package net.stevenuray.walletexplorer.wallettransactions.dto;

public class ConvertedWalletTransactionOutput {
	private final TransactionOutput transactionOutput;
	private final double usdVolume;
	
	public ConvertedWalletTransactionOutput(TransactionOutput transactionOutput,double usdVolume){
		this.transactionOutput = transactionOutput;
		this.usdVolume = usdVolume;
	}

	public TransactionOutput getTransactionOutput() {
		return transactionOutput;
	}

	public double getUsdVolume() {
		return usdVolume;
	}
}
