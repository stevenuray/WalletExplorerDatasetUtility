package net.stevenuray.walletexplorer.persistence.walletdatafactories;

import net.stevenuray.walletexplorer.persistence.BasicDataPipeline;
import net.stevenuray.walletexplorer.persistence.DataConsumer;
import net.stevenuray.walletexplorer.persistence.DataPipeline;
import net.stevenuray.walletexplorer.persistence.DataProducer;

/**Represents a pair of factories that construct objects based on a wallet name from WalletExplorer.com.
 *  It is common for data producers and consumers to produce specific objects to represent a 
 * collection of data from a specific wallet name. 
 * @author Steven Uray 
 *
 * @param <T>
 * @param <U>
 */
public class BasicWalletNameDataPipelineFactory<T,U> implements WalletNameDataPipelineFactory<T,U>{
	private final WalletNameDataProducerFactory<T> producerFactory;	
	private final WalletNameDataConsumerFactory<U> consumerFactory;	
	
	public BasicWalletNameDataPipelineFactory(
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
	
	public DataPipeline<T, U> getDataPipeline(String walletName){
		DataProducer<T> producer = producerFactory.getDataProducer(walletName);
		DataConsumer<U> consumer = consumerFactory.getDataConsumer(walletName);
		DataPipeline<T, U> producerConsumerPair = new BasicDataPipeline<T,U>(producer,consumer);
		return producerConsumerPair;
	}
}