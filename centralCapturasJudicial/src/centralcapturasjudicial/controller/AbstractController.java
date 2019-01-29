/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.controller;

import centralcapturasjudicial.MainApp;

/**
 *
 * @author f8940147
 * 
 * Classe responsável por possibilitar o acesso ao Main App
 * Os controllers dos anchor panes de cada pesquisa herdam essa classe
 * 
 */
public abstract class AbstractController {
    
    protected MainApp mainApp; 
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
}
