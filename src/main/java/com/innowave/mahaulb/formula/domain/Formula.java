package com.innowave.mahaulb.formula.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
@Entity
public class Formula extends Base{
	private static final long serialVersionUID = 1L;
	@OneToMany(cascade=CascadeType.ALL)
	List<Field> fields = new ArrayList<Field>();
	
	String formulaName;
	
	
	
	String formulaContents;
	
	public Formula(){
		
	}
	
	public Formula(String tenantId){
		this.tenantId = tenantId;
	}
	
	public Formula(String formulaName, String tenantId){
		this.formulaName = formulaName;
		this.tenantId = tenantId;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getFormulaName() {
		return formulaName;
	}

	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
	}

	

	public String getFormulaContents() {
		return formulaContents;
	}

	public void setFormulaContents(String formulaContents) {
		this.formulaContents = formulaContents;
	}
	
	

}
