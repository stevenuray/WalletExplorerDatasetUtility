package net.stevenuray.walletexplorer.persistence;



/**Represents a pair of factories that construct objects based on a wallet name from WalletExplorer.com.
 *  It is common for data producers and consumers to produce specific objects to represent a 
 * collection of data from a specific wallet name. 
 * @author Steven Uray 
 *
 * @param <T>
 * @param <U>
 */
public class WalletNameDataProducerConsumerFactory<T,U> {
	private final WalletNameDataProducerFactory<T> producerFactory;	
	private final WalletNameDataConsumerFactory<U> consumerFactory;	
	
	public WalletNameDataProducerConsumerFactory(
			WalletNameDataProducerFactory<T> producerFactory,WalletNameDataConsumerFactory<U> consumerFactory){		
		this.producerFactory = producerFactory;
		this.consumerFactory = consumerFactory;
	}
	
	public WalletNameDataConsumerFactory<U> getConsumerFactory() {
		return consumerFactory;
	}

	public WalletNameDataProducerFactory<T> getProducerFactory() {
		return producerFactory;
	}
	
	public ProducerConsumerPair<T,U> getProducerConsumerPair(String walletName){
		DataProducer<T> producer = producerFactory.getDataProducer(walletName);
		DataConsumer<U> consumer = consumerFactory.getDataConsumer(walletName);
		ProducerConsumerPair<T,U> producerConsumerPair = new ProducerConsumerPair<T,U>(producer,consumer);
		return producerConsumerPair;
	}
}
