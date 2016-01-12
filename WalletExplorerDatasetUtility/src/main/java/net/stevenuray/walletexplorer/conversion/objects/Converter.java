package net.stevenuray.walletexplorer.conversion.objects;

public interface Converter<T,U>{
	public U to(T t);
	public T from(U u);
}
