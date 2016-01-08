package net.stevenuray.walletexplorer.conversion.currency;

import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;

import org.joda.time.DateTime;

public class TransactionOutputConverterService implements
		TransactionOutputConverter {
	private final HistoricalBTCToUSDConverter converter;
	
	public TransactionOutputConverterService(HistoricalBTCToUSDConverter converter){
		this.converter = converter;
	}
	
	public ConvertedWalletTransactionOutput convert(TransactionOutput transactionOutput, DateTime transactionTime) {		
		double usdVolume = getUsdVolume(transactionOutput,transactionTime);
		ConvertedWalletTransactionOutput convertedOutput = 
				new ConvertedWalletTransactionOutput(transactionOutput,usdVolume);
		return convertedOutput;
	}
	
	private double getUsdVolume(TransactionOutput transactionOutput, DateTime transactionTime){
		double btcVolume = transactionOutput.getAmount();
		double usdVolume = converter.convertBTCVolumeToUSDVolume(btcVolume, transactionTime);
		return usdVolume;
	}
}
