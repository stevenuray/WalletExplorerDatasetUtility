package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

public interface DataProducer<T> extends DataPipelineComponent{
	Iterator<T> getData();
}
