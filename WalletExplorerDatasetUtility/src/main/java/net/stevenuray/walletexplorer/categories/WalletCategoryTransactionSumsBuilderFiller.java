package net.stevenuray.walletexplorer.categories;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;

/**Combines a source of ConvertedWalletTransaction objects and a WalletCategoryTransactionSumsBuilder.
 * Each ConvertedWalletTransaction in the source will be inserted into the WalletCategoryTransactionSumsBuilder.
 * @author Steven Uray 2015-10-30
 */
public class WalletCategoryTransactionSumsBuilderFiller implements Callable<WalletCategoryTransactionSums>{
	private final Iterator<ConvertedWalletTransaction> transactionSource;
	private final WalletCategoryTransactionSumsBuilder builder;
	
	public WalletCategoryTransactionSumsBuilderFiller(Iterator<ConvertedWalletTransaction> transactionSource,
			WalletCategoryTransactionSumsBuilder builder){
		this.transactionSource = transactionSource;
		this.builder = builder;
	}

	public WalletCategoryTransactionSums call() throws Exception {
		insertAllTransactionsIntoBuilder();
		WalletCategoryTransactionSums transactionSums = builder.build();
		return transactionSums;
	}	
	
	private void insertAllTransactionsIntoBuilder(){
		while(transactionSource.hasNext()){
			ConvertedWalletTransaction nextTransaction = transactionSource.next();
			if(nextTransaction != null){
				builder.insert(nextTransaction);
			} else{				
				//This is an indication an error has occurred or no more transactions can be found. 
				break;
			}
		}
	}
}