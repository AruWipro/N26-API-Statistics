package com.n26.service;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.TransactionDTO;

public interface StatisticsService {

	   public void addTransaction(TransactionDTO transaction) ;
	   
	   public StatisticsDTO getLatestStats(); 
	   
		   
}
