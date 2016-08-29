package net.stevenuray.walletexplorer.general;

import net.stevenuray.walletexplorer.downloader.ratelimiting.RateLimit;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

//TODO better configuration methods!
public class WalletExplorerConfig {
	public static final int MAX_QUEUE_LENGTH = 100;
	public static final int MAX_THREADS = 5;
	
	/**No bitcoin transactions could have been generated before the genesis block. */
	public static Interval getMaxTimespan() {
		DateTime start = new DateTime(2009,1,3,0,0,0);
		DateTime end = new DateTime();
		return new Interval(start,end);
	}
	
	/*A test on 2016-08-28 verified this rate limit did not exceed the threshold. */
	public static RateLimit getRateLimit(){
		return new RateLimit(5,Duration.standardSeconds(60));
	}
}