package com.n26.dto;

import static java.lang.System.currentTimeMillis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.n26.service.impl.TransactionServiceImpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

	private Double amount;
	
	private Long timestamp;
	

    @JsonIgnore
    public boolean isValidTimeLimit() {
        return currentTimeMillis() - timestamp <= TransactionServiceImpl.VALID_TIME;
    }

}
