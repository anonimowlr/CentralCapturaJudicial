/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.contacorrente.ContaCorrente;
import centralcapturasjudicial.model.entity.contacorrente.MesConta;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
/**
 *
 * @author f6323539
 */

        
public class ContaCorrenteDAO {
    
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
    EntityManager em = emf.createEntityManager();
    
    public void deletaLancamentosPeriodo(String agencia, String conta, int perInicial, int perFinal) throws Exception{  
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MesConta> query = cb.createQuery(MesConta.class); 
            Root fromTabMesCc = query.from(MesConta.class);
            Expression strDate = cb.function("STR_TO_DATE", Date.class, fromTabMesCc.get("dtPeriodoLancamento"), cb.literal("%m/%Y"));
            Expression dateFormat = cb.function("DATE_FORMAT", Date.class, strDate, cb.literal("%Y%m"));
            List<Predicate> condicoes = new ArrayList();
            condicoes.add(cb.equal(fromTabMesCc.get("contaCorrente").get("idConta").get("nrAgencia"), Short.parseShort(agencia)));
            condicoes.add(cb.equal(fromTabMesCc.get("contaCorrente").get("idConta").get("nrConta"), Long.parseLong(conta)));
            condicoes.add(cb.between(dateFormat, perInicial, perFinal)); 
            query.select(fromTabMesCc).where(condicoes.toArray(new Predicate[]{}));
            List<MesConta> listMes =  em.createQuery(query).getResultList();
            for (MesConta mes : listMes) {
                em.remove(mes); 
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            throw new Exception(ex);
        } 

    }  
    
     public void insertExtrato(ContaCorrente cc)  throws Exception {   
       try {
            em.getTransaction().begin();
            em.merge(cc);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
           throw new Exception(ex);
        } 
        finally {
            em.close();
        }
    }       
    
}
