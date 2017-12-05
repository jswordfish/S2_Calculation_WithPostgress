package com.innowave.mahaulb.formula.common;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Rule;

public class Util {
	
	
	public static String getXml(Object obj)  {
		try {
			XmlMapper mapper = new XmlMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new S2GenericException("XML_CONVERSION_PROBLEM");
		}
	}
	
	public static <T extends Object> T getObject(String obj, Class<T> type)  {
		try {
			XmlMapper mapper = new XmlMapper();
			//mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.readValue(obj, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new S2GenericException("XML_CONVERSION_PROBLEM");
		}
	}
	
	public static Rule replaceSpaceInFieldValues(Rule rule) {
    	for(Field field : rule.getCriteria()) {
    			if(field.getValue() == null) {
    				throw new S2GenericException("Input Field value is null for "+field.getName());
    			}
    		field.setValue(field.getValue().trim().replace(" ", Constants.SPACE_REPLACER));
    	}
    return rule;
    }
	
	public static Rule revrtToSpaceIfAroundInFieldValues(Rule rule) {
    	for(Field field : rule.getCriteria()) {
    			if(field.getValue() == null) {
    				throw new S2GenericException("Input Field value is null for "+field.getName());
    			}
    		field.setValue(field.getValue().trim().replace(Constants.SPACE_REPLACER, " "));
    	}
    return rule;
    }
	
	public static Set<Field> replaceSpaceIfAroundInCriteriaFieldValues(Set<Field> criteria) {
    	for(Field field : criteria) {
    			if(field.getValue() == null) {
    				throw new S2GenericException("Input Field value is null for "+field.getName());
    			}
    		field.setValue(field.getValue().trim().replace(" ", Constants.SPACE_REPLACER));
    	}
    return criteria;
    }
	
	public static void isTenantIdPresent(Rule rule) throws S2GenericException {
		if(rule.getTenantId() == null || rule.getTenantId().trim().length() == 0) {
			throw new S2GenericException("Tenant Id not available in Rule object having name - "+rule.getRuleName());
		}
		
		for(Field field : rule.getCriteria()) {
			if(field.getTenantId() == null || field.getTenantId().trim().length() == 0) {
				throw new S2GenericException("Tenant Id not available in Criteria Field object having name - "+field.getName());
			}
		}
		
		if(rule.getFormula().getTenantId() == null || rule.getFormula().getTenantId().trim().length() == 0) {
			throw new S2GenericException("Tenant Id not available in Formula object having name - "+rule.getFormula().getFormulaName());
		}
		
		for(Field field : rule.getFormula().getFields()) {
			if(field.getTenantId() == null || field.getTenantId().trim().length() == 0) {
				throw new S2GenericException("Tenant Id not available in Formula Field object having name - "+field.getName());
			}
		}
	}
	
	public static Rule setAndReturnTenantId(Rule rule, String tenantId) {
		if(rule.getTenantId() == null || rule.getTenantId().trim().length() == 0) {
			rule.setTenantId(tenantId);
		}
		
		for(Field field : rule.getCriteria()) {
			if(field.getTenantId() == null || field.getTenantId().trim().length() == 0) {
				field.setTenantId(tenantId);
			}
		}
		
		if(rule.getFormula().getTenantId() == null || rule.getFormula().getTenantId().trim().length() == 0) {
			rule.getFormula().setTenantId(tenantId);
		}
		
		for(Field field : rule.getFormula().getFields()) {
			if(field.getTenantId() == null || field.getTenantId().trim().length() == 0) {
				field.setTenantId(tenantId);
			}
		}
		
		return rule;
	}

}
