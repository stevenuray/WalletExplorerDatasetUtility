package net.stevenuray.walletexplorer.categories;

import java.util.Iterator;

import org.joda.time.Interval;

import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransaction;

public class FactoryWalletCategoryTransactionSumsBuilderFiller implements
		TransactionSumsBuilderFiller {
	private final TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> producerFactory;
	private final WalletCategoryTransactionSumsBuilder builder;
	private final Iterator<String> walletNames;
	private final Interval timespan;
	
	public FactoryWalletCategoryTransactionSumsBuilderFiller(
			TimableWalletNameDataProducerFactory<ConvertedWalletTransaction> producerFactory,
			Iterator<String> walletNames,Interval timespan,WalletCategoryTransactionSumsBuilder builder){
		this.producerFactory = producerFactory;
		this.walletNames = walletNames;
		this.timespan = timespan;
		this.builder = builder;
	}

	public WalletCategoryTransactionSums getSums() {
		while(walletNames.hasNext()){
			String nextWalletName = walletNames.next();
			//TODO replace with a Log4j Logger. 	
			System.out.println("Aggregating wallet: "+nextWalletName);
			addTransactionsToBuilder(nextWalletName);
		}
		WalletCategoryTransactionSums sums = builder.build();
		return sums;
	}

	private void addTransactionsToBuilder(String nextWalletName) {
		TimableDataProducer<ConvertedWalletTransaction> producer = 
				producerFactory.getDataProducer(nextWalletName, timespan);
		Iterator<ConvertedWalletTransaction> producerIterator = producer.getData();
		while(producerIterator.hasNext()){
			ConvertedWalletTransaction nextProduct = producerIterator.next();
			builder.insert(nextProduct);
		}		
	}
}