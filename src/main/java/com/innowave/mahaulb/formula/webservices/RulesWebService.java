package com.innowave.mahaulb.formula.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.innowave.mahaulb.formula.common.Constants;
import com.innowave.mahaulb.formula.common.S2GenericException;
import com.innowave.mahaulb.formula.common.SpringUtil;
import com.innowave.mahaulb.formula.common.Util;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Formula;
import com.innowave.mahaulb.formula.domain.Rule;
import com.innowave.mahaulb.formula.entities.RuleInput;
import com.innowave.mahaulb.formula.entities.RuleOutput;
import com.innowave.mahaulb.formula.services.RuleService;

@Path("/rulesService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class RulesWebService {

	//@Autowired
	RuleService ruleService;
//	@PostConstruct
//	public void init() {
//		ruleService =  SpringUtil.getService(RuleService.class);
//	}
	
	@POST
	@Path("/identifyAndExecute/token/{token}")
	@Produces("application/json")
	@Consumes("application/json")
	public javax.ws.rs.core.Response identifyAndExecute(RuleInput ruleInput, @PathParam("token") String token){
		try {
			//Set<Field> criteria = Util.replaceSpaceIfAroundInCriteriaFieldValues(ruleInput.getCriteriaFields());
			ruleService =  SpringUtil.getService(RuleService.class);
			Rule rule = ruleService.getUniqueRuleByFields(ruleInput.getCriteriaFields());
			Formula formula = populateFormulaWithValues(rule.getFormula(), ruleInput.getValueFields());
			RuleOutput ruleOutput = ruleService.execute(formula);
			return Response.ok().entity(ruleOutput).build();
		} catch (S2GenericException e) {
			// TODO Auto-generated catch block
			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/hello")
	@Produces("application/json")
	@Consumes("application/json")
	public String helloWorld() {
		return "Hello World";
	}
	
	@POST
	@Path("/identifyAndExecuteForTenant/tenant/{tenant}/token/{token}")
	@Produces("application/json")
	@Consumes("application/json")
	public javax.ws.rs.core.Response identifyAndExecute(RuleInput ruleInput, @PathParam("tenant") String tenant, @PathParam("token") String token){
		try {
			//Set<Field> criteria = Util.replaceSpaceIfAroundInCriteriaFieldValues(ruleInput.getCriteriaFields());
			ruleService =  SpringUtil.getService("ruleService", RuleService.class);
			Rule rule = ruleService.getUniqueRuleByFieldsAndTenant(ruleInput.getCriteriaFields(), tenant);
			Formula formula = populateFormulaWithValues(rule.getFormula(), ruleInput.getValueFields());
			RuleOutput ruleOutput = ruleService.execute(formula);
			return Response.ok().entity(ruleOutput).build();
		} catch (S2GenericException e) {
			// TODO Auto-generated catch block
			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	private Formula populateFormulaWithValues(Formula formula, Set<Field> input) {
		if(formula.getFields().size() > input.size()) {
			throw new S2GenericException("INSUFFICIENT_INPUT");
		}
		else {
			Map<String,String> map = new HashMap<>();
			for(Field ip : input) {
				map.put(ip.getName(), ip.getValue());
			}
			
			for(Field formulaField : formula.getFields()) {
				if(map.get(formulaField.getName()) == null) {
					throw new S2GenericException("DATA_NOT_PROVIDED_FOR-"+formulaField.getName());
				}
				formulaField.setValue(map.get(formulaField.getName()).trim());
			}
		}
	return formula;
	}
	
	
}
