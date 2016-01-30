package net.stevenuray.walletexplorer.wallettransactions.dto;

import java.util.Collection;

import org.joda.time.DateTime;

public class ConvertedWalletTransaction {
	private final Collection<ConvertedWalletTransactionOutput> convertedOutputs;
	private final double transactionOutputVolumeSumInUsd;
	private final DateTime transactionTime;
	private final String txid;
	
	public ConvertedWalletTransaction(
			String txid,Collection<ConvertedWalletTransactionOutput> convertedOutputs,DateTime transactionTime){
		this.txid = txid;
		this.convertedOutputs = convertedOutputs;
		this.transactionOutputVolumeSumInUsd = sumConvertedOutputsVolumeInUsd(convertedOutputs);
		this.transactionTime = transactionTime;
	}
	
	public ConvertedWalletTransaction(
			WalletTransaction originalTransaction,Collection<ConvertedWalletTransactionOutput> convertedOutputs){
		this.txid = originalTransaction.getTxid();
		this.convertedOutputs = convertedOutputs;
		this.transactionOutputVolumeSumInUsd = sumConvertedOutputsVolumeInUsd(convertedOutputs);
		this.transactionTime = originalTransaction.getTransactionTime();
	}

	public Collection<ConvertedWalletTransactionOutput> getConvertedOutputs() {
		return convertedOutputs;
	}

	public double getTransactionOutputVolumeSumInUsd() {
		return transactionOutputVolumeSumInUsd;
	}
	
	public DateTime getTransactionTime() {
		return transactionTime;
	}

	public String getTxid() {
		return txid;
	}

	private double sumConvertedOutputsVolumeInUsd(Collection<ConvertedWalletTransactionOutput> convertedOutputs2) {
		double sum = 0;
		for(ConvertedWalletTransactionOutput convertedOutput : convertedOutputs2){
			sum += convertedOutput.getUsdVolume();
		}	
		return sum;
	}
}