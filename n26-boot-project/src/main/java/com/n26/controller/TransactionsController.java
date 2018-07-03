package com.n26.controller;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.dto.TransactionDTO;
import com.n26.exceptions.APIException;
import com.n26.exceptions.OutdatedTransactionException;
import com.n26.service.TransactionsService;

@RestController
@RequestMapping("transactions")
public class TransactionsController {
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(TransactionsController.class);

	@Autowired
	private TransactionsService transactionsService;

	@PostMapping
	public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws ValidationException {

		LOG.info("Inside Create Transaction" + transactionDTO.toString());
		try { 
			
		if(transactionDTO == null) throw new ValidationException(APIException.EMPTY_REQUEST_BODY.message());
        if(transactionDTO.getTimestamp() == null) throw new ValidationException(APIException.MISSING_TIMESTAMP.message());
        if(transactionDTO.getAmount() == null) throw new ValidationException(APIException.MISSING_AMOUNT.message());

			transactionsService.registerTransaction(transactionDTO);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (final OutdatedTransactionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}

}
