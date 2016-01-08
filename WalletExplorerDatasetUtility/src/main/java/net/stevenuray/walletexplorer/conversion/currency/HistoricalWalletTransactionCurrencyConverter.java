package net.stevenuray.walletexplorer.conversion.currency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.TransactionOutput;
import net.stevenuray.walletexplorer.walletattribute.dto.WalletTransaction;

import org.apache.commons.collections4.IteratorUtils;
import org.joda.time.DateTime;

public class HistoricalWalletTransactionCurrencyConverter implements WalletTransactionCurrencyConverter {
	private final TransactionOutputConverter outputConverter;
	
	public HistoricalWalletTransactionCurrencyConverter(HistoricalBTCToUSDConverter currencyConverter){		
		this.outputConverter = new TransactionOutputConverterService(currencyConverter);
	}
	
	public HistoricalWalletTransactionCurrencyConverter(TransactionOutputConverter outputConverter){
		this.outputConverter = outputConverter;		
	}
		
	public ConvertedWalletTransaction convert(WalletTransaction walletTransaction) {		
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = getConvertedOutputs(walletTransaction);
		ConvertedWalletTransaction convertedWalletTransaction = 
				new ConvertedWalletTransaction(walletTransaction,convertedOutputs);
		return convertedWalletTransaction;
	}
		
	private Collection<ConvertedWalletTransactionOutput> getConvertedOutputs(WalletTransaction walletTransaction) {
		DateTime transactionTime = walletTransaction.getTransactionTime();
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = new ArrayList<ConvertedWalletTransactionOutput>();
		
		Iterator<TransactionOutput> transactionOutputIterator = 
				walletTransaction.getWalletTransactionOutputsUnmodifiable().iterator();
		List<TransactionOutput> transactionOutputList = IteratorUtils.toList(transactionOutputIterator);
		for(TransactionOutput transactionOutput : transactionOutputList){
			ConvertedWalletTransactionOutput convertedOutput = outputConverter.convert(transactionOutput, transactionTime);
			convertedOutputs.add(convertedOutput);
		}		
		return convertedOutputs;
	}	
}