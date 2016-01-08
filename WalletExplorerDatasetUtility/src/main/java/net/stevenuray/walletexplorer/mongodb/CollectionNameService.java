package net.stevenuray.walletexplorer.mongodb;

import net.stevenuray.walletexplorer.aggregator.aggregationperiod.AggregationPeriod;

public class CollectionNameService {
	private static final String CONVERSION_BASE = "_To_";
	private static final String AGGREGATE_BASE = "_Per_";
	
	public String getConvertedCollectionName(String unconvertedCollectionName,String currencySymbol){
		String convertedCollectionName = unconvertedCollectionName+CONVERSION_BASE+currencySymbol;
		return convertedCollectionName;
	}
	
	public String getAggregatedCollectionName(String unconvertedCollectionName, AggregationPeriod aggregationPeriod){
		String aggregateSizeName = aggregationPeriod.getName();
		String aggregateCollectionName = unconvertedCollectionName+AGGREGATE_BASE+aggregateSizeName;
		return aggregateCollectionName;
	}
}
