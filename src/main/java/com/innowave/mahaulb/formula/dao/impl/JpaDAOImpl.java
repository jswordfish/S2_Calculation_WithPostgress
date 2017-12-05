package com.innowave.mahaulb.formula.dao.impl;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.innowave.mahaulb.formula.dao.JPADAO;
@SuppressWarnings("deprecation")
public class JpaDAOImpl <K, E> implements JPADAO<E, K>{ 
    protected Class<E> entityClass; 
    
    EntityManagerFactory entityManagerFactory;
    
    EntityManager entityManager;

    public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@SuppressWarnings("unchecked") 
    public JpaDAOImpl() { 
        ParameterizedType genericSuperclass = (ParameterizedType) getClass() 
                .getGenericSuperclass(); 
        this.entityClass = (Class<E>) genericSuperclass 
                .getActualTypeArguments()[1]; 
    } 

    public void persist(E entity) { 
       // getJpaTemplate().persist(entity); 
    	getEntityManager().persist(entity);
    	getEntityManager().flush();
    } 

    public void remove(E entity) { 
       // getJpaTemplate().remove(entity); 
    	getEntityManager().remove(entity);
    	getEntityManager().flush();
    	
    } 
    
    public E merge(E entity) { 
      //  return getJpaTemplate().merge(entity); 
    	E e = getEntityManager().merge(entity);
    	getEntityManager().flush();
    	return e;
    } 
    
    public void refresh(E entity) { 
       // getJpaTemplate().refresh(entity); 
    	getEntityManager().refresh(entity);
    	getEntityManager().flush();
    } 

    public E findById(K id) { 
       // return getJpaTemplate().find(entityClass, id); 
    	return getEntityManager().find(entityClass, id);
    } 
    
    public E flush(E entity) { 
       // getJpaTemplate().flush(); 
    	getEntityManager().flush();
        return entity; 
    } 
    
    @SuppressWarnings("unchecked") 
    public List<E> findAll() { 
      //  Object res = getJpaTemplate().execute(new JpaCallback() { 
//    	Object res = getEntityManager().equals(new JpaCallback() { 
//            public Object doInJpa(EntityManager em) throws PersistenceException { 
//            	try{
//                Query q = em.createQuery("SELECT h FROM " + 
//                        entityClass.getName() + " h"); 
//                return q.getResultList(); 
//            	}
//            	catch(PersistenceException e){
//            		e.printStackTrace();
//            		throw e;
//            	}
//            } 
//            
//        }); 
    	return getEntityManager().createQuery("SELECT h FROM "+entityClass.getName() + " h").getResultList();
        
       // return (List<E>) res; 
    }
    
    @SuppressWarnings("unchecked") 
    public List<E> find(final int startFrom, final int maxResults) { 
        /*Object res = getEntityManager().equals((new JpaCallback() { 

            public Object doInJpa(EntityManager em) throws PersistenceException { 
            	try{
                Query q = em.createQuery("SELECT h FROM " + 
                        entityClass.getName() + " h");
                q.setMaxResults(maxResults);
                q.setFirstResult(startFrom);
                return q.getResultList(); 
            	}
            	catch(PersistenceException e){
            		e.printStackTrace();
            		throw e;
            	}
            } 
            
        })); */
        
        Query q = getEntityManager().createQuery("SELECT h FROM " + 
                entityClass.getName() + " h ORDER BY h.id DESC");
        q.setMaxResults(maxResults);
        q.setFirstResult(startFrom);        
        return (List<E>) q.getResultList(); 
    }

    @SuppressWarnings("unchecked") 
    public Integer removeAll() { 
        //return (Integer) getJpaTemplate().execute(new JpaCallback() { 
//    	return (Integer) getEntityManager().execute(new JpaCallback() { 
//            public Object doInJpa(EntityManager em) throws PersistenceException { 
//                Query q = em.createQuery("DELETE FROM " + 
//                        entityClass.getName() + " h"); 
//                return q.executeUpdate(); 
//            } 
//            
//        }); 
    	return 0;
    }
    
   
    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params the query parameters
     *
     * @return the list of entities
     */
    public List<E> findByNamedQueryAndNamedParams(
        final String name,
        final Map<String, ?extends Object> params
    ){
    	javax.persistence.Query query = getEntityManager().createNamedQuery(
				name);

		for (final Map.Entry<String, ? extends Object> param : params
				.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}

		final List<E> result = (List<E>) query.getResultList();
		return result;
    }
    
    /**
     * Find using a named query.
     *
     * @param name the name
     * @param params the query parameters
     * @param startFrom the start from
     * @param maxResults the max results
     * @return the list of entities
     */
    public List<E> findByNamedQueryAndNamedParams(
        final String name,
        final Map<String, ?extends Object> params,
        int startFrom,int maxResults
    ){
    	javax.persistence.Query query = getEntityManager().createNamedQuery(
				name);

		for (final Map.Entry<String, ? extends Object> param : params
				.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}
		query.setMaxResults(maxResults);
		query.setFirstResult(startFrom); 

		final List<E> result = (List<E>) query.getResultList();
		return result;
    }
    
    public int recordCount(final String name,final Map<String, ?extends Object> params)
    {
    	javax.persistence.Query query = getEntityManager().createNamedQuery(name);	
		for (final Map.Entry<String, ? extends Object> param : params
				.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}
    	return ((Long)query.getSingleResult()).intValue();	
    }
}
