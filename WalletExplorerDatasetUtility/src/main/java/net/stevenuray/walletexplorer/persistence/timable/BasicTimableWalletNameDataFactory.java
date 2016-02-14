package net.stevenuray.walletexplorer.persistence.timable;

/**Builds a TimableDataPipeline given a wallet name from WalletExplorer.com. 
 * More specifically, given a wallet name, will produce a data pipeline with both a data source
 * and a data destination for that wallet name. The source could be a database, WalletExplorer.com itself, 
 * etc. The destination could be a database, or any other thing that consumes data. 
 * @author Steven Uray 
 *
 * @param <T>
 * @param <U>
 */
public class BasicTimableWalletNameDataFactory<T,U> implements
		TimableWalletNameDataFactory<T, U> {
	private final TimableWalletNameDataProducerFactory<T> producerFactory;
	private final TimableWalletNameDataConsumerFactory<U> consumerFactory;
	
	public BasicTimableWalletNameDataFactory(
			TimableWalletNameDataProducerFactory<T> producerFactory,
			TimableWalletNameDataConsumerFactory<U> consumerFactory){
		this.producerFactory = producerFactory;
		this.consumerFactory = consumerFactory;
	}
	
	public TimableDataPipeline<T, U> getDataPipeline(String walletName) {			
		TimableDataProducer<T> producer = producerFactory.getDataProducer(walletName);	
		TimableDataConsumer<U> consumer = consumerFactory.getDataConsumer(walletName);	
		TimableDataPipeline<T,U> pair = new TimableDataPipeline<T,U>(producer,consumer);
		return pair;
	}
}