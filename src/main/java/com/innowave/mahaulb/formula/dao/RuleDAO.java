package com.innowave.mahaulb.formula.dao;

import java.util.Set;

import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Rule;

public interface RuleDAO extends JPADAO<Rule, Long>{
	
	public Rule getUniqueRuleByFields(Set<Field> criteria);
	
	public Rule getUniqueRuleByFieldsAndTenant(Set<Field> criteria, String tenant);
	
	public Rule getUniqueRuleByTenant(String ruleName, String tenant);

}