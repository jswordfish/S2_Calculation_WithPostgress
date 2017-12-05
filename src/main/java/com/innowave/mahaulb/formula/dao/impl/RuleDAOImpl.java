package com.innowave.mahaulb.formula.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.EntityContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.innowave.mahaulb.formula.common.Constants;
import com.innowave.mahaulb.formula.common.S2GenericException;
import com.innowave.mahaulb.formula.dao.RuleDAO;
import com.innowave.mahaulb.formula.domain.Field;
import com.innowave.mahaulb.formula.domain.Rule;

@Repository("ruleDAO")
public class RuleDAOImpl extends JpaDAOImpl<Long, Rule> implements RuleDAO{
	@Autowired
    EntityManagerFactory entityManagerFactory;
	
	@PersistenceContext(unitName="V2CRM_PersistenceUnit")
	private EntityManager entityManager;
	
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
		super.setEntityManager(entityManager);
		}
    
    @PostConstruct
    public void init() {
        super.setEntityManagerFactory(entityManagerFactory);
        super.setEntityManager(entityManager);
    }

    

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Rule getUniqueRuleByFields(Set<Field> criteria) {
		// TODO Auto-generated method stub
		FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(getEntityManager());
	//	QueryBuilder builder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(Rule.class).get();
		EntityContext context = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(Rule.class);
//		for(Field f : criteria) {
//			context.overridesForField(f.getName(), "WhitespaceAnalyzer");
//		}
		QueryBuilder builder = context.get();
		
		BooleanJunction bool = builder.bool();
			for(Field field : criteria){
				org.apache.lucene.search.Query q = builder.keyword().onField(field.getName()).ignoreFieldBridge().matching(field.getValue()).createQuery();
				bool.must(q);
			}
			org.apache.lucene.search.Query q = bool.createQuery();
		System.out.println(q.toString());
		List<Rule> rules = fullTextEm.createFullTextQuery(q, Rule.class).getResultList();
			if(rules.size() > 1){
				throw new S2GenericException("TOO_MANY_RESULTS");
			}
			else if(rules.size() == 1){
				return rules.get(0);
			}
			
		return null;
	}

	@Override
	public Rule getUniqueRuleByFieldsAndTenant(Set<Field> criteria, String tenant) {
		// TODO Auto-generated method stub
				FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(getEntityManager());
			//	QueryBuilder builder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(Rule.class).get();
				EntityContext context = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(Rule.class);
//				for(Field f : criteria) {
//					context.overridesForField(f.getName(), "WhitespaceAnalyzer");
//				}
				QueryBuilder builder = context.get();
				
				BooleanJunction bool = builder.bool();
					for(Field field : criteria){
						org.apache.lucene.search.Query q = builder.keyword().onField(field.getName()).ignoreFieldBridge().matching(field.getValue()).createQuery();
						bool.must(q);
					}
					
					org.apache.lucene.search.Query qT = builder.keyword().onField("tenantId").ignoreFieldBridge().matching(tenant).createQuery();
					bool.must(qT);
					org.apache.lucene.search.Query q = bool.createQuery();
				System.out.println(q.toString());
				List<Rule> rules = fullTextEm.createFullTextQuery(q, Rule.class).getResultList();
					if(rules.size() > 1){
						throw new S2GenericException("TOO_MANY_RESULTS");
					}
					else if(rules.size() == 1){
						Rule r = rules.get(0);
						r.getCriteria().size();
						r.getFormula().getFields().size();
						return r;
					}
					
				return null;
	}

	@Override
	public Rule getUniqueRuleByTenant(String ruleName, String tenant) {
		// TODO Auto-generated method stub
		Query query = getEntityManager().createQuery("SELECT r FROM Rule r WHERE r.ruleName=:ruleName ");
		query.setParameter("ruleName", ruleName);
		//query.setParameter("tenantId", tenant);
		List<Rule> rules = query.getResultList();
		int count = 0;
			if(rules.size() > 0) {
				for(Rule rule : rules) {
					if(rule.getTenantId().equals(tenant)) {
						count ++;
						if(count > 1) {
							throw new S2GenericException("TOO_MANY_RESULTS");
						}
					}
				}
				
				if(count == 0) {
					//Same rule anme exists but for a different client
					//so its safe to create it with same name for this tenant
					return null;
				}
				else {
					return rules.get(0);
				}
			}
			
			if(rules.size() == 0) {
				return null;
			}
			
			return rules.get(0);
	}
	
	
	
	
}