/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.leasing.OperacaoLeasing;
import com.jfoenix.controls.JFXTextField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author f3295813
 */
public class LeasingDAO {
    
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
    EntityManager em = emf.createEntityManager();
    
    
    public OperacaoLeasing save(OperacaoLeasing c) throws Exception {

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

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery <OperacaoLeasing> criteriaQuery = criteriaBuilder.createQuery(OperacaoLeasing.class);

        Root <OperacaoLeasing> root = criteriaQuery.from(OperacaoLeasing.class);

        List <Predicate> p = new ArrayList <Predicate> ();

        if(operacao != null){
             p.add(criteriaBuilder.equal(root.get("nrContrato"), operacao));
        }

        if(!p.isEmpty()){
            Predicate[] pr = new Predicate[p.size()];
            p.toArray(pr);
            criteriaQuery.where(pr);    
        }
        
        List<OperacaoLeasing> listOpr = em.createQuery(criteriaQuery).getResultList();
        em.getTransaction().begin();
        for (OperacaoLeasing oprs : listOpr) {
            em.remove(oprs); 
        }
        em.getTransaction().commit();
    }
    
}
