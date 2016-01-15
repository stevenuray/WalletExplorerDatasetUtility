package net.stevenuray.walletexplorer.mongodb;

import org.joda.time.DateTime;

import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionEarliestTimeQuerier;
import net.stevenuray.walletexplorer.mongodb.queries.WalletExplorerCollectionLatestTimeQuerier;
import net.stevenuray.walletexplorer.persistence.timable.TimeNotFoundException;

public class MongoDBPipelineComponent<T> {
	private final WalletCollection walletCollection;
	
	public MongoDBPipelineComponent(WalletCollection walletCollection) {
		this.walletCollection = walletCollection;
	}

	public WalletCollection getWalletCollection() {
		return walletCollection;
	}

	protected DateTime tryToGetEarliestTime() throws TimeNotFoundException {
		WalletExplorerCollectionEarliestTimeQuerier earliestTimeQuerier = 
				new WalletExplorerCollectionEarliestTimeQuerier(walletCollection);
		
		DateTime earliestTransactionTime = null;
		try{
			earliestTransactionTime = earliestTimeQuerier.call();
		} catch(Exception e){
			throw new TimeNotFoundException();
		}
		return earliestTransactionTime;
	}

	protected DateTime tryToGetLatestTime() throws TimeNotFoundException {
		WalletExplorerCollectionLatestTimeQuerier latestTimeQuerier =
				new WalletExplorerCollectionLatestTimeQuerier(walletCollection);
		DateTime latestTransactionTime = null;		 
		try {
			latestTransactionTime = latestTimeQuerier.call();
		} catch (Exception e) {			
			throw new TimeNotFoundException();
		}		
		return latestTransactionTime;
	}
}