/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.BBGiroDAO;
import centralcapturasjudicial.model.entity.bbgiro.OperacaoGiro;
import centralcapturasjudicial.sisbb.CapturaBBGiro;
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
        
        OperacaoGiro dadosGerais = new OperacaoGiro();
        BBGiroDAO dao = new BBGiroDAO();         //Thu Jun 25 00:00:00 BRT 2009
        SimpleDateFormat in= new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat out = new SimpleDateFormat("ddMMyyyy");
        
        
       String op = inputOpBBGiro.getText();
 
   try {
           dao.removeRegistroBBGiro(Integer.parseInt(op));
        } catch (Exception ex) {
            System.out.println(ex);
            throw new Exception(ex);
        }
         
        
         
       
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");

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
