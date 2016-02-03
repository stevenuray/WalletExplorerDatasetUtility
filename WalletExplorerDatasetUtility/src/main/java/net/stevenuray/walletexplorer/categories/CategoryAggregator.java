package net.stevenuray.walletexplorer.categories;

import java.util.Iterator;

import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationTimespan;
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

	private TransactionSumsBuilderFiller getTransactionSumsBuilderFiller(
			AggregationTimespan aggregationTimespan) {
		Iterator<String> walletsNameIterator = walletCategory.getWalletNameIterator();
		Interval timespan = aggregationTimespan.getTimespan();
		WalletCategoryTransactionSumsBuilder builder = 
				new WalletCategoryTransactionSumsBuilder(walletCategory,aggregationTimespan);
		TransactionSumsBuilderFiller builderFiller = 
				new FactoryWalletCategoryTransactionSumsBuilderFiller(producerFactory,walletsNameIterator,timespan,builder);
		return builderFiller;
	}	
}