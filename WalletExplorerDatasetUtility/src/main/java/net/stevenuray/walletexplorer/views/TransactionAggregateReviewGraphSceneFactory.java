package net.stevenuray.walletexplorer.views;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import net.stevenuray.walletexplorer.aggregation.WalletTransactionSums;
import net.stevenuray.walletexplorer.categories.WalletCategoryTransactionSums;
import net.stevenuray.walletexplorer.executables.WalletReviewer;

public class TransactionAggregateReviewGraphSceneFactory {
	private final int sceneWidth;
	private final int sceneHeight;
	
	public TransactionAggregateReviewGraphSceneFactory(int width, int height){
		this.sceneWidth = width;
		this.sceneHeight = height;
	}
	
	public Scene getScene(WalletCategoryTransactionSums transactionSums) {
		TransactionAggregateReviewGraph categoryReviewGraph = new TransactionAggregateReviewGraph(transactionSums);
		Scene scene = getSceneWithGraphAndStyleSheet(categoryReviewGraph);
		return scene;
	}

	public Scene getScene(WalletTransactionSums transactionSums) {
		TransactionAggregateReviewGraph categoryReviewGraph = new TransactionAggregateReviewGraph(transactionSums);
		Scene scene = getSceneWithGraphAndStyleSheet(categoryReviewGraph);
		return scene;
	}	
	
	private void addStyleSheetToScene(Scene scene){
		String styleSheetPath = getStyleSheetPath();
		scene.getStylesheets().add(styleSheetPath);
	}
		
	private Scene getSceneWithGraphAndStyleSheet(TransactionAggregateReviewGraph graph){
		LineChart<String,Number> reviewChart = graph.getLineChart();
		Scene scene = new Scene(reviewChart,sceneWidth,sceneHeight);		
		addStyleSheetToScene(scene);
		return scene;
	}
	
	private String getStyleSheetPath(){
		return WalletReviewer.class.getResource("TransactionAggregateReviewGraph.fxml")
		.toExternalForm();
	}
}