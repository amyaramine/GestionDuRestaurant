package com.ujm.xmltech.services;

import java.util.List;

import com.ujm.xmltech.entity.Files;
import com.ujm.xmltech.entity.Transaction;

public interface TransactionService {

	void createTransaction();

	void createTransaction(Files t);
	
	Transaction findTransactionByMndtId(String id);
	
	Transaction findTransactionByMsgId(String mandat_id);
	
	List<Transaction> findTransactionByProces();
	
	public void updatePrececed(Transaction transaction);
}
