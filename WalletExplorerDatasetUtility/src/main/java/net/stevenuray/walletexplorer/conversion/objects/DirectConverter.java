package net.stevenuray.walletexplorer.conversion.objects;

/**Useful converter for when direct casting conversion between two types is possible. 
 * @author Steven Uray
 *
 */
public class DirectConverter<T,U> implements Converter<T,U> {
	public U to(T t) {
		//The contract of this class specifies this conversion must always succeed!
		@SuppressWarnings("unchecked")
		U u = (U) t;
		return u;
	}

	public T from(U u) {
		//The contract of this class specifies this conversion must always succeed!
		@SuppressWarnings("unchecked")
		T t = (T) u;
		return t;
	}
}