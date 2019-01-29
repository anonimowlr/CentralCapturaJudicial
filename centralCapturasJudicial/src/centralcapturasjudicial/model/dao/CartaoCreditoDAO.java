/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.model.dao;

import centralcapturasjudicial.model.entity.cartao.CartaoCredito;
import centralcapturasjudicial.model.entity.cartao.ExtratoCartao;
import centralcapturasjudicial.sisbb.CapturaExtratoCartaoCredito;
import static com.microsoft.schemas.office.excel.STTrueFalseBlank.T;
import java.io.Externalizable;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author F3161139
 */
public class CartaoCreditoDAO implements Serializable {

    public void removeRegistro(int op) throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
        EntityManager em = emf.createEntityManager();
        // try{
        //ExtratoCartao ec = em.find(ExtratoCartao.class, Long.parseLong(Integer.toString(op)));
        CartaoCredito c = em.find(CartaoCredito.class, op);
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

    public CartaoCredito save(CartaoCredito c) throws Exception {

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

}
