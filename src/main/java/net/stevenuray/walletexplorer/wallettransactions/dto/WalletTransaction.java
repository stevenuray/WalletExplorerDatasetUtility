package net.stevenuray.walletexplorer.wallettransactions.dto;

import java.util.Collection;
import java.util.Collections;

import org.joda.time.DateTime;

public class WalletTransaction{
	public enum TransactionDirection{
		RECEIVED,
		SENT
	}
	private final TransactionDirection transactionDirection;
	private final DateTime transactionTime;
	private final String txid;
	private final double balance;
	private final Collection<TransactionOutput> walletTransactionOutputs;
	
	public WalletTransaction(DateTime transactionTime,String txid,double balance,
			TransactionDirection transactionDirection,Collection<TransactionOutput> transactionOutputs){
		this.transactionTime = transactionTime;
		this.txid = txid;
		this.balance = balance;
		this.transactionDirection = transactionDirection;
		this.walletTransactionOutputs = transactionOutputs;
	}	
	
	public double getBalance(){
		return balance;
	}	
	
	public TransactionDirection getTransactionDirection(){
		return transactionDirection;
	}
	
	public DateTime getTransactionTime(){
		return transactionTime;
	}
	
	public String getTxid(){
		return txid;
	}
	
	public Collection<TransactionOutput> getWalletTransactionOutputsUnmodifiable(){
		return Collections.unmodifiableCollection(walletTransactionOutputs);
	}	
}