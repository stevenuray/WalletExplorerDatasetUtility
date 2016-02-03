package net.stevenuray.walletexplorer.categories;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.IntervalSum;
import net.stevenuray.walletexplorer.dto.Namable;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;

/**Represents the sum of transactions for an entire wallet category for a given Interval.
 * For example, could represent the sum of transactions for a wallet category "exchanges" 
 * in the month of January, 2014. 
 * @author Steven Uray 2015-10-26
 */
public class WalletCategoryTransactionSum implements TransactionIntervalSum,Namable{
	private final WalletCategory walletCategory;	
	public WalletCategory getWalletCategory() {
		return walletCategory;
	}
	private final IntervalSum transactionSumInUSD;	
	public IntervalSum getTransactionSumInUSD() {
		return transactionSumInUSD;
	}	

	public WalletCategoryTransactionSum(WalletCategory walletCategory,IntervalSum transactionSumInUSD){
		this.walletCategory = walletCategory;
		this.transactionSumInUSD = transactionSumInUSD;			
	}

	public IntervalSum getTransactionIntervalSum() {
		return transactionSumInUSD;
	}

	public String getName() {
		return walletCategory.getName();
	}	
}
