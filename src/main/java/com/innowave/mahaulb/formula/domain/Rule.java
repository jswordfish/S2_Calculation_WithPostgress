package com.innowave.mahaulb.formula.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.innowave.mahaulb.formula.lucene.bridges.CustomBridge;
@Entity
@NamedQueries({
	@NamedQuery(name="Rule.getUniqueRule", 
			query="SELECT r FROM Rule r WHERE r.ruleName=:ruleName")
})
@Indexed
public class Rule extends Base {
	
	
	@OneToMany(cascade=CascadeType.ALL)
	@FieldBridge(impl = CustomBridge.class )
	@org.hibernate.search.annotations.Field(analyze = Analyze.YES, store=Store.YES)
	List<Field> criterias = new ArrayList<>();
	
	@org.hibernate.search.annotations.Field(index=org.hibernate.search.annotations.Index.YES, analyze=Analyze.NO, store=Store.YES)
	String ruleName;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	Formula formula = new Formula();
	
	

	public List<Field> getCriteria() {
		return criterias;
	}

	public void setCriteria(List<Field> criteria) {
		this.criterias = criteria;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	
	
	@Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Rule)) return false;
        return this.ruleName.equals(((Rule) obj).ruleName);
    }

    @Override
    public int hashCode() {        
        return this.ruleName.hashCode();
    }
    
	public String getTenantId() {
		return super.tenantId;
	}

}
