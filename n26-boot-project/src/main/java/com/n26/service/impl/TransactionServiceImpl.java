package com.n26.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n26.dto.TransactionDTO;
import com.n26.exceptions.OutdatedTransactionException;
import com.n26.service.StatisticsService;
import com.n26.service.TransactionsService;

@Service
public class TransactionServiceImpl implements TransactionsService {
	
	@Autowired
	private StatisticsService statService;

	public static final int VALID_TIME = 60000;
	
	@Override
	public void registerTransaction(TransactionDTO request) throws OutdatedTransactionException {
		if(validateTransaction(request))
		statService.addTransaction(request);
		

	}

	private boolean validateTransaction(TransactionDTO request) throws OutdatedTransactionException {

		if ((System.currentTimeMillis() - request.getTimestamp()) < VALID_TIME) {
			return true;
		} else {
			throw new OutdatedTransactionException("Transaction is Outdated");
		}

	}
	
	}
