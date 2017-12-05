package com.innowave.mahaulb.formula.domain;

import java.util.ArrayList;
import java.util.List;

public class Input {

	List<Field> criteriaFields = new ArrayList<Field>();
	
	List<Field> valueFields = new ArrayList<>();

	public List<Field> getCriteriaFields() {
		return criteriaFields;
	}

	public void setCriteriaFields(List<Field> criteriaFields) {
		this.criteriaFields = criteriaFields;
	}

	public List<Field> getValueFields() {
		return valueFields;
	}

	public void setValueFields(List<Field> valueFields) {
		this.valueFields = valueFields;
	}
	
	
}
