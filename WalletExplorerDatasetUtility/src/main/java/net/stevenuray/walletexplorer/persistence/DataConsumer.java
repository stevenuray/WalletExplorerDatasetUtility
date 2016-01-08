package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

public interface DataConsumer<T> {
	void consume(Iterator<T> producer);
	void consume(T t);
}
