package net.stevenuray.walletexplorer.testobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;

public class TestTransactionOutputs {

	public static TransactionOutput getOutput(){
		String walletId = "BTC-e.com";
		double amount = 1;
		return new TransactionOutput(amount,walletId);
	}

	public static Collection<TransactionOutput> getOutputs() {
		TransactionOutput transactionOutput = getOutput();
		List<TransactionOutput> outputList = new ArrayList<TransactionOutput>();
		outputList.add(transactionOutput);
		return outputList;
	}
}