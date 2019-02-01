/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.bbgiro.OperacaoGiro;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author f3295813
 */
public class BBGiroDAO {
    
    
    
     public OperacaoGiro save(OperacaoGiro c) throws Exception {
         
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
        EntityManager em = emf.createEntityManager();

     

        try {
            em.getTransaction().begin();
            em.merge(c);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception ex) {

            throw new Exception(ex);
        } finally {
            em.close();
        }

        return c;

    }

    
    
    
public void deletarOperacao(String operacao) throws Exception{
    
       EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
        EntityManager em = emf.createEntityManager();
      
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        
        CriteriaQuery <OperacaoGiro> criteriaQuery = criteriaBuilder.createQuery(OperacaoGiro.class);
        Root <OperacaoGiro> root = criteriaQuery.from(OperacaoGiro.class);
        List <Predicate> p = new ArrayList <Predicate> ();
        if(operacao != null){
             p.add(criteriaBuilder.equal(root.get("nrOperacao"), operacao));
        }
        if(!p.isEmpty()){
            Predicate[] pr = new Predicate[p.size()];
            p.toArray(pr);
            criteriaQuery.where(pr);    
        }
        
        List<OperacaoGiro> listOpr = em.createQuery(criteriaQuery).getResultList();
        em.getTransaction().begin();
        for (OperacaoGiro oprs : listOpr) {
            em.remove(oprs);
        }
        em.getTransaction().commit();
    }

    
    
    
    
}
