package net.stevenuray.walletexplorer.views;

import java.util.Iterator;

import net.stevenuray.walletexplorer.dto.TransactionIntervalSum;
import net.stevenuray.walletexplorer.dto.TransactionIntervalSums;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class TransactionAggregateReviewGraph {		
	private static final String X_AXIS_LABEL = "Date";
	private static final String Y_AXIS_LABEL = "Transaction Volume in USD";
	
	private final TransactionIntervalSums transactionSums;
	public TransactionAggregateReviewGraph(TransactionIntervalSums transactionSums){		
		this.transactionSums = transactionSums;		
	}
	
	public LineChart<String,Number> getLineChart(){		
		final LineChart<String,Number> lineChart = getLineChartFoundation();					
		addTransactionDataToLineChart(lineChart);
		return lineChart;
	}
	
	private void addTransactionDataToLineChart(LineChart<String,Number> lineChart){
		XYChart.Series<String, Number> dataSeries = getSeries();
		lineChart.getData().add(dataSeries);
	}
	
	private void addTransactionToSeries(TransactionIntervalSum transactionSum,
			XYChart.Series<String, Number> dataSeries){
		String dataPointName = getDataPointName(transactionSum);
		Number dataPointValue = transactionSum.getTransactionIntervalSum().getSum();
		XYChart.Data<String, Number> dataPoint = new XYChart.Data<String, Number>(dataPointName, dataPointValue);
		dataSeries.getData().add(dataPoint);
	}
		
	private String getDataPointName(TransactionIntervalSum transactionSum) {
		DateTime start = transactionSum.getTransactionIntervalSum().getTimespan().getStart();
		org.joda.time.format.DateTimeFormatter formatter = ISODateTimeFormat.date();	
		return formatter.print(start);
	}
			
	private LineChart<String,Number> getLineChartFoundation(){
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(X_AXIS_LABEL);
		yAxis.setLabel(Y_AXIS_LABEL);
		
		final LineChart<String,Number> foundation = new LineChart<String,Number>(xAxis,yAxis);
		foundation.setTitle(transactionSums.getName());
		//TODO refactor this so this setting is edited in the FXML file linked to this graph. 
		foundation.setLegendVisible(false);
		return foundation;
	}
	
	private Series<String, Number> getSeries() {
		XYChart.Series<String, Number> dataSeries = new XYChart.Series<String, Number>();
		Iterator<TransactionIntervalSum> transactionSumsIterator = transactionSums.getSums();		
		while(transactionSumsIterator.hasNext()){
			TransactionIntervalSum transactionSum = transactionSumsIterator.next();
			addTransactionToSeries(transactionSum,dataSeries);			
		}
		return dataSeries;
	}	
}