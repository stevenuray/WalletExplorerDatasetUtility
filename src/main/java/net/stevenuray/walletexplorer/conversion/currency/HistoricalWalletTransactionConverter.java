package net.stevenuray.walletexplorer.conversion.currency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

import org.apache.commons.collections4.IteratorUtils;
import org.joda.time.DateTime;

public class HistoricalWalletTransactionConverter implements WalletTransactionCurrencyConverter, 
	Converter<WalletTransaction,ConvertedWalletTransaction> {
	private final TransactionOutputConverter outputConverter;
	
	public HistoricalWalletTransactionConverter(HistoricalBTCToUSDConverter currencyConverter){		
		this.outputConverter = new TransactionOutputConverterService(currencyConverter);
	}
	
	public HistoricalWalletTransactionConverter(TransactionOutputConverter outputConverter){
		this.outputConverter = outputConverter;		
	}
		
	public ConvertedWalletTransaction convert(WalletTransaction walletTransaction) {		
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = getConvertedOutputs(walletTransaction);
		ConvertedWalletTransaction convertedWalletTransaction = 
				new ConvertedWalletTransaction(walletTransaction,convertedOutputs);
		return convertedWalletTransaction;
	}
		
	public WalletTransaction from(ConvertedWalletTransaction convertedWalletTransaction) {
		// TODO implement if ever needed. Note this is a violation of the liskov substition principle.  
		return null;
	}

	public ConvertedWalletTransaction to(WalletTransaction walletTransaction) {
		return convert(walletTransaction);
	}

	private Collection<ConvertedWalletTransactionOutput> getConvertedOutputs(WalletTransaction walletTransaction) {
		DateTime transactionTime = walletTransaction.getTransactionTime();
		Collection<ConvertedWalletTransactionOutput> convertedOutputs = 
				new ArrayList<ConvertedWalletTransactionOutput>();		
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