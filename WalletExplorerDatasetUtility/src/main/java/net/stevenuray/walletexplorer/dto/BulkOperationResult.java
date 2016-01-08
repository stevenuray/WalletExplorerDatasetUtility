package net.stevenuray.walletexplorer.dto;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**Intended to serve as a statistics object for WalletExplorerLoader. Will 
 * serve as a container for database query statistics such as number of objects loaded, number
 * of objects that could not be loaded, time spent loading, etc. 
 * @author Steven Uray 2015-10-12
 */

public class BulkOperationResult {
	private final DateTime startTime;
	private Interval loadTimespan;
	public Interval getLoadTimeSpan(){
		return loadTimespan;		
	}
	private int operations = 0; 
	public int getOperations(){
		return operations;
	}
	private boolean completed = false;
	
	public BulkOperationResult(){
		startTime = new DateTime();
	}
	
	public void iterateOperations(){
		operations++;
	}
	
	public void complete(){
		if(!completed){
			DateTime endTime = new DateTime();
			loadTimespan = new Interval(startTime,endTime);
			completed = true;
		} else{
			throw new IllegalStateException("TableLoadResult has had it's endLoad() called repeatedly!");
		}
	}
}
