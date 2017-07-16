package net.stevenuray.walletexplorer.mongodb.datapipeline.factories;

import org.bson.Document;
import org.joda.time.Interval;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.general.WalletExplorerConfig;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.datapipeline.MongoDBProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataProducer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataProducerFactory;
import net.stevenuray.walletexplorer.wallettransactions.dto.WalletTransaction;

public class MongoDBProducerFactory 
		extends MongoDBPipelineComponentFactory<WalletTransaction>
		implements TimableWalletNameDataProducerFactory<WalletTransaction> {

	public MongoDBProducerFactory(Converter<WalletTransaction, Document> converter) {
		super(converter);		
	}

	@Override
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName) {
		WalletCollection walletCollection = getWalletCollection(walletName);
		Interval maxTimespan = WalletExplorerConfig.getMaxTimespan();		
		MongoDBProducer<WalletTransaction> producer = new MongoDBProducer<WalletTransaction>(
						walletCollection,maxTimespan,super.getConverter());
		return producer;
	}

	@Override
	public TimableDataProducer<WalletTransaction> getDataProducer(String walletName,Interval timespan) {
		WalletCollection walletCollection = getWalletCollection(walletName);				
		MongoDBProducer<WalletTransaction> producer = new MongoDBProducer<WalletTransaction>(
						walletCollection,timespan,super.getConverter());
		return producer;
	}		
}