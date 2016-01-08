package net.stevenuray.walletexplorer.conversion.collection;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**Serves as a statistics class for a CollectionConverter.
 * 
 * @author Steven Uray 2015-10-12 
 */
/*TODO Combine into interface or superclass with TableLoadResult
 * 
 */
public class ConversionResults {
	private boolean conversionComplete = false;
	private Interval conversionTimespan;
	private int failedConversions = 0;
	private final DateTime startTime;
	private int successfulConversions = 0; 
	public ConversionResults(){
		startTime = new DateTime();
	} 
	
	public void endConversion(){
		if(!conversionComplete){
			DateTime endTime = new DateTime();
			conversionTimespan = new Interval(startTime,endTime);
			conversionComplete = true;
		} else{
			throw new IllegalStateException("ConversionResults had it's endConversion() called multiple times!");
		}
	}
	
	public Interval getConversionTimespan(){
		if(conversionComplete){
			return conversionTimespan;
		} else{
			throw new IllegalStateException("Must call endConversion() before getConversionTimespan()!");
		}
	}
	
	public int getFailedConversions() {
		return failedConversions;
	}
	
	public int getSuccessfulConversions() {
		return successfulConversions;
	}

	public void iterateFailedConversions(){
		failedConversions++;
	}

	public void iterateSuccessfulConversions(){
		successfulConversions++;
	}
}
