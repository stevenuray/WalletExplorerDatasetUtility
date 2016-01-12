package net.stevenuray.walletexplorer.persistence.timable;

import java.util.Iterator;

/**Wraps an Iterator<T> to decorate it 
 * @author Steven Uray 2015-12-17 
 * @param <T>
 */
public class AscendingTimeIteratorInstance<T> implements AscendingTimeIterator<T>{
	private final Iterator<T> sourceIterator;
	
	public AscendingTimeIteratorInstance(Iterator<T> sourceIterator){
		this.sourceIterator = sourceIterator;
	}
	
	public boolean hasNext() {
		return sourceIterator.hasNext();
	}

	public T next() {
		return sourceIterator.next();
	}

}
