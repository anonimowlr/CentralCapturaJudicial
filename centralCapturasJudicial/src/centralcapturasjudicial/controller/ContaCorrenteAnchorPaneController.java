/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.sisbb.CapturaExtratoContaCorrente;
import centralcapturasjudicial.util.TextFieldFormatter;
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
public class ContaCorrenteAnchorPaneController extends AbstractController implements Initializable {

    @FXML private JFXTextField inputAgencia;
    @FXML private JFXTextField inputConta;
    @FXML private JFXTextField inputDataInicial;
    @FXML private JFXTextField inputDataFinal;
    private CapturaExtratoContaCorrente captura;
    private String string;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            captura = new CapturaExtratoContaCorrente();
        } catch (PropertyVetoException ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    
    @FXML
    private void handleButtonCapturaCc() throws Throwable {
                        
        try {
            captura.capturaExtratos(inputAgencia, inputConta, inputDataInicial, inputDataFinal);            
        } catch (RoboException ex) {
            Logger.getLogger(CartaoCreditoAnchorPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    @FXML
    private void inputDataInicialKeyReleased() throws ParseException{
       
        TextFieldFormatter tff = new TextFieldFormatter();
        tff.setMask("##/####");
        tff.setCaracteresValidos("0123456789");
        tff.setTf(inputDataInicial);
        tff.formatter();      
       
    }
    
    @FXML
    private void inputDataFinalKeyReleased(){
        
        TextFieldFormatter tff = new TextFieldFormatter();
        tff.setMask("##/####");
        tff.setCaracteresValidos("0123456789");
        tff.setTf(inputDataFinal);
        tff.formatter();        
    }
    
}
