package com.ujm.xmltech.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ujm.xmltech.dao.TransactionDao;
import com.ujm.xmltech.entity.Files;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;

@Service("TransactionService")
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionDao dao;

	
	
	public void createTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(500);
		transaction.setEndToEndId("uniqIdentifier");
		dao.createTransaction(transaction);
	}

	@Override
	public void createTransaction(Files t) {
		dao.createTransaction(t);
	}
	
	@Override
	public Transaction findTransactionByMndtId(String id){
		return dao.findTransactionByMndtId(id);
	}
	
	
	@Override
	public Transaction findTransactionByMsgId(String mandat_id){
		return dao.findTransactionByMsgId(mandat_id);
	}
	
	

	@Override
	public List<Transaction> findTransactionByProces(){
		return dao.findTransactionByProces();
	}
	
	
	
	@Override
	public void updatePrececed(Transaction transaction){
		 dao.updatePrececed(transaction);
	}
}
