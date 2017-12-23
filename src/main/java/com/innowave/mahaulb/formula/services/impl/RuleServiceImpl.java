package com.innowave.mahaulb.formula.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.innowave.mahaulb.formula.common.S2GenericException;
import com.innowave.mahaulb.formula.common.Util;
import com.innowave.mahaulb.formula.dao.JPADAO;
import com.innowave.mahaulb.formula.dao.RuleDAO;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Formula;
import com.innowave.mahaulb.formula.domain.Rule;
import com.innowave.mahaulb.formula.entities.FormulaValidation;
import com.innowave.mahaulb.formula.entities.RuleOutput;
import com.innowave.mahaulb.formula.services.RuleService;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

@Component("ruleService")
@org.springframework.transaction.annotation.Transactional(propagation= Propagation.REQUIRED, rollbackFor=S2GenericException.class)
public class RuleServiceImpl extends BaseServiceImpl<Long, Rule> implements RuleService{
	@Autowired
    protected RuleDAO dao;
	
	

	@PostConstruct
    public void init() throws Exception {
	 super.setDAO((JPADAO) dao);
    }
    
    @PreDestroy
    public void destroy() {
    }
    
    @Override
    public void setEntityManagerOnDao(EntityManager entityManager){
    	dao.setEntityManager(entityManager);
    }
    
    

	@Override
	public void saveOrUpdate(Rule rule) throws S2GenericException {
		// TODO Auto-generated method stub
		if(rule.getRuleName() == null || rule.getRuleName().trim().length() == 0){
			throw new S2GenericException("INVALID RULE NAME");
		}
		//rule = Util.replaceSpaceInFieldValues(rule);
		/*
		 * Make sure tenant id is available in Rule as well as all of its nested objects
		 */
		Util.isTenantIdPresent(rule);
		
//		Rule rule2 = getUniqueRule(rule.getRuleName() );
		Rule rule2 = getUniqueRuleByTenant(rule.getRuleName(), rule.getTenantId() );
		
			if(rule2 == null){
				rule.getRuleName().trim();
				rule.getFormula().getFormulaName().trim();
				rule.getFormula().getFormulaContents().trim();
				rule.setCreatedDate(new Date());
				dao.persist(rule);
			}
			else{
//				rule.setId(rule2.getId());
//				rule.getFormula().setId(rule2.getFormula().getId());
//				org.dozer.Mapper mapper = new DozerBeanMapper();
//				mapper.map(rule, rule2);
//				dao.merge(rule2);
					if(rule2.getCreatedDate() == null) {
						rule.setCreatedDate(new Date());
						
					}
					else {
						rule.setCreatedDate(rule2.getCreatedDate());
					}
				dao.remove(rule2);
				rule.setLastModifiedDate(new Date());
				dao.persist(rule);
			}
	}

	public Rule getUniqueRule(String ruleName) throws S2GenericException {
		//Company.getUniqueCompany
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("ruleName", ruleName);
		List<Rule> rules = findByNamedQueryAndNamedParams(
				"Rule.getUniqueRule", queryParams);
		if(rules.size() > 1){
			throw new S2GenericException("TOO_MANY_Rules_BY_SAME_NAME");
		}
		if(rules.size() == 0){
			return null;
		}
		//MultipleBagFetchException
		Rule ret = rules.get(0);
		List<Field> flds = ret.getCriteria();
		flds.size();
		List<Field> flds2 = ret.getFormula().getFields();
		flds2.size();
		return ret;
	}

	@Override
	public Rule getUniqueRuleByFields(Set<Field> criteria)
			throws S2GenericException {
		// TODO Auto-generated method stub
		return dao.getUniqueRuleByFields(criteria);
	}

	@Override
	public RuleOutput execute(Formula formula) throws S2GenericException {
		ExpressionBuilder builder = new ExpressionBuilder(formula.getFormulaContents());
		for(Field field : formula.getFields()) {
			builder = builder.variable(field.getName());
		}
		Expression expression = builder.build();
		for(Field field : formula.getFields()) {
			expression.setVariable(field.getName(), Double.parseDouble(field.getValue()));
		}
		String out = ""+expression.evaluate();
		RuleOutput output = new RuleOutput();
		output.setFormulaExpression(formula.getFormulaContents());
		output.setFormulaName(formula.getFormulaName());
		output.setOutput(out);
		return output;
	}

	@Override
	public FormulaValidation validate(String expression, List<Field> flds) {
		try {
			ExpressionBuilder builder = new ExpressionBuilder(expression);
			for(Field field : flds) {
				builder = builder.variable(field.getName());
			}
			Expression exp = builder.build();
			for(Field field : flds) {
				exp.setVariable(field.getName(), Double.parseDouble(field.getValue()));
			}
			ValidationResult result = exp.validate();
			FormulaValidation formulaValidation = new FormulaValidation(result.isValid());
			if(!result.isValid()) {
				List<String> er = result.getErrors();
				String msg = "";
				for(String e : er) {
					msg += e+"\n";
				}
				formulaValidation.setReasonIfFailure(msg);
			}
			return formulaValidation;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new FormulaValidation(false, e.getMessage());
		}
	}

	@Override
	public List<Rule> getAllRules() throws S2GenericException {
		List<Rule> rules = super.findAll();
		for(Rule rule : rules) {
			rule.getCriteria().size();
			rule.getFormula().getFields().size();
			
		}
		return rules;
	}

	@Override
	public void deleteRule(Long id) throws S2GenericException {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	public Rule getUniqueRuleByFieldsAndTenant(Set<Field> criteria, String tenant) throws S2GenericException {
		// TODO Auto-generated method stub
		return dao.getUniqueRuleByFieldsAndTenant(criteria, tenant);
	}

	@Override
	public Rule getUniqueRuleByTenant(String ruleName, String tenant) throws S2GenericException {

		Rule rule = dao.getUniqueRuleByTenant(ruleName, tenant);
			if(rule == null) {
				return null;
			}
			List<Field> flds = rule.getCriteria();
			flds.size();
			List<Field> flds2 = rule.getFormula().getFields();
			flds2.size();
			return rule;
	}

	
    
   
	
}