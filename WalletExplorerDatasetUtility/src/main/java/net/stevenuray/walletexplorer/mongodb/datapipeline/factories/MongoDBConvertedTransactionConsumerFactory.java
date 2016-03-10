package net.stevenuray.walletexplorer.mongodb.datapipeline.factories;

import org.bson.Document;

import net.stevenuray.walletexplorer.conversion.objects.Converter;
import net.stevenuray.walletexplorer.mongodb.WalletCollection;
import net.stevenuray.walletexplorer.mongodb.datapipeline.MongoDBConsumer;
import net.stevenuray.walletexplorer.persistence.timable.TimableDataConsumer;
import net.stevenuray.walletexplorer.persistence.timable.TimableWalletNameDataConsumerFactory;

public class MongoDBConvertedTransactionConsumerFactory<ConvertedWalletTransaction> extends
		MongoDBPipelineComponentFactory<ConvertedWalletTransaction> implements
		TimableWalletNameDataConsumerFactory<ConvertedWalletTransaction> {

	public MongoDBConvertedTransactionConsumerFactory(
			Converter<ConvertedWalletTransaction, Document> converter) {
		super(converter);		
	}

	@Override
	public TimableDataConsumer<ConvertedWalletTransaction> getDataConsumer(String walletName) {
		WalletCollection walletCollection = getConvertedCollection(walletName);		
		TimableDataConsumer<ConvertedWalletTransaction> consumer = 
				new MongoDBConsumer<>(walletCollection,super.getConverter());
		return consumer;
	}
}