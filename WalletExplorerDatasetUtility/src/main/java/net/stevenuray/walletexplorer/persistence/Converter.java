package net.stevenuray.walletexplorer.persistence;

public interface Converter<T,U>{
	public U to(T t);
	public T from(U u);
}
