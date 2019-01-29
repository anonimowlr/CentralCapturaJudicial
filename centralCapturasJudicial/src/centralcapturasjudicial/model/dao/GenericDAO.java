/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.AbstractEntity;
import centralcapturasjudicial.model.entity.cartao.CartaoCredito;
import centralcapturasjudicial.sisbb.CapturaExtratoCartaoCredito;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author f8940147
 * @param <T>
 */
public class GenericDAO<T extends AbstractEntity> {
    
    public EntityManager getEM() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
        return emf.createEntityManager();
    }

    public T salvar(T t) throws Exception {

        EntityManager em = getEM();


        try {
            em.getTransaction().begin();
            if (t.getId() == null) {
                em.merge(t);
                
            } else {
                if (em.contains(t)) {
                    if (em.find(t.getClass(), t.getId()) == null) {
                        throw new Exception("Erro ao atualizar!");
                    }
                }
                t = em.merge(t);
                
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
             Logger.getLogger(CapturaExtratoCartaoCredito.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return t;
    }

    public void remover(Class<T> clazz, Long id) {

        EntityManager em = getEM();
        T t =  em.find(clazz, id);
        

        try {
            em.getTransaction().begin();
            em.remove(t);
            em.getTransaction().commit();
        } catch (Exception ex) {
             Logger.getLogger(CapturaExtratoCartaoCredito.class.getName()).log(Level.SEVERE, null, ex);
        }     
        finally {
            em.close();
        }
    }
    
    
    

    public T consultarPorId(Class<T> clazz , Long id) {
        EntityManager em = getEM();
        T t = null;

        try {
            t = em.find(clazz, id);
        } finally {
            em.close();
        }
        return t;
    }
    
    public List<T> consultarTodos(Class<T> clazz) {
        EntityManager em = getEM();
            return em.createQuery("Select t from " + clazz.getSimpleName() + " t").getResultList();
    }            
}
