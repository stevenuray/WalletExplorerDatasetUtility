package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

/**DataProducer implementations generate Java objects from a resource external to the program.
 * This interface can be considered an implementation of the Data Access Object design pattern,
 * as it separates business services from low level data access. 
 *
 * @param <T>
 */
public interface DataProducer<T> extends DataPipelineComponent{
	Iterator<T> getData();
}
