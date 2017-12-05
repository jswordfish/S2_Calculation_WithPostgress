package com.innowave.mahaulb.formula.entities;

import java.util.HashSet;
import java.util.Set;

import com.innowave.mahaulb.formula.domain.Field;

public class RuleInput {
Set<Field> criteriaFields = new HashSet<Field>();
	
	Set<Field> valueFields = new HashSet<>();

	public Set<Field> getCriteriaFields() {
		return criteriaFields;
	}

	public void setCriteriaFields(Set<Field> criteriaFields) {
		this.criteriaFields = criteriaFields;
	}

	public Set<Field> getValueFields() {
		return valueFields;
	}

	public void setValueFields(Set<Field> valueFields) {
		this.valueFields = valueFields;
	}

	
}
