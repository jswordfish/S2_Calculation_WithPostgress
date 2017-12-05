package com.innowave.mahaulb.formula.jsf.managedBeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.innowave.mahaulb.formula.common.Util;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Rule;
import com.innowave.mahaulb.formula.entities.FormulaValidation;
import com.innowave.mahaulb.formula.entities.RuleInput;
import com.innowave.mahaulb.formula.entities.RuleOutput;
import com.innowave.mahaulb.formula.services.RuleService;

@ManagedBean(name = "rulesManager", eager = true)
@ELBeanName(value = "rulesManager")
@Component (value = "rulesManager")
@SessionScoped
@Service
public class RulesController {
	RuleService ruleService;
	
	List<Rule> rules = new ArrayList<>();
	
	String d = "";
	
	String tenantId = "Tenant1";
	String ruleXml = "";
	
	
	Rule rule = new Rule();
	
	Boolean ruleNameEdit = true;
	
	String title = "Create New Rule";
	
	String error;
	
	// initialize a Random object somewhere; you should only need one
	Random random = new Random();
	
	Rule testRule;
	
	String webServiceUrl = "http://localhost:8080/ws/rest/rulesService/identifyAndExecuteForTenant/tenant/{tenant}/token/test";
	
	String httpMethod = "POST";
	
	String inputJson = "";
	
	String outputJson = "";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@PostConstruct
    private void init() {
		ruleService = com.innowave.mahaulb.formula.common.SpringUtil.getService("ruleService", RuleService.class);
		rules = ruleService.getAllRules();
		setRuleXmlWithDefaultValues();
    }

	public String edit(Rule rule) {
		this.ruleNameEdit = false;
		setTitle("Please don't change Rule Name");
		this.rule = rule;
		//this.rule = Util.setAndReturnTenantId(this.rule, "Tenant1");
		this.ruleXml = Util.getXml(rule);
		return "rule.xhtml?faces-redirect=false";
	}
	
	public String callWebService(Rule rule) {
		try {
			setTitle("Rule Name "+rule.getRuleName());
			this.rule = rule;
			RuleInput ruleInput = new RuleInput();
			Set<Field> cr = new HashSet<>();
			cr.addAll(rule.getCriteria());
			ruleInput.setCriteriaFields(cr);
			List<Field> flds = rule.getFormula().getFields();
				for(Field f : flds) {
					f.setValue(random.nextInt(100)+"");
				}
			Set<Field> vl = new HashSet<>();
			vl.addAll(flds);
			ruleInput.setValueFields(vl);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String json = mapper.writeValueAsString(ruleInput);
			setInputJson(json);
			return "WebServiceTest.xhtml?faces-redirect=false";
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			setInputJson(e.getMessage());
			setOutputJson("Soemthing is wrong. Go back and contact Admin");
			return "WebServiceTest.xhtml?faces-redirect=false";
		}
	}
	
	
	
	public String delete(Long ruleId) {
		ruleService.deleteRule(ruleId);
			for(Rule r : getRules()) {
				if(r.getId() == ruleId) {
					getRules().remove(r);
					break;
				}
			}
		
		return "RulesAdminPanel.xhtml?faces-redirect=false";
	}
	
	public String createNew() {
		this.ruleNameEdit = true;
		setRuleXmlWithDefaultValues();
		return "rule.xhtml?faces-redirect=false";
	}
	
	void setRuleXmlWithDefaultValues(){
		setTitle("Create New Rule");
		this.rule = new Rule();
		
		this.rule.setTenantId(getTenantId());
		//this.rule = Util.setAndReturnTenantId(this.rule, "Tenant1");
		
		this.rule.setRuleName("Rule Name - Change this");
		this.rule.getFormula().setFormulaName("Formula Nama - Change this");
		
		Field critField = new Field();
		critField.setName("Field Name 1 - Change this");
		critField.setValue("Test Value");
		this.rule.getCriteria().add(critField);
		
		Field formulaField = new Field();
		formulaField.setName("Field Name 1 - Change this");
		formulaField.setValue("NA");
		this.rule.getFormula().getFields().add(formulaField);
		
		this.rule.getFormula().setFormulaContents("{Expression}");
		this.ruleXml = Util.getXml(rule);
		
	}
	
	
	
	public boolean validate() {
		Rule rule = Util.getObject(this.ruleXml, Rule.class);
		if(rule.getRuleName() == null || rule.getRuleName().trim().length() == 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid Data", "Rule Name can not be blank");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			RequestContext context = RequestContext.getCurrentInstance();
			return false;
		}
		
		if(!ruleNameEdit) {
			//update
			/**
			 * If user has changed the rule name in the xml editor
			 */
			if(!rule.getRuleName().equalsIgnoreCase(this.rule.getRuleName())) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Update Error Dialog", "Can't Change Rule Name! ");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				return false;
			}
		}
		else {
			//create
			/**
			 * Step 1 validate if the new rule name is existing
			 */
			if(rule.getId() != null && rule.getId() > 0) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Save Error Dialog", "New Rule can not have an ID property set. Remove the value ");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				return false;
			}
			/**
			 * Step 2 validate if the new rule name is existing
			 */
			if( ruleService.getUniqueRule(rule.getRuleName()) != null) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Save Error Dialog", "Rule with the supplied name already exists. Please provide a new name! ");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				return false;
			}
			
			
		}
		String field = findFieldWithNameHavingSpace(rule.getCriteria());
			if(field != null) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Save Error Dialog", "Field - "+field+" can not have space in between");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				return false;
			}
			
		field = findFieldWithValueHavingSpace(rule.getCriteria());
			if(field != null) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Save Error Dialog", "Criteria Field Value- "+field+" can not have space in between");
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				return false;
			}
		field = 	findFieldWithNameHavingSpace(rule.getFormula().getFields());
		if(field != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rule Save Error Dialog", "Field - "+field+" can not have space in between");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return false;
		}
		/**
		 * Check if a similar rule, albeit with different name, exists with same criteria fields and values
		 */
		Set<Field> fields = new HashSet<>();
		fields.addAll(rule.getCriteria());
		Rule r = ruleService.getUniqueRuleByFields(fields);
		if(r != null && (!r.getRuleName().equalsIgnoreCase(rule.getRuleName()))) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Similar Rule Exists", "Rule -"+r.getRuleName()+" exists with same criteria fields and values. You should delete that before creating/updating this one");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return false;
		}
		/**
		 * Trim All field names and values
		 */
		trimAll(rule.getCriteria());
		trimAll(rule.getFormula().getFields());
		
			for(Field f : rule.getFormula().getFields()) {
				f.setValue("34");
			}
		FormulaValidation val = ruleService.validate(rule.getFormula().getFormulaContents(), rule.getFormula().getFields());
		if(!val.getValidation()) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Formula Express Invalid", "Details -"+val.getReasonIfFailure());
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return false;
		}
		
	return true;
	}
	
	private String findFieldWithNameHavingSpace(List<Field> fields) {
		for(Field f : fields) {
			if(f.getName().contains(" ")) {
				return f.getName();
			}
		}
		return null;
	}
	private String findFieldWithValueHavingSpace(List<Field> fields) {
		for(Field f : fields) {
			if(f.getValue().contains(" ")) {
				return f.getName();
			}
		}
		return null;
	}
	
	
	private void trimAll(List<Field> fields) {
		for(Field f : fields) {
			f.setName(f.getName().trim());
			f.setValue(f.getValue().trim());
		}
	}
	
	public void saveRule(ActionEvent event) {
		if(validate()) {
			Rule rule = Util.getObject(this.ruleXml, Rule.class);
			Set<Field> fields = new HashSet<>();
			fields.addAll(rule.getCriteria());
			ruleService.saveOrUpdate(rule);
			setRule(rule);
			this.rules = ruleService.getAllRules();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Rule persisted. Click on Go Back Button");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
		}
		
		
	}
	
//	public void showInputFields() {
//		String fields = "";
//		for(Field field : rule.getCriteria()) {
//			fields+= field.getName()+"-"+field.getValue()+"\n";
//		}
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Input Fields for "+rule.getRuleName(), fields);
//		RequestContext.getCurrentInstance().showMessageInDialog(message);
//	}
	
	public String navigateToTestPage(Rule rule) {
		this.testRule = rule;
		for(Field field : testRule.getFormula().getFields()) {
			field.setValue(random.nextInt(100)+"");
		//	fields+= field.getName()+"-"+field.getValue()+"\n";
		}
		return "showTest.xhtml?faces-redirect=false";
	}
	
	public void testRule() {
		String fields = "";
		
		
		RuleOutput output = ruleService.execute(testRule.getFormula());
		String message = "Testing Rule with following test values - "+fields;
		message += "Formula Executed..\n";
		message+= output.getOutput();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Testing Rule with "+testRule.getRuleName(), message);
		RequestContext.getCurrentInstance().showMessageInDialog(msg);
	}

	
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getRuleXml() {
		return ruleXml;
	}

	public void setRuleXml(String ruleXml) {
		this.ruleXml = ruleXml;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public Boolean getRuleNameEdit() {
		return ruleNameEdit;
	}

	public void setRuleNameEdit(Boolean ruleNameEdit) {
		this.ruleNameEdit = ruleNameEdit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Rule getTestRule() {
		return testRule;
	}

	public void setTestRule(Rule testRule) {
		this.testRule = testRule;
	}

	public String getWebServiceUrl() {
		return webServiceUrl;
	}

	public void setWebServiceUrl(String webServiceUrl) {
		this.webServiceUrl = webServiceUrl;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getInputJson() {
		return inputJson;
	}

	public void setInputJson(String inputJson) {
		this.inputJson = inputJson;
	}

	public String getOutputJson() {
		return outputJson;
	}

	public void setOutputJson(String outputJson) {
		this.outputJson = outputJson;
	}

	
	public void hitWebservice() {
		try {
			ClientConfig configuration = new ClientConfig();
			configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 1000*1000);
			configuration = configuration.property(ClientProperties.READ_TIMEOUT, 1000*1000);
			Client client = ClientBuilder.newBuilder().newClient(configuration).register(new  com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider());
			//client.
			//client.
			String u = getWebServiceUrl().replace("{tenant}", getTenantId());
			WebTarget target = client.target(u);
			
			
			RuleInput input = mapper.readValue(getInputJson(), RuleInput.class);
			Invocation.Builder builder = target.request();
			RuleOutput res = builder.post(javax.ws.rs.client.Entity.entity(input, MediaType.APPLICATION_JSON), RuleOutput.class);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String op = mapper.writeValueAsString(res);
			setOutputJson(op);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setOutputJson("Got Error after hitting the web service - "+e.getMessage());
		} 
	}
	
	public String reset() {
		setOutputJson("");
		return "RulesAdminPanel.xhtml?faces-redirect=false";
	}
	
}
