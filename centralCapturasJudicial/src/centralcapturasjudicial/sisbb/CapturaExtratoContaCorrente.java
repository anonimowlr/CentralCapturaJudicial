/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;
import centralcapturasjudicial.model.dao.ContaCorrenteDAO;
import centralcapturasjudicial.model.entity.contacorrente.ContaCorrente;
import centralcapturasjudicial.model.entity.contacorrente.IdConta;
import centralcapturasjudicial.model.entity.contacorrente.MesConta;
import centralcapturasjudicial.poi.PlanilhaExtratoContaCorrente;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import static java.lang.Integer.parseInt;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

/**
 *
 * @author f8940147
 * 
 * Classe responsável por capturar extrato de conta corrente
 * 
 */
public class CapturaExtratoContaCorrente {
    
    DateTimeFormatter fd = DateTimeFormatter.ofPattern("dd.MM.yyyy");        
    SimpleDateFormat fd2 = new SimpleDateFormat("yyyy-MM-dd");
    
    public CapturaExtratoContaCorrente() throws PropertyVetoException {
    }
    
    public void capturaExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputDataInicial, JFXTextField inputDataFinal) throws Throwable  {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        if (!inputAgencia.getText().matches("[0-9]+") || inputAgencia.getText().length() > 4 || inputAgencia.getText().length() == 0) {
            alert.setHeaderText("Problema no número da agência...");
            alert.setContentText("Algum erro no número da agência, verifique!");
            alert.showAndWait();
        } else if (!inputConta.getText().matches("[0-9]+") || inputConta.getText().length() > 12 || inputConta.getText().length() == 0) {
            alert.setHeaderText("Problema no número da conta...");
            alert.setContentText("Algum erro no número da conta, verifique!");
            alert.showAndWait();
         } else {
            try {
                String retornoMsg = geraExtratos(inputAgencia, inputConta, inputDataInicial, inputDataFinal);
                if(retornoMsg.equals("")) {
                    alertSucesso();
                }
                else {
                    alert.setHeaderText("Atenção!");
                    alert.setContentText(retornoMsg);
                    alert.showAndWait();
                }
               
            } catch (Exception e) {
                alert.setHeaderText("Problema de execução...");
                alert.setContentText("Execução interrompida!");
                alert.showAndWait();
            }
        }  
    }
    
     private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();
    }
    
    private String geraExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputDataInicial, JFXTextField inputDataFinal) throws PropertyVetoException, Throwable {    
                
        int dataInicialInv = parseInt(inputDataInicial.getText().substring(3, 7) + inputDataInicial.getText().substring(0, 2));
        int dataFinalInv = parseInt(inputDataFinal.getText().substring(3, 7) + inputDataFinal.getText().substring(0, 2));
       
        ContaCorrenteDAO dao = new ContaCorrenteDAO();
        
        dao.deletaLancamentosPeriodo(inputAgencia.getText(), inputConta.getText(), dataInicialInv, dataFinalInv);
        
        ExtratoContaCorrente extrato = new ExtratoContaCorrente();
        ContaCorrente cc = extrato.CapturaLancamentos(inputAgencia.getText(), inputConta.getText(), dataInicialInv, dataFinalInv);
        
        if(cc.getListMesConta().size() > 0) {  
            try {
               dao.insertExtrato(cc);
            } catch (Exception ex) {
                return "Erro na atualização dos dados. Planilha não gerada.";    
            }   
            PlanilhaExtratoContaCorrente planilha = new PlanilhaExtratoContaCorrente();
            planilha.criaPlanilha(cc, inputDataInicial.getText(), inputDataFinal.getText());   
            return "";
        }    
        else {
            return "Nenhum lançamento encontrado. Planilha não gerada.";    
        }
    }
}
