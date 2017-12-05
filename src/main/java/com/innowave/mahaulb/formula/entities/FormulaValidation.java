package com.innowave.mahaulb.formula.entities;

public class FormulaValidation {
	
	Boolean validation = false;
	
	String reasonIfFailure;
	
	public FormulaValidation(Boolean res) {
		this.validation = res;
	}
	
	public FormulaValidation(Boolean res, String reason) {
		this.validation = res;
		this.reasonIfFailure = reason;
	}

	public Boolean getValidation() {
		return validation;
	}

	public void setValidation(Boolean validation) {
		this.validation = validation;
	}

	public String getReasonIfFailure() {
		return reasonIfFailure;
	}

	public void setReasonIfFailure(String reasonIfFailure) {
		this.reasonIfFailure = reasonIfFailure;
	}
	
	

}
