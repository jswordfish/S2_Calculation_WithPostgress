package com.s2.calculationEngine.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Formula;
import com.innowave.mahaulb.formula.domain.Rule;
import com.innowave.mahaulb.formula.entities.RuleInput;
import com.innowave.mahaulb.formula.entities.RuleOutput;
import com.innowave.mahaulb.formula.services.RuleService;
import com.innowave.mahaulb.formula.webservices.RulesWebService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:appContext.xml"})
@Transactional
public class TestRule {
	@Autowired
	RuleService ruleService;
	
	@Autowired
	RulesWebService ruleWebService;
	
	@Test
	@Rollback(value=false)
	public void addRule(){
		Rule rule = new Rule();
		com.innowave.mahaulb.formula.domain.Field f1 = new com.innowave.mahaulb.formula.domain.Field("UsageType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f2 = new com.innowave.mahaulb.formula.domain.Field("UsageSubType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f3 = new com.innowave.mahaulb.formula.domain.Field("ConstructionType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f4 = new com.innowave.mahaulb.formula.domain.Field("OccupancyType", "Tenant1");
		f1.setValue("Residential");
		f2.setValue("All");
		f3.setValue("All");
		f4.setValue("Owner");
		rule.getCriteria().add(f1);
		rule.getCriteria().add(f2);
		rule.getCriteria().add(f3);
		rule.getCriteria().add(f4);
		
		com.innowave.mahaulb.formula.domain.Field f5 = new com.innowave.mahaulb.formula.domain.Field("CarpetArea", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f6 = new com.innowave.mahaulb.formula.domain.Field("Age", "Tenant1");
		
		Formula formula = new Formula("Generate ALV - Formula 1", "Tenant1");
		formula.setFormulaContents("("+f5.getName()+") * (9*("+f6.getName()+")/10)");
		//f5.setValue("750");
		//f6.setValue("6");
		formula.getFields().add(f5);
		formula.getFields().add(f6);
		rule.setRuleName("Rule3");
		rule.setFormula(formula);
		rule.setTenantId("Tenant1");
		ruleService.saveOrUpdate(rule);
	}
	@Test
	@Rollback(value=false)
	public void testFetchRule(){
		Rule rule = ruleService.getUniqueRule("Rule3");
		System.out.println(rule.getCriteria());
	}
	
	@Test
	@Rollback(value=false)
	public void testFetchRuleByCriteria(){
		Rule rule = ruleService.getUniqueRule("Rule3");
		com.innowave.mahaulb.formula.domain.Field f1 = new com.innowave.mahaulb.formula.domain.Field("UsageType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f2 = new com.innowave.mahaulb.formula.domain.Field("UsageSubType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f3 = new com.innowave.mahaulb.formula.domain.Field("ConstructionType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f4 = new com.innowave.mahaulb.formula.domain.Field("OccupancyType", "Tenant1");
		f1.setValue("Residential");
		f2.setValue("All");
		f3.setValue("All");
		f4.setValue("Owner");
		Set<Field> criteria = new HashSet<Field>();
		criteria.add(f1);
		criteria.add(f2);
		criteria.add(f3);
		criteria.add(f4);
		Rule rule2 = ruleService.getUniqueRuleByFields(criteria);
		System.out.println(rule2);
	}

	@Test
	@Rollback(value=false)
	public void testRuleExecutionOwnerOrRented() {
		RuleInput input = new RuleInput();
		com.innowave.mahaulb.formula.domain.Field f1 = new com.innowave.mahaulb.formula.domain.Field("UsageType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f2 = new com.innowave.mahaulb.formula.domain.Field("UsageSubType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f3 = new com.innowave.mahaulb.formula.domain.Field("ConstructionType", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f4 = new com.innowave.mahaulb.formula.domain.Field("OccupancyType", "Tenant1");
		f1.setValue("Residential");
		f2.setValue("All");
		f3.setValue("All");
		f4.setValue("OwnerOrRented");
		Set<Field> criteria = new HashSet<>();
		criteria.add(f1);
		criteria.add(f2);
		criteria.add(f3);
		criteria.add(f4);
		
		com.innowave.mahaulb.formula.domain.Field f5 = new com.innowave.mahaulb.formula.domain.Field("CarpetArea", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f6 = new com.innowave.mahaulb.formula.domain.Field("PropertyAge", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f7 = new com.innowave.mahaulb.formula.domain.Field("MarketValue", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f8 = new com.innowave.mahaulb.formula.domain.Field("Floorfactor", "Tenant1");
		f5.setValue("650");
		f6.setValue("63");
		f7.setValue("75056");
		f8.setValue("12");
		input.setCriteriaFields(criteria);
		
		Set<Field> inputValues = new HashSet<>();
		inputValues.add(f5);
		inputValues.add(f6);
		inputValues.add(f7);
		inputValues.add(f8);
		input.setValueFields(inputValues);
		Response response = ruleWebService.identifyAndExecute(input, "tok");
		RuleOutput output = response.readEntity(RuleOutput.class);
		System.out.println(output.getOutput());
	}
	
	@Test
	@Rollback(value=false)
	public void testRuleExecutionLand() {
		RuleInput input = new RuleInput();
		com.innowave.mahaulb.formula.domain.Field f1 = new com.innowave.mahaulb.formula.domain.Field("UsageType", "Tenant1");
		f1.setValue("Land");
	
		Set<Field> criteria = new HashSet<>();
		criteria.add(f1);
		
		
		com.innowave.mahaulb.formula.domain.Field f5 = new com.innowave.mahaulb.formula.domain.Field("CarpetArea", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f6 = new com.innowave.mahaulb.formula.domain.Field("SDRRRate", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f7 = new com.innowave.mahaulb.formula.domain.Field("MarketValue", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f8 = new com.innowave.mahaulb.formula.domain.Field("Floorfactor", "Tenant1");
		f5.setValue("650");
		f6.setValue("5.8");
		f7.setValue("75056");
		f8.setValue("12");
		input.setCriteriaFields(criteria);
		//(CarpetArea)/(SDRRRate) * (MarketValue) - (5 * (Floorfactor))
		
		Set<Field> inputValues = new HashSet<>();
		inputValues.add(f5);
		inputValues.add(f6);
		inputValues.add(f7);
		inputValues.add(f8);
		input.setValueFields(inputValues);
		Response response = ruleWebService.identifyAndExecute(input, "tok");
		RuleOutput output = response.readEntity(RuleOutput.class);
		System.out.println(output.getOutput());
	}
	
	@Test
	@Rollback(value=false)
	public void testRuleExecutionOther() {
		RuleInput input = new RuleInput();
		com.innowave.mahaulb.formula.domain.Field f1 = new com.innowave.mahaulb.formula.domain.Field("UsageType", "Tenant1");
		f1.setValue("OtherResidentialShopCommercialIndustrial");
	
		Set<Field> criteria = new HashSet<>();
		criteria.add(f1);
		
		
		com.innowave.mahaulb.formula.domain.Field f5 = new com.innowave.mahaulb.formula.domain.Field("CarpetArea", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f6 = new com.innowave.mahaulb.formula.domain.Field("SDRRRate", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f7 = new com.innowave.mahaulb.formula.domain.Field("MarketValue", "Tenant1");
		com.innowave.mahaulb.formula.domain.Field f8 = new com.innowave.mahaulb.formula.domain.Field("Floorfactor", "Tenant1");
		f5.setValue("36");
		f6.setValue("95");
		f7.setValue("92");
		f8.setValue("71");
		input.setCriteriaFields(criteria);
		//(CarpetArea)/(SDRRRate) * (MarketValue) - (5 * (Floorfactor))
		
		Set<Field> inputValues = new HashSet<>();
		inputValues.add(f5);
		inputValues.add(f6);
		inputValues.add(f7);
		inputValues.add(f8);
		input.setValueFields(inputValues);
		Response response = ruleWebService.identifyAndExecute(input, "tok");
		RuleOutput output = response.readEntity(RuleOutput.class);
		System.out.println(output.getOutput());
	}
}
