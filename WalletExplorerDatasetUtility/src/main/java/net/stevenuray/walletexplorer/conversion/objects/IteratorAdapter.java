package net.stevenuray.walletexplorer.conversion.objects;

import java.util.Iterator;

public class IteratorAdapter<T,U> implements Iterator<T> {
	private final Iterator<U> sourceIterator;
	private final Converter<T,U> converter;
	private boolean hasMoreConvertedObjects = true;
	private T nextConvertedObject;
	
	public IteratorAdapter(Iterator<U> sourceIterator,Converter<T,U> converter){
		this.sourceIterator = sourceIterator;
		this.converter = converter;
		/*Loading the first converted object, or setting hasMoreConvertedObjects if 
		 * the source iterator is empty or has no convertible objects in it.		
		 */
		tryToGetNextConvertedObject();
	}
	
	public boolean hasNext() {
		return hasMoreConvertedObjects;
	}

	public T next() {
		//Saving the current nextConvertedObject, to be returned after the next one is loaded.
		T returnedConvertedObject = nextConvertedObject;
		/*Loading the next converted object, if possible. If we are out of objects, 
		 * setting hasMoreConvertedObjects to false.
		 */
		tryToGetNextConvertedObject();
		return returnedConvertedObject;
	}
	
	public void tryToGetNextConvertedObject(){
		//If the source has more documents, get them and try to convert them. 		
		if(sourceIterator.hasNext()){
			U original = sourceIterator.next();
			//Attempting conversion, trying again recursively until successful or out of source documents.
			try{
				nextConvertedObject = converter.from(original);
			} catch(Exception e){				
				//Intentionally ignoring this exception and trying to convert the next object.				
				tryToGetNextConvertedObject();
			}
		} else{
			/*If the source is out of documents, or never had them in the first place, we must 
			 * store this information in hasMoreConvertedObjects to faithfully implement Iterator<T>
			 */
			hasMoreConvertedObjects = false;
		}
	}
}