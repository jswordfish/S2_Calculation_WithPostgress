package com.innowave.mahaulb.formula.domain;

public enum KEY_VALUE_TYPE {
	ALL("ALL"), SPECIFIC("SPECIFIC");
	
	private String type;
	private KEY_VALUE_TYPE(String type){
		this.type = type;
	}
	public String getType() {
		return type;
	}
	
	

}
