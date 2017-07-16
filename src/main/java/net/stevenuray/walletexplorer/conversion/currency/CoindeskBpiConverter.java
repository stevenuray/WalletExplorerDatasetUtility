package net.stevenuray.walletexplorer.conversion.currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoindeskBpiConverter implements HistoricalBTCToUSDConverter {	
	private static final DateTime START_TIME = new DateTime(2010,07,18,0,0,0);
	private static final String CSV_URL_BASE = "https://api.coindesk.com/v1/bpi/historical/close.json";	
	private final HashMap<String,Double> conversionMap;
	
	public CoindeskBpiConverter() throws IllegalStateException{
		String queryUrl = getQueryUrl();
		JSONObject historicalPricesWithInfo = tryToDownloadHistoricalPrices(queryUrl);
		JSONObject historicalPrices = (JSONObject) historicalPricesWithInfo.get("bpi");
		conversionMap = getConversionMap(historicalPrices);		
	}
	
	private HashMap<String,Double> getConversionMap(JSONObject historicalPrices){
		HashMap<String,Double> conversionMap = new HashMap<String,Double>();
		DateTime currentDay = START_TIME;
		DateTime endOfToday = getEndOfToday();
		DateTime endOfYesterday = endOfToday.minusDays(1);
		while(currentDay.isBefore(endOfYesterday)){			
			String currentDayIso8601Date = getIso8601Date(currentDay);			
			Number priceInUsd = (Number) historicalPrices.get(currentDayIso8601Date);			
			conversionMap.put(currentDayIso8601Date, priceInUsd.doubleValue());
			
			currentDay = currentDay.plusDays(1);
		}
		return conversionMap;
	}
	
	private DateTime getEndOfToday(){
		DateTime rightNow = new DateTime();
		DateTime tomorrow = rightNow.plusDays(1);
		int year = tomorrow.getYear();
		int month = tomorrow.getMonthOfYear();
		int day = tomorrow.getDayOfMonth();
		DateTime beginningOfTomorrow = new DateTime(year,month,day,0,0,0);
		DateTime endOfToday = beginningOfTomorrow.minusMillis(1);
		return endOfToday;		
	}
	
	
	private JSONObject downloadHistoricalPrices(String queryUrl) throws MalformedURLException, IOException, ParseException {
		URL downloadSource = new URL(queryUrl);
		InputStream downloadStream = downloadSource.openStream();
		String charset = "UTF-8";
		InputStreamReader inputStreamReader = new InputStreamReader(downloadStream,charset);
		
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);		
		JSONParser jsonParser = new JSONParser();
		JSONObject historicalPrices = (JSONObject) jsonParser.parse(bufferedReader);		
		return historicalPrices;
	}
		
	private String getEndParam(){
		String endBase = "&end=";		
		DateTime today = new DateTime();
		String endIso8601Date = getIso8601Date(today);
		String endParam = endBase+endIso8601Date;
		return endParam;
	}

	private String getIso8601Date(DateTime dateTime){		
		DateTimeFormatter formatter = ISODateTimeFormat.date();
		return formatter.print(dateTime);
	}

	private String getQueryUrl() {		
		String startParam = getStartParam();
		String endParam = getEndParam();
		String dateParams = startParam+endParam;
		return CSV_URL_BASE+"?"+dateParams;
	}

	private String getStartParam() {
		String startBase = "start=";
		String startIso8601Date = getIso8601Date(START_TIME);
		String startParam = startBase+startIso8601Date;
		return startParam;
	}

	private JSONObject tryToDownloadHistoricalPrices(String queryUrl) throws IllegalStateException{
		JSONObject historicalPrices = null;
		try{
			historicalPrices = downloadHistoricalPrices(queryUrl);
		} catch(Exception e){
			throw new IllegalStateException(e);
		}
		return historicalPrices;
	}

	public double convertBTCVolumeToUSDVolume(double btcAmount, DateTime time) throws IllegalArgumentException {
		String iso8601Date = getIso8601Date(time);
		Double price = conversionMap.get(iso8601Date);
		if(price == null){
			throw new IllegalArgumentException("Could not find a price for amount: "+btcAmount+" and time"+time);
		}
		
		double usdVolume = price*btcAmount;
		return usdVolume;
	}	
}
