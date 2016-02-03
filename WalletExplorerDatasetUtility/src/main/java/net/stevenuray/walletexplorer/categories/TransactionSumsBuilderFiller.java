package net.stevenuray.walletexplorer.categories;

/**Before they can build a TransactionSums object, 
 * TransactionSumsBuilder objects must be loaded with transactions from a data source. 
 * TransactionSumsBuilderFiller implementations are responsible for loading a TransactionSumsBuilder 
 * with transactions from a data source. 
 * @author Steven Uray 
 */
public interface TransactionSumsBuilderFiller {
	public abstract WalletCategoryTransactionSums getSums();
}