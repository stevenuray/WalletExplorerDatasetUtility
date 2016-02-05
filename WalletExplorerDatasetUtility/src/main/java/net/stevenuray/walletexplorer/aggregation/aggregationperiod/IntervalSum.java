package net.stevenuray.walletexplorer.aggregation.aggregationperiod;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.Interval;

/**IntervalSum represents some sum of events over a period of time, such as a day, week, or month. 
 * The units of this sum is left to the user to choose. 
 * @author Steven Uray
 */
public class IntervalSum {
	private final double sum;
	private final Interval timespan;
		
	public IntervalSum(Interval timespan,double sum){
		this.timespan = timespan;
		this.sum = sum;
	}
	
	public Double getSum(){
		return sum;
	}
	
	public Interval getTimespan(){
		return timespan;
	}
		
	public String toPrettyString(){
		BigDecimal sumBD = new BigDecimal(sum);
		BigDecimal sumRounded = sumBD.setScale(2, RoundingMode.HALF_EVEN);
		StringBuilder builder = new StringBuilder();
		builder.append("Timespan: "+timespan);
		builder.append(" ");
		builder.append("Sum: "+sumRounded);
		return builder.toString();
	}
}
