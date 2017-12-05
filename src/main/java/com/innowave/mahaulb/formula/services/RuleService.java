package com.innowave.mahaulb.formula.services;

import java.util.List;
import java.util.Set;

import com.innowave.mahaulb.formula.common.S2GenericException;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Formula;
import com.innowave.mahaulb.formula.domain.Rule;
import com.innowave.mahaulb.formula.entities.FormulaValidation;
import com.innowave.mahaulb.formula.entities.RuleOutput;

public interface RuleService extends BaseService {
	
	public void saveOrUpdate(Rule rule) throws S2GenericException;
	
	public Rule getUniqueRule(String ruleName) throws S2GenericException;
	
	public Rule getUniqueRuleByFields(Set<Field> criteria) throws S2GenericException;
	
	public RuleOutput execute(Formula formula) throws S2GenericException;
	
	public FormulaValidation validate(String expression, List<Field> flds);
	
	public List<Rule> getAllRules() throws S2GenericException;
	
	public void deleteRule(Long id)throws S2GenericException;
	
	public Rule getUniqueRuleByFieldsAndTenant(Set<Field> criteria, String tenant) throws S2GenericException;
	public Rule getUniqueRuleByTenant(String ruleName, String tenant) throws S2GenericException;
}
