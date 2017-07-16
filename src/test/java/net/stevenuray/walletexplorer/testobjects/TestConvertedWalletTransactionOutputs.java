package net.stevenuray.walletexplorer.testobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;

public class TestConvertedWalletTransactionOutputs {
	
	public static Collection<ConvertedWalletTransactionOutput> getConvertedTransactionOutputs(){
		List<ConvertedWalletTransactionOutput> convertedOutputsList = new ArrayList<ConvertedWalletTransactionOutput>();
		convertedOutputsList.add(getConvertedTransactionOutput());
		return convertedOutputsList;
	}
	
	public static ConvertedWalletTransactionOutput getConvertedTransactionOutput(){
		TransactionOutput transactionOutput = TestTransactionOutputs.getOutput();
		double usdVolume = 100;
		ConvertedWalletTransactionOutput convertedOutput = 
				new ConvertedWalletTransactionOutput(transactionOutput,usdVolume);
		return convertedOutput;
	}
}
