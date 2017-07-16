package net.stevenuray.walletexplorer.categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSums;

import org.joda.time.DateTime;

/**Represents a collection of WalletCategoryTransactionSums as a whole. 
 * These WalletCategoryTransactionSum objects should not be duplicate and should all contain 
 * Intervals of the same length. 
 * @author Steven Uray 2015-10-26
 */
public class WalletCategoryTransactionSums implements TransactionIntervalSums{
	private class AscendingIterator implements Iterator<TransactionIntervalSum>{
		private final Iterator<WalletCategoryTransactionSum> sumIterator;
		
		private AscendingIterator(List<WalletCategoryTransactionSum> walletCategoryTransactionSums){
			this.sumIterator = walletCategoryTransactionSums.iterator();
		}
		
		public boolean hasNext() {			
			return sumIterator.hasNext();
		}

		public TransactionIntervalSum next() {
			return sumIterator.next();
		}		
	}	
	private class AscendingTimeSortComparator implements Comparator<WalletCategoryTransactionSum>{
		public int compare(WalletCategoryTransactionSum arg0,WalletCategoryTransactionSum arg1) {
			DateTime arg0StartTime = arg0.getTransactionSumInUSD().getTimespan().getStart();
			DateTime arg1StartTime = arg1.getTransactionSumInUSD().getTimespan().getStart();
			if(arg0StartTime.isBefore(arg1StartTime)){
				return -1;
			} else{
				return 1;
			}
		}		
	}
	private final List<WalletCategoryTransactionSum> walletCategoryTransactionSums;
	private final WalletCategory walletCategory;

	public WalletCategoryTransactionSums(WalletCategory walletCategory,
			List<WalletCategoryTransactionSum> transactionSums){
		this.walletCategory = walletCategory;		
		this.walletCategoryTransactionSums = getAscendingSortedSums(transactionSums);
	}
	
	public String getName() {
		return walletCategory.getName();
	}
	
	public Iterator<TransactionIntervalSum> getSums() {		
		return new AscendingIterator(walletCategoryTransactionSums);
	}

	/**
	 * @return - The WalletCategory each transaction sum is from. 
	 */
	public WalletCategory getWalletCategory(){
		return walletCategory;
	}
	/**	 
	 * @return - An unmodifable list of WalletCategoryTransactionSums in ascending time order. 
	 */
	public List<WalletCategoryTransactionSum> getWalletCategoryTransactionSums() {		
		return Collections.unmodifiableList(walletCategoryTransactionSums);
	}

	private List<WalletCategoryTransactionSum> getAscendingSortedSums(List<WalletCategoryTransactionSum> unsortedSums){
		ArrayList<WalletCategoryTransactionSum> sortedSums = new ArrayList<WalletCategoryTransactionSum>(unsortedSums);
		Collections.sort(sortedSums, new AscendingTimeSortComparator());
		return sortedSums;
	}
}