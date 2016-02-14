package net.stevenuray.walletexplorer.general;

import org.joda.time.DateTime;
import org.joda.time.Interval;

//TODO better configuration methods!
public class WalletExplorerConfig {
	public static final int MAX_QUEUE_LENGTH = 100;
	public static final int MAX_THREADS = 5;
	
	/**Assumes no bitcoin transactions could have been generated before the genesis block. */
	public static Interval getMaxTimespan() {
		DateTime start = new DateTime(2009,1,3,0,0,0);
		DateTime end = new DateTime();
		return new Interval(start,end);
	}
}