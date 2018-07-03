package com.n26.exceptions;

	public enum APIException {
	    VALIDATION(1000, "Validation error"),
	    EMPTY_REQUEST_BODY(1001, "Empty request body"),
	    MISSING_TIMESTAMP(1002, "Missing timestamp field"),
	    MISSING_AMOUNT(1003, "Missing amount field"),

	    UNEXPECTED_ERROR(9999, "Internal API Error");

	    private Integer code;
	    private String message;

	    APIException(Integer code, String message){
	        this.code = code;
	        this.message = message;
	    }

	    public Integer code(){
	        return this.code;
	    }

	    public String message(){
	        return this.message;
	    }
	}

