package net.stevenuray.walletexplorer.categories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationTimespan;
import net.stevenuray.walletexplorer.persistence.DataProducer;
import net.stevenuray.walletexplorer.walletattribute.dto.ConvertedWalletTransaction;

import org.apache.commons.collections.IteratorUtils;
import org.joda.time.Interval;

public class CategoryAggregator{
	private final WalletCategory walletCategory;
	private final CategoryProvider<ConvertedWalletTransaction> categoryProvider;
				
	public CategoryAggregator(
			WalletCategory walletCategory,CategoryProvider<ConvertedWalletTransaction> categoryProvider, 
			int maxQueueSize){		
		this.walletCategory = walletCategory;
		this.categoryProvider = categoryProvider;			
	}
	
	public WalletCategoryTransactionSums getTransactionSums(AggregationTimespan aggregationTimespan) 
			throws InterruptedException, ExecutionException{
		WalletCategoryTransactionSumsBuilderFiller builderFiller = getTransactionSumsCreator(aggregationTimespan);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<WalletCategoryTransactionSums> sumsFuture = executor.submit(builderFiller);
		WalletCategoryTransactionSums transactionSums = sumsFuture.get();
		return transactionSums;
	}
					
	private WalletCategoryTransactionSumsBuilderFiller getTransactionSumsCreator(
			AggregationTimespan aggregationTimespan) {
		Interval timespan = aggregationTimespan.getTimespan();
		Iterator<ConvertedWalletTransaction> transactionProvider = getConvertedWalletTransactionProvider(timespan);
		WalletCategoryTransactionSumsBuilder sumsBuilder = 
				new WalletCategoryTransactionSumsBuilder(walletCategory,aggregationTimespan);
		WalletCategoryTransactionSumsBuilderFiller sumsCreator = 
				new WalletCategoryTransactionSumsBuilderFiller(transactionProvider,sumsBuilder);
		return sumsCreator;
	}

	//TODO possibly refactor this into MongoDBCategoryProvider
	@SuppressWarnings("unchecked")
	private Iterator<ConvertedWalletTransaction> getConvertedWalletTransactionProvider(Interval timespan) {
		Iterator<DataProducer<ConvertedWalletTransaction>> dataProducerIterator = 
				categoryProvider.getDataProducers(walletCategory, timespan);
		Collection<Iterator<ConvertedWalletTransaction>> iterators = 
				new ArrayList<Iterator<ConvertedWalletTransaction>>();
		while(dataProducerIterator.hasNext()){
			DataProducer<ConvertedWalletTransaction> dataProducer = dataProducerIterator.next();
			iterators.add(dataProducer.getData());
		}		
		return IteratorUtils.chainedIterator(iterators);
	}
}