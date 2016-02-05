package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

public class AggregationPeriodFactory {
	public enum AggregationSize{	
		YEAR,
		MONTH,
		WEEK,
		DAY		
	}
	
	public static AggregationPeriod getAggregationPeriod(AggregationSize aggregationSize){
		switch(aggregationSize){
			case YEAR: return getYear();
			case MONTH: return getMonth();
			case WEEK: return getWeek();
			case DAY: return getDay();		
			default: throw new IllegalArgumentException("Could not find AggregationSize:"+aggregationSize);
		}		
	}

	private static AggregationPeriod getYear() {
		return new Year();
	}

	private static AggregationPeriod getMonth() {
		return new Month();
	}

	private static AggregationPeriod getWeek() {
		// TODO Auto-generated method stub
		return null;
	}

	private static AggregationPeriod getDay() {
		// TODO Auto-generated method stub
		return null;
	}
}
