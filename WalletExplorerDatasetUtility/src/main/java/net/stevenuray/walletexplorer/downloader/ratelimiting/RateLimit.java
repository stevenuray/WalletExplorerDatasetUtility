package net.stevenuray.walletexplorer.downloader.ratelimiting;

import org.joda.time.Duration;

/**Rate limiting is in effect on the WalletExplorer API. 
 * This represents maximum download rate limits that API clients are permitted to use. 
 * @author Steven Uray 
 */
public class RateLimit {
	private final int queryCount;
	private final Duration timespan;
	
	public RateLimit(int queryCount,Duration timespan){
		this.queryCount = queryCount;
		this.timespan = timespan;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public Duration getTimespan() {
		return timespan;
	}
}