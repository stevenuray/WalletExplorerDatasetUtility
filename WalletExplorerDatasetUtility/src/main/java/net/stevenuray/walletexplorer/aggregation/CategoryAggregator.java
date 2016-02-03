package net.stevenuray.walletexplorer.aggregation;

import java.util.Iterator;

import net.stevenuray.walletexplorer.aggregation.aggregationperiod.AggregationTimespan;
import net.stevenuray.walletexplorer.categories.FactoryBasedWalletCategoryTransactionSumsBuilderFiller;
import net.stevenuray.walletexplorer.categories.TransactionSumsBuilderFiller;
import net.stevenuray.walletexplorer.categories.WalletCategory;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSums;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSumsBuilder;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

import org.joda.time.Interval;

public class CategoryAggregator{
	private final TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> producerFactory;
	private final WalletCategory walletCategory;
				
	public CategoryAggregator(
			WalletCategory walletCategory,
			TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> producerFactory){		
		this.walletCategory = walletCategory;
		this.producerFactory = producerFactory;			
	}
	
	public WalletCategoryTransactionSums getTransactionSums(AggregationTimespan aggregationTimespan) throws Exception{
		TransactionSumsBuilderFiller builderFiller = getTransactionSumsBuilderFiller(aggregationTimespan);	
		WalletCategoryTransactionSums transactionSums = builderFiller.getSums();
		return transactionSums;
	}

	private TransactionSumsBuilderFiller getTransactionSumsBuilderFiller(AggregationTimespan aggregationTimespan) {
		Iterator<String> walletsNameIterator = walletCategory.getWalletNameIterator();
		Interval timespan = aggregationTimespan.getTimespan();
		WalletCategoryTransactionSumsBuilder builder = 
				new WalletCategoryTransactionSumsBuilder(walletCategory,aggregationTimespan);
		TransactionSumsBuilderFiller builderFiller = 
				new FactoryBasedWalletCategoryTransactionSumsBuilderFiller(producerFactory,walletsNameIterator,timespan,builder);
		return builderFiller;
	}	
}