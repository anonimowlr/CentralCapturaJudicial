/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.LeasingDAO;
import centralcapturasjudicial.model.entity.leasing.*;
import centralcapturasjudicial.poi.PlanilhaExtratoLeasing;
import centralcapturasjudicial.sisbb.CapturaLeasing;
import centralcapturasjudicial.sisbb.JanelaSisbb;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

/**
 * FXML Controller class
 *
 * @author f3295813
 */
public class LeasingAnchorPaneController extends AbstractController implements Initializable {

    @FXML
    private JFXButton btnCapturaLeasing;
    @FXML
    private JFXTextField inputOperacao;

    private CapturaLeasing captura;

    
    JanelaSisbb sisbb;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        captura = new CapturaLeasing();
    }

    @FXML
    private void handleButtonCapturaLeasing(ActionEvent event) throws Throwable {
        
        OperacaoLeasing oprLeasing = new OperacaoLeasing();  

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");

        try {
            String operacao = inputOperacao.getText();
            LeasingDAO dao = new LeasingDAO();
            dao.deletarOperacao(operacao);      
            
            JanelaSisbb sisbb = new JanelaSisbb();
            sisbb.setTamanho(1000, 700);

            oprLeasing = captura.CapturaLeasing(sisbb, inputOperacao);
            oprLeasing = captura.CapturaLancamentosLeasing(sisbb, oprLeasing);

            dao.save(oprLeasing);
            
            if (oprLeasing.getListLancamentoLeasing().size() > 0) {
                PlanilhaExtratoLeasing planilha = new PlanilhaExtratoLeasing();
                planilha.criaPlanilha(oprLeasing);
                
                alertSucesso();                
            }

            sisbb.rotinaEncerramento();
            inputOperacao.clear();

        } catch (Exception e) {
            
            System.out.println(e);

            alert.setHeaderText("Problema na captura...");
            alert.setContentText("Execução interrompida!");
            alert.showAndWait();
        }

    }

    private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();
        
    }
    
    
}
