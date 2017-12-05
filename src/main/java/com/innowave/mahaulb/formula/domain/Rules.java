package com.innowave.mahaulb.formula.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
@Entity
public class Rules extends Base{
String tenantId;
@OneToMany
Set<Rule> rule = new HashSet<Rule>();

public String getTenantId() {
	return tenantId;
}

public void setTenantId(String tenantId) {
	this.tenantId = tenantId;
}

public Set<Rule> getRule() {
	return rule;
}

public void setRules(Set<Rule> rule) {
	this.rule = rule;
}




}
