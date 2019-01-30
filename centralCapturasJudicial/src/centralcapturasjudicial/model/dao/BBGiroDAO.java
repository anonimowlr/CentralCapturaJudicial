/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.bbgiro.OperacaoGiro;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

    
    
     public void removeRegistroBBGiro(int op) throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
        EntityManager em = emf.createEntityManager();
        // try{
        //ExtratoCartao ec = em.find(ExtratoCartao.class, Long.parseLong(Integer.toString(op)));
        OperacaoGiro c = em.find(OperacaoGiro.class, Long.parseLong(Integer.toString(op)));
//            }
//        catch (Exception ex) {
//           throw new Exception(ex);
//        }

        try {
            em.getTransaction().begin();
            if (c == null) {
                return;
            }
            em.remove(c);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            em.close();
        }

    }
    
    
    
    
}
