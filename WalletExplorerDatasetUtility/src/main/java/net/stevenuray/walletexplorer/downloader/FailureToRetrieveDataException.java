package net.stevenuray.walletexplorer.downloader;

@SuppressWarnings("serial")
public class FailureToRetrieveDataException extends RuntimeException{

	public FailureToRetrieveDataException(Exception e) {
		super(e);
	}

}
