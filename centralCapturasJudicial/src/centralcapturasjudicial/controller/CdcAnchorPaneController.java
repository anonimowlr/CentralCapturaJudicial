/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.sisbb.CapturaCdc;
import centralcapturasjudicial.sisbb.CapturaFin;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

/**
 * FXML Controller class
 *
 * @author f8940147
 */
public class CdcAnchorPaneController extends AbstractController implements Initializable {

    @FXML private JFXTextField inputAgencia;
    @FXML private JFXTextField inputConta;
    @FXML private JFXTextField inputOp;
    @FXML private JFXTextField inputCpf;
    @FXML private JFXTextField inputOperacao;
    private CapturaCdc captura;
    private CapturaFin capFin;
    private String string;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        captura = new CapturaCdc();
        capFin = new CapturaFin();
    }  
    
    @FXML
    private void handleButtonCDC() {

        String path = "view/CdcAnchorPaneCDC.fxml";                
        mainApp.showCenterAnchorPane(path, this);         
    }

    @FXML
    private void handleButtonFIN() {

        String path = "view/CdcAnchorPaneFIN.fxml";               
        mainApp.showCenterAnchorPane(path, this);     
    }

    
    
    @FXML
    private void handleButtonCapturaCdc() throws Throwable {
                        
        try {
            captura.capturaExtratos(inputAgencia, inputConta, inputOp);            
        } catch (RoboException ex) {
            Logger.getLogger(CdcAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    } 
    
    @FXML
    private void handleButtonCapturaCdcFin() throws Throwable {
                        
        try {
            capFin.capturaExtratos(inputAgencia, inputConta, inputOp, inputCpf, inputOperacao);
        } catch (RoboException ex) {
            Logger.getLogger(CdcAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    
    
    

}
