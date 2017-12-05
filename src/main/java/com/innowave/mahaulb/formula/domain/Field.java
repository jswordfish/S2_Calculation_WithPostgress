package com.innowave.mahaulb.formula.domain;

import javax.persistence.Entity;

import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
public class Field extends Base {

String name;

String value;

String validation;



public Field(){
	
}

public Field(String name){
	this.name = name;
}

public Field(String name, String tenantId){
	this.name = name;
	this.tenantId = tenantId;
}



public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}

public String getValidation() {
	return validation;
}

public void setValidation(String validation) {
	this.validation = validation;
}




}
