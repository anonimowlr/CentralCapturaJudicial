/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import static antlr.build.ANTLR.root;
import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.BBGiroDAO;
import centralcapturasjudicial.model.entity.bbgiro.ExtratoSubcredito;
import centralcapturasjudicial.model.entity.bbgiro.OperacaoGiro;
import centralcapturasjudicial.sisbb.CapturaBBGiro;
import centralcapturasjudicial.sisbb.JanelaSisbb;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.annotations.common.util.StringHelper.root;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import static org.hibernate.internal.util.StringHelper.root;
import org.hibernate.service.ServiceRegistry;

/**
 * FXML Controller class
 *
 * @author f3295813
 */
public class BBGiroAnchorPaneController extends AbstractController implements Initializable {

    @FXML
    private JFXButton btnCapturaBBGiro;
    @FXML
    private JFXTextField inputOpBBGiro;
    @FXML
    private JFXTextField inputAgenciaBBGiro;

    private CapturaBBGiro captura;

    JanelaSisbb sisbb;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        captura = new CapturaBBGiro();
    }

    @FXML
    private void handleButtonCapturaBBGiro(ActionEvent event) throws Throwable {

        List<OperacaoGiro> op = new ArrayList();

        OperacaoGiro dadosGerais = new OperacaoGiro();
        BBGiroDAO dao = new BBGiroDAO();         //Thu Jun 25 00:00:00 BRT 2009
        SimpleDateFormat in = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat out = new SimpleDateFormat("ddMMyyyy");

      
        String operacao = inputOpBBGiro.getText();
        
        
        try {

            dao.deletarOperacao(operacao);

        } catch (Exception ex) {

            System.out.println(ex);

            throw new Exception(ex);
        }

        try {

            JanelaSisbb sisbb = new JanelaSisbb();

            dadosGerais = captura.capturaDadosGerais(sisbb, inputOpBBGiro, inputAgenciaBBGiro);

            Date data_proposta = dadosGerais.getDtProposta();

            String dataProposta = out.format(in.parse(data_proposta.toString()));

            dadosGerais = captura.extratoConsolidado(sisbb, dataProposta, dadosGerais);

            dadosGerais = captura.itensFinanciados(sisbb, dataProposta, dadosGerais);

            dadosGerais = captura.aberturaTeto(sisbb, dadosGerais);

            dao.save(dadosGerais);

            alertSucesso();
            sisbb.rotinaEncerramento();
            inputAgenciaBBGiro.clear();
            inputOpBBGiro.clear();

        } catch (Exception e) {

            System.out.println(e);

        }

    }

    private static List<OperacaoGiro> fetchOperacao(int ope) {
        List<OperacaoGiro> lista = new ArrayList();
        OperacaoGiro dadosGerais = new OperacaoGiro();
        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("calculobbreuTestesPU");
            EntityManager em = emf.createEntityManager();

            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<OperacaoGiro> query = builder.createQuery(OperacaoGiro.class);
            query.select(query.from(OperacaoGiro.class));

            Root<OperacaoGiro> i = query.from(OperacaoGiro.class);
            query.select(i).where(
                    builder.equal(i.get("nrOperacao"), ope)
            );

            lista = em.createQuery(query).getResultList();

        } catch (Exception e) {
            System.out.println(e);
        }

        return lista;

    }

    private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();

    }

    private void alertEroo() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Problema na captura...");
        alert.setContentText("Execução interrompida!");
        alert.showAndWait();

    }
}
