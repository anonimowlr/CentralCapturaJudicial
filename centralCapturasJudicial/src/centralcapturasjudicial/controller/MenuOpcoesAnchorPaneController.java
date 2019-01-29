/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import centralcapturasjudicial.MainApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


/**
 * FXML Controller class
 *
 * @author f8940147
 */
public class MenuOpcoesAnchorPaneController {

    // Reference to the main application.
    private MainApp mainApp;       

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    @FXML
    private void initialize() {
    }
    
    @FXML    
    private void handleButtonContaCorrente(){
        String path = "view/ContaCorrenteAnchorPane.fxml";
        AbstractController controller = new ContaCorrenteAnchorPaneController();
        mainApp.showCenterAnchorPane(path, controller);
    }
    
    @FXML
    private void handleButtonCartaoCredito(){
        String path = "view/CartaoCreditoAnchorPane.fxml";
        AbstractController controller = new CartaoCreditoAnchorPaneController();
        mainApp.showCenterAnchorPane(path, controller);
    }
    
    @FXML
    private void handleButtonCdc(){
        String path = "view/CdcAnchorPane.fxml";
        AbstractController controller = new CartaoCreditoAnchorPaneController();
        mainApp.showCenterAnchorPane(path, controller);
    }
    
    @FXML
    private void handleButtonBBGiro(){
        
        String path = "view/BBGiroAnchorPane.fxml";
        AbstractController controller = new BBGiroAnchorPaneController();
        mainApp.showCenterAnchorPane(path, controller);
        
    }
    
    @FXML
    private void handleButtonCedulasRurais(){
        
    }
    
    @FXML
    private void handleButtonLeasing(){
        
    }
    
    @FXML
    private void handleButtonDescontoCheques(){
        
    }
    
    @FXML
    private void handleButtonDescontoTitulos(){
        
    }
    
    @FXML
    private void handleButtonCPR(){
        
    }
}
