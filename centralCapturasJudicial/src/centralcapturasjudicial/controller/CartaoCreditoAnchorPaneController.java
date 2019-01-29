/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.entity.cartao.ExtratoCartao;
import centralcapturasjudicial.poi.PlanilhaExtratoCartao;
import centralcapturasjudicial.sisbb.CapturaExtratoCartaoCredito;
import centralcapturasjudicial.util.TextFieldFormatter;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author f8940147
 */
public class CartaoCreditoAnchorPaneController extends AbstractController implements Initializable {

    @FXML
    private JFXTextField inputAgencia;
    @FXML
    private JFXTextField inputConta;
    @FXML
    private JFXTextField inputCpf;
    @FXML
    private JFXTextField inputCnpj;
    @FXML
    private JFXTextField inputDataInicial;
    @FXML
    private JFXTextField inputDataFinal;
    @FXML
    private ListView listViewCartoesPF;
    @FXML
    private ListView listViewCartoesPJ;
    
    private CapturaExtratoCartaoCredito captura;
    //private MainApp mainApp;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            captura = new CapturaExtratoCartaoCredito();
        } catch (PropertyVetoException ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonCartaoPF() {

        String path = "view/CartaoCreditoPFAnchorPane.fxml";                
        mainApp.showCenterAnchorPane(path, this);         
    }

    @FXML
    private void handleButtonCartaoPJ() {

        String path = "view/CartaoCreditoPJAnchorPane.fxml";               
        mainApp.showCenterAnchorPane(path, this);     
    }

    @FXML
    private void handleButtonCapturarCartoesPF() throws PropertyVetoException {

        try {            
            captura.validaCapturaCartao(inputAgencia, inputConta, inputCpf, listViewCartoesPF);
        } catch (Throwable ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleButtonCapturarExtratosPF() throws IOException, Exception {

        try {
              captura.capturaFaturas(listViewCartoesPF);
//            PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
//            planilha.criaPlanilha(fatura);
        } catch (RoboException ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void handleButtonCapturarCartoesPJ() throws PropertyVetoException {

        try {            
            captura.capturaListaCartoesPJAntigos(inputAgencia, inputConta, inputCnpj, listViewCartoesPJ);
        } catch (Throwable ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleButtonCapturarExtratosPJAntigos() throws IOException, Exception {

        try {
            ExtratoCartao fatura = captura.capturaFaturaPJAntigos(listViewCartoesPJ);
//            PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
//            planilha.criaPlanilha(fatura);
        } catch (RoboException ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @FXML
    private void inputAgenciaKeyReleased() {

        TextFieldFormatter tff = new TextFieldFormatter();
        tff.setMask("####");
        tff.setCaracteresValidos("0123456789");
        tff.setTf(inputAgencia);
        tff.formatter();

    }

}
