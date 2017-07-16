package net.stevenuray.walletexplorer.mongodb.queries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.bson.Document;

public class QueryResultAggregator implements Callable<List<Document>> {
	private final List<Document> queryResults = new ArrayList<Document>();	
	private final int aggregateTargetSize;	
	private final Iterator<Document> cursor;
	
	protected QueryResultAggregator(int aggregateTargetSize, Iterator<Document> cursor){		
		this.aggregateTargetSize = aggregateTargetSize;	
		this.cursor = cursor;
	}
	
	public List<Document> call() throws Exception {				
		while(cursor.hasNext() && queryResults.size() <= aggregateTargetSize){
			Document currentDocument = cursor.next();
			queryResults.add(currentDocument);			
		}		
		return queryResults;
	}		
}
