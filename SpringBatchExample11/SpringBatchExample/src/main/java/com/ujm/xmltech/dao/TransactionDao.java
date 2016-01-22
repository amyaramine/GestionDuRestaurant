package com.ujm.xmltech.dao;

import java.util.List;

import com.ujm.xmltech.entity.Files;
import com.ujm.xmltech.entity.Transaction;

public interface TransactionDao {

  void createTransaction(Transaction transaction);
	
	void createTransaction(Files file);

  Transaction findTransactionById(long id);

  Transaction findTransactionByMndtId(String id);

  Transaction findTransactionByMsgId(String mandat_id);
  
  List<Transaction> findTransactionByProces();
  
  
  public void updatePrececed(Transaction transaction);
  
}
