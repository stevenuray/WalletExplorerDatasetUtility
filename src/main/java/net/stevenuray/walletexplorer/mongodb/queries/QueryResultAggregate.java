package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.List;

import org.bson.Document;
import org.joda.time.Interval;

public class QueryResultAggregate {
	private final List<Document> queryResults;	
	public List<Document> getQueryResults() {
		return queryResults;
	}
	private final Interval queryResultInterval;	
	public Interval getQueryResultInterval() {
		return queryResultInterval;
	}

	public QueryResultAggregate(List<Document> queryResults,Interval queryResultInterval){
		this.queryResults = queryResults;
		this.queryResultInterval = queryResultInterval;
	}
}
