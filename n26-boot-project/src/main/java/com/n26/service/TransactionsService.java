package com.n26.service;

import com.n26.dto.TransactionDTO;
import com.n26.exceptions.OutdatedTransactionException;

public interface TransactionsService {

	public void registerTransaction(TransactionDTO dto) throws OutdatedTransactionException;
}
