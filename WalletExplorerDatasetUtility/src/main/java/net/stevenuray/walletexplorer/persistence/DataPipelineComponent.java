package net.stevenuray.walletexplorer.persistence;

public interface DataPipelineComponent {
	/**Optional method implementations may use to prepare for use. 
	 * Users of DataPipelineComponent subclasses should call this function before use.
	 **/
	void start();
	
	/**Optional method implementations may use to shut down after use. 
	 * Users of DataPipelineComponent subclasses should call this function before use.
	 **/
	void finish(); 
}
