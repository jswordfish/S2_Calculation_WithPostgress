package com.innowave.mahaulb.formula.common;

public class S2GenericException extends RuntimeException{

	public S2GenericException(){
		super();
	}
	
	public S2GenericException(String message){
		super(message);
	}
	
	public S2GenericException(String message, Throwable t){
		super(message, t);
	}

}
