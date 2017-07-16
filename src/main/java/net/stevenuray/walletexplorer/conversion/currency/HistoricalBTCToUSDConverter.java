package net.stevenuray.walletexplorer.conversion.currency;

import org.joda.time.DateTime;

public interface HistoricalBTCToUSDConverter {
	public double convertBTCVolumeToUSDVolume(double btcAmount, DateTime time) throws IllegalArgumentException;
}
