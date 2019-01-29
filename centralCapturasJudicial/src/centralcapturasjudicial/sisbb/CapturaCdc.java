/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import centralcapturasjudicial.model.dao.ComponenteCdcDAO;
import centralcapturasjudicial.model.dao.OperacaoCdcDAO;
import centralcapturasjudicial.model.entity.cdc.ComponentePagamentoParcelaCdc;
import centralcapturasjudicial.model.entity.cdc.ContratoVinculadoCdc;
import centralcapturasjudicial.model.entity.cdc.OperacaoCdc;
import centralcapturasjudicial.model.entity.cdc.ParcelaOperacaoCdc;
import centralcapturasjudicial.model.entity.cdc.PagamentoParcelaCdc;
import centralcapturasjudicial.model.entity.cdc.TipoComponenteCdc;
import centralcapturasjudicial.poi.PlanilhaExtratoCdc;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author f8940147
 * 
 * Classe responsável por capturar extrato de conta corrente
 * 
 */
public class CapturaCdc {
    
    DateTimeFormatter fd = DateTimeFormatter.ofPattern("dd.MM.yy");   
    DateTimeFormatter fd1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");   
    SimpleDateFormat fd2 = new SimpleDateFormat("yyyy-MM-dd");
    DateTimeFormatter fd3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");   
    
    List <TipoComponenteCdc> listaCmpt = new ArrayList();
    
    public CapturaCdc() {
    }
//    Pega dados informados pelo usuário na tela inicial???
   public void capturaExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputOp) throws Throwable {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");

         if (!inputOp.getText().matches("[0-9]+")) {//retornando um valor true ou false
             System.out.println("Operação " +inputOp);
            alert.setHeaderText("Problema no número da operação...");
            alert.setContentText("Algum erro no número da operação, verifique!");
            alert.showAndWait();
         
         } else {
            try {
                String msg = geraExtratos(inputAgencia, inputConta, inputOp); 
                if( msg.equals("")) {
                    alertSucesso();
                }
                else {
                    alert.setHeaderText("Problema na captura...");
                    alert.setContentText(msg);
                    alert.showAndWait();
                }
               
            } catch (Exception e) {
                alert.setHeaderText("Problema na captura...");
                alert.setContentText("Execução interrompida!");
                alert.showAndWait();
            }
        }  
         
         //geraExtratos(inputAgencia, inputConta, inputOp);
    }
    
     private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();
    }
     
    private String geraExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputOp) throws PropertyVetoException, Throwable {    
       
        OperacaoCdcDAO daoOpe = new OperacaoCdcDAO(); 
        
        daoOpe.remover(OperacaoCdc.class, Long.parseLong(inputOp.getText()));
        
        // Carregar lista dos componentes de pagamento
        ComponenteCdcDAO daoCpnt = new ComponenteCdcDAO();
        
        listaCmpt = daoCpnt.consultarTodos(TipoComponenteCdc.class);
        if(listaCmpt.isEmpty()) {
            return "Lista de componentes não cadastrada na Base de Dados.";      
        }
        
        
        
        JanelaSisbb sisbb = new JanelaSisbb(); 
            
        sisbb.setTamanho(1000, 700);
        
        sisbb.Aplicativo(System.getProperty("user.name"), "CDC", true); //Verificar qual aplicativo o funcionário vai querer usar.
                                                                       
        
        sisbb.aguardarInd(1, 18, "SISBB");//Segunda tela do SISBB
        
        Thread.sleep(300);
        
        if(sisbb.copiar(5, 38, 7).equals("ATENÇÃO")) {  
            sisbb.teclarAguardarTroca("@3");
            Thread.sleep(300);
        }
        
        if(sisbb.copiar(3, 36, 10).equals("Comunicado")) {  
            sisbb.teclarAguardarTroca("@E");
            Thread.sleep(300);
        }

        sisbb.aguardarInd(1, 3, "CDCM0000");
        sisbb.colar(21, 20, "15");
        sisbb.teclarAguardarTroca("@E");
        
        Thread.sleep(300);
        
        if(sisbb.copiar(5, 38, 7).equals("ATENÇÃO")) {  
            sisbb.teclarAguardarTroca("@3");
        }
        
        sisbb.aguardarInd(1, 3, "CDCM1500");
        sisbb.colar(21, 20, "12");
        sisbb.teclarAguardarTroca("@E");
       
        sisbb.aguardarInd(2, 14, "Para");
        sisbb.colar(8, 38, inputOp.getText());
        sisbb.teclarAguardarTroca("@E");
        
        Thread.sleep(300);
        
        if(sisbb.copiar(4, 38, 7).equals("ATENÇÃO")) {  
           sisbb.teclarAguardarTroca("@E");
           Thread.sleep(300);
        }
        
        if(sisbb.copiar(23, 14, 6).equals("Nenhum")) {
           return "Operação não encontrada.";
        }
       
        sisbb.aguardarInd(1, 3, "CDCM");  
        
        OperacaoCdc opeCdc = new OperacaoCdc();  
        
        if(sisbb.copiar(1, 3, 8).equals("CDCM1520")) { // operação liquidada
            opeCdc.setNomeCliente(sisbb.copiar(5, 32, 50).trim());
            opeCdc.setId(Long.parseLong(sisbb.copiar(4, 20, 11).replace(".", "")));
            opeCdc.setTxSituacaoCdc(sisbb.copiar(6, 20, 50).trim().toUpperCase());
            opeCdc.setChaveFunci(System.getProperty("user.name")); //pega chave inserida no sistema
            
            
        }  
        else { // operação em ser
            opeCdc.setNomeCliente(sisbb.copiar(6, 31, 50).trim());
            opeCdc.setId(Long.parseLong(sisbb.copiar(4, 20, 9)));
            opeCdc.setId(Long.parseLong(sisbb.copiar(4, 20, 9)));
            opeCdc.setTxSituacaoCdc(sisbb.copiar(7, 20, 50).trim().toUpperCase());
            opeCdc.setChaveFunci(System.getProperty("user.name")); //pega chave inserida no sistema
            
            //Data e hora atual do sistema
            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            opeCdc.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));
        }
        
        sisbb.teclarAguardarTroca("@8");         
        sisbb.aguardarInd(4, 3, "Cliente");        
        int linha = 10;
        while(!sisbb.copiar(linha, 3, 2).equals("Ag")) {
            linha++;
        } 
        opeCdc.setNrAgenciaDebito(sisbb.copiar(linha, 22, 6).trim());
        if(!sisbb.copiar(linha + 1, 22, 20).trim().equals("")) {
           opeCdc.setNrContaDebito(sisbb.copiar(linha + 1, 22, 20).trim());
        }
        sisbb.teclarAguardarTroca("@7");
        
        sisbb.aguardarInd(4, 3, "Operacao");    
        if(sisbb.copiar(1, 3, 8).equals("CDCM1520")) { // operação liquidada
            sisbb.colar(19, 4, "X");
            sisbb.teclarAguardarTroca("@E");
            sisbb.aguardarInd(1, 3, "CDCM152A");
            if(sisbb.copiar(11, 3, 18).equals("Total financiado")) {
                linha = 11;
            }   
            else {
                linha = 12; 
            }   
            opeCdc.setVlTotalFinanciado(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setTxJurosMensal(Float.parseFloat(sisbb.copiar(linha, 65, 6).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setVlSolicitado(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setTxJurosAnual(Float.parseFloat(sisbb.copiar(linha, 65, 7).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setVlIof(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setDtUltimoIndice(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            if(!sisbb.copiar(linha, 24, 16).equals("")) {
                opeCdc.setVlJurosCarencia(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            }
            opeCdc.setDtBaseCarencia(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            opeCdc.setVlBaseCalculo(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setDtSaldoDevedor(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            opeCdc.setVlPrestacao(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setVlSaldoDevedor(Double.parseDouble(sisbb.copiar(linha, 65, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setQtParcela(Short.parseShort(sisbb.copiar(linha, 24, 16).trim()));
            if(!sisbb.copiar(linha, 65, 10).trim().equals("") && !sisbb.copiar(linha, 65, 1).equals("*")) {
                opeCdc.setDtParcelaSer(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            }
            linha++;
            opeCdc.setNrDiaParcela(Byte.parseByte(sisbb.copiar(linha, 24, 16).trim()));
            linha++;
            if(!sisbb.copiar(linha, 24, 10).trim().equals("")) {
               opeCdc.setDtPrimeiraParcela(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 24, 10), fd1).toString()));
            }   
            linha++;
            if(!sisbb.copiar(linha, 24, 10).trim().equals("")) {
                opeCdc.setDtUltimaParcela(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 24, 10), fd1).toString()));
            }
            sisbb.teclarAguardarTroca("@3");
            sisbb.aguardarInd(1, 3, "CDCM1520"); 
        }
        else {
            sisbb.colar(20, 4, "X");
            sisbb.teclarAguardarTroca("@E");
            sisbb.aguardarInd(1, 3, "CDCM3212");
            if(sisbb.copiar(11, 3, 18).equals("Total financiado")) {
                linha = 11;
            }   
            else {
                linha = 12; 
            }   
            opeCdc.setVlTotalFinanciado(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setTxJurosMensal(Float.parseFloat(sisbb.copiar(linha, 65, 6).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setVlSolicitado(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setTxJurosAnual(Float.parseFloat(sisbb.copiar(linha, 65, 7).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setVlIof(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            if(!sisbb.copiar(linha, 24, 16).equals("")) {
                opeCdc.setVlJurosCarencia(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            }
            opeCdc.setDtUltimoIndice(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            opeCdc.setVlBaseCalculo(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setDtBaseCarencia(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            opeCdc.setVlPrestacao(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setDtSaldoDevedor(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            linha++;
            opeCdc.setVlPrestacaoAmortizada(Double.parseDouble(sisbb.copiar(linha, 24, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            opeCdc.setVlSaldoDevedor(Double.parseDouble(sisbb.copiar(linha, 65, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            linha++;
            opeCdc.setQtParcela(Short.parseShort(sisbb.copiar(linha, 24, 16).trim()));
            if(!sisbb.copiar(linha, 65, 16).trim().equals("")) {
               opeCdc.setVlTotalJuros(Double.parseDouble(sisbb.copiar(linha, 65, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
            }
            linha++;
            opeCdc.setNrDiaParcela(Byte.parseByte(sisbb.copiar(linha, 24, 16).trim()));
            if(!sisbb.copiar(linha, 65, 10).equals("")) {
               opeCdc.setDtParcelaSer(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 65, 10), fd1).toString()));
            }
            linha++;
            if(!sisbb.copiar(linha, 24, 10).trim().equals("")) {
                opeCdc.setDtPrimeiraParcela(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 24, 10), fd1).toString()));
            }
            linha++;
            if(!sisbb.copiar(linha, 24, 10).trim().equals("")) {
                opeCdc.setDtUltimaParcela(fd2.parse(LocalDate.parse(sisbb.copiar(linha, 24, 10), fd1).toString()));
                opeCdc.setChaveFunci(System.getProperty("user.name")); //pega chave inserida no sistema
                //opeCdc.setHor_inc_func(Date.valueOf(LocalDate.now()));
            }
            sisbb.teclarAguardarTroca("@3");
            sisbb.aguardarInd(1, 3, "CDCM1510"); 
        }
        
        sisbb.teclarAguardarTroca("@2");
        //sisbb.aguardarInd(6, 40, "Outros");  
        
        int linOpcao = 0;
        if(sisbb.copiar(17, 34, 7).equals("Informe")) {
            linOpcao = 17;
        }
        
        else {
            linOpcao = 20;
            
        }
        sisbb.colar(linOpcao, 51, "5");    
        sisbb.teclarAguardarTroca("@E");
        
        // captura contratos vinculados
        List <ContratoVinculadoCdc> listaCtrVinc = new ArrayList();
        if(!sisbb.copiar(linOpcao+2, 34, 11).equals("Nao existem")) {
            sisbb.aguardarInd(1, 3, "CDC"); 
            while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
                for(int linCtr=11;linCtr<22;linCtr++) {   
                    if(sisbb.copiar(linCtr, 9, 12).equals("")) {
                        break;
                    }   
                    ContratoVinculadoCdc ctr = new  ContratoVinculadoCdc();
                    ctr.setOperacaoCdc(opeCdc);
                    ctr.setNrContrato(Integer.parseInt(sisbb.copiar(linCtr, 9, 12)));
                    ctr.setTxSistema(sisbb.copiar(linCtr, 23, 3));
                    ctr.setNrAgencia(Short.parseShort(sisbb.copiar(linCtr, 28, 4).trim()));
                    if(!sisbb.copiar(linCtr, 34, 10).trim().equals("")) {
                       ctr.setDtOperacao(fd2.parse(LocalDate.parse(sisbb.copiar(linCtr, 34, 10), fd1).toString()));      
                    }
                    if(!sisbb.copiar(linCtr, 47, 16).trim().equals("")) {
                       ctr.setVlSaldoDevedor(Double.parseDouble(sisbb.copiar(linCtr, 47, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                    }   
                    if(!sisbb.copiar(linCtr, 65, 16).trim().equals("")) {
                       ctr.setVlSaldoRenegociado(Double.parseDouble(sisbb.copiar(linCtr, 65, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                    }   
                    listaCtrVinc.add(ctr);
                }
                sisbb.teclarAguardarTroca("@8");
            }
            sisbb.teclarAguardarTroca("@3");
            //sisbb.aguardarInd(6, 40, "Outros");  
            
        }
        opeCdc.setListContratoVinculadoCdc(listaCtrVinc);
        
        // captura dados das parcelas da operação
        if(sisbb.copiar(17, 34, 7).equals("Informe")) {
            sisbb.colar(17, 51, "4");   
        }
        else {
            sisbb.colar(20, 51, "4");    
        }          
        sisbb.teclarAguardarTroca("@E");
        sisbb.aguardarInd(3, 30, "Cronograma");
        opeCdc.setCpfCliente(sisbb.copiar(5, 25, 14).trim());
  //-----------------------------------------------------------LISTA TabPclOpeCdc-------------------------------------------------------------------------------------------          
        List <ParcelaOperacaoCdc> listaParc = new ArrayList();   
        while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
            for(int linPcl=9;linPcl<21;linPcl++) {                
                if(!sisbb.copiar(linPcl, 7, 3).trim().equals("")) {  
                    ParcelaOperacaoCdc pcl = new ParcelaOperacaoCdc();
                    pcl.setNrParcela(Integer.parseInt(sisbb.copiar(linPcl, 7, 3).trim()));
                    pcl.setOperacaoCdc(opeCdc);
                    String tela = sisbb.copiar(1, 3, 8);
                    if(tela.equals("CDCM160B")) {
                        pcl.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(linPcl, 11, 10), fd).toString()));  
                        pcl.setVlCapital(Double.parseDouble(sisbb.copiar(linPcl, 20, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                        pcl.setVlJuros(Double.parseDouble(sisbb.copiar(linPcl, 33, 11).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                        if(!sisbb.copiar(linPcl, 71, 10).equals("")) {
                           pcl.setDtPagamento(fd2.parse(LocalDate.parse(sisbb.copiar(linPcl, 71, 10), fd).toString()));  
                        }  
                    }
                    else {
                        pcl.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(linPcl, 11, 10), fd1).toString()));      
                        pcl.setVlCapital(Double.parseDouble(sisbb.copiar(linPcl, 22, 11).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                        pcl.setVlJuros(Double.parseDouble(sisbb.copiar(linPcl, 34, 10).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                        if(!sisbb.copiar(linPcl, 71, 10).equals("")) {
                           pcl.setDtPagamento(fd2.parse(LocalDate.parse(sisbb.copiar(linPcl, 71, 10), fd1).toString()));  
                        }
                    }
                    sisbb.colar(linPcl, 4, "X");  
                    sisbb.teclarAguardarTroca("@E");
                    sisbb.aguardarInd(2, 39, "Detalhes");
                    pcl.setDtCobranca(fd2.parse(LocalDate.parse(sisbb.copiar(5, 65, 10), fd1).toString())); 
                    
                    // captura dados dos pagamentos da parcela  
                    List <PagamentoParcelaCdc> listaPgto = new ArrayList();//ENTRA AQUI??? OPERAÇÃO 869033519  e 875405127 :
                    sisbb.teclarAguardarTroca("@9");
                    if(sisbb.copiar(19, 24, 12).trim().equals("Tecla de fun")) { // parcela não paga
                        pcl.setListPagamentoParcelaCdc(listaPgto);
                        
                    }
                    if(sisbb.copiar(7, 24, 21).equals("Componente da parcela") && !sisbb.copiar(19, 24, 12).trim().equals("Tecla de fun")){ // parcela pagawhile(!sisbb.copiar(19, 25, 5).equals("ltimo")) {
                        while(!sisbb.copiar(19, 25, 5).equals("ltimo")) {
                            PagamentoParcelaCdc pgto = new PagamentoParcelaCdc(); // ERRO CORRIGIDO!!!
                          
                            pgto.setParcelaOperacaoCdc(pcl);
                            if(!sisbb.copiar(5, 65, 3).trim().equals("") && sisbb.copiar(5, 49, 15).trim().equals("Dt.Recebimento:")) {//data recebimento tela menor
                               
                                pgto.setNrRecebimento(Short.parseShort(sisbb.copiar(6, 65, 3).trim())); //Nr.Rec.Parcial - pega os dois digitos
                                pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(5, 65, 10), fd3).toString()));
                                
                            }                                                                   
                            if(!sisbb.copiar(4, 65, 3).trim().equals("") && sisbb.copiar(4, 49, 15).trim().equals("Dt.Recebimento:")){
                                
                                pgto.setNrRecebimento(Short.parseShort(sisbb.copiar(5, 68, 3).trim())); //Nr.Rec.Parcial - pega os dois digitos
                                pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(4, 65, 10), fd3).toString()));
                                
                            }
                            
                            List <ComponentePagamentoParcelaCdc> listaCpntPgto = new ArrayList();
                            
                            if(sisbb.copiar(8, 24, 10).equals("Componente")){
                                
                                  while(!sisbb.copiar(20, 25, 5).equals("ltima")) {
                                for(int linCpntRec=9;linCpntRec<19;linCpntRec++) {   
                                    if(sisbb.copiar(linCpntRec, 24, 5).equals("Total")) {
                                        break;
                                    }
                                    if(!sisbb.copiar(linCpntRec, 24, 3).equals("")) {
                                       ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                       TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();
                                       tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 32).trim());
                                       if(tipoCpntPgto != null) {
                                           cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                           cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 57, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                           cpntPgto.setPagamentoParcelaCdc(pgto);
                                           listaCpntPgto.add(cpntPgto);
                                       }
                                       else {
                                          return "Componente de pagamento da parcela " + pcl.getNrParcela()+ " não cadastrado.";
                                       }
                                    }
                                }
                                sisbb.teclarAguardarTroca("@8");
                            }
                                
                            }
                            
                              if(sisbb.copiar(7, 24, 10).equals("Componente")){
                                
                                  while(!sisbb.copiar(19, 25, 5).equals("ltima")) {
                                for(int linCpntRec=8;linCpntRec<19;linCpntRec++) {   
                                    if(sisbb.copiar(linCpntRec, 24, 5).equals("Total")) {
                                        break;
                                    }
                                    if(!sisbb.copiar(linCpntRec, 24, 3).equals("")) {
                                       ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                       TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();
                                       tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 32).trim());
                                       if(tipoCpntPgto != null) {
                                           cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                           cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 57, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                           cpntPgto.setPagamentoParcelaCdc(pgto);
                                           listaCpntPgto.add(cpntPgto);
                                       }
                                       else {
                                          return "Componente de pagamento da parcela " + pcl.getNrParcela()+ " não cadastrado.";
                                       }
                                    }
                                }
                                if(sisbb.copiar(19, 24, 34).trim().equals("Tecla de funcao F9 nao disponivel.")){
                                break;
                            }
                                sisbb.teclarAguardarTroca("@8");
                            }
                                
                            }
                            
                            
                          
                            pgto.setListComponentePagamentoParcelaCdc(listaCpntPgto);
                            listaPgto.add(pgto);
                            sisbb.teclarAguardarTroca("@12");
                            if(sisbb.copiar(19, 24, 12).trim().equals("Tecla de fun")){
                                break;
                            }
                            
                        }
                        sisbb.teclarAguardarTroca("@3");
                        sisbb.aguardarInd(2, 39, "Detalhes");
                    }
                    
                    if(sisbb.copiar(8, 24, 21).equals("Componente da parcela") && !sisbb.copiar(20, 24, 12).trim().equals("Tecla de fun")){ // parcela pagawhile(!sisbb.copiar(19, 25, 5).equals("ltimo")) {
                        while(!sisbb.copiar(20, 25, 5).equals("ltimo")) {
                            PagamentoParcelaCdc pgto = new PagamentoParcelaCdc(); // ERRO CORRIGIDO!!!
                            pgto.setParcelaOperacaoCdc(pcl);
                            if(!sisbb.copiar(5, 65, 3).trim().equals("") && sisbb.copiar(5, 49, 15).trim().equals("Dt.Recebimento:")) {//data recebimento tela menor
                               
                                pgto.setNrRecebimento(Short.parseShort(sisbb.copiar(6, 65, 3).trim())); //Nr.Rec.Parcial - pega os dois digitos
                                pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(5, 65, 10), fd3).toString()));
                                
                            }                                                                   
                            if(!sisbb.copiar(4, 65, 3).trim().equals("") && sisbb.copiar(4, 49, 15).trim().equals("Dt.Recebimento:")){
                                
                                pgto.setNrRecebimento(Short.parseShort(sisbb.copiar(5, 68, 3).trim())); //Nr.Rec.Parcial - pega os dois digitos
                                pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(4, 65, 10), fd3).toString()));
                                
                            }
                            
                            List <ComponentePagamentoParcelaCdc> listaCpntPgto = new ArrayList();
                            
                            if(sisbb.copiar(8, 24, 10).equals("Componente")){
                                
                                  while(!sisbb.copiar(20, 25, 5).equals("ltima")) {
                                for(int linCpntRec=9;linCpntRec<19;linCpntRec++) {   
                                    if(sisbb.copiar(linCpntRec, 24, 5).equals("Total")) {
                                        break;
                                    }
                                    if(!sisbb.copiar(linCpntRec, 24, 3).equals("")) {
                                       ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                       TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();
                                       tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 32).trim());
                                       if(tipoCpntPgto != null) {
                                           cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                           cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 57, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                           cpntPgto.setPagamentoParcelaCdc(pgto);
                                           listaCpntPgto.add(cpntPgto);
                                       }
                                       else {
                                          return "Componente de pagamento da parcela " + pcl.getNrParcela()+ " não cadastrado.";
                                       }
                                    }
                                }
                                sisbb.teclarAguardarTroca("@8");
                            }
                                
                            }
                            
                              if(sisbb.copiar(7, 24, 10).equals("Componente")){
                                
                                  while(!sisbb.copiar(19, 25, 5).equals("ltima")) {
                                for(int linCpntRec=8;linCpntRec<19;linCpntRec++) {   
                                    if(sisbb.copiar(linCpntRec, 24, 5).equals("Total")) {
                                        break;
                                    }
                                    if(!sisbb.copiar(linCpntRec, 24, 3).equals("")) {
                                       ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                       TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();
                                       tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 32).trim());
                                       if(tipoCpntPgto != null) {
                                           cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                           cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 57, 16).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                           cpntPgto.setPagamentoParcelaCdc(pgto);
                                           listaCpntPgto.add(cpntPgto);
                                       }
                                       else {
                                          return "Componente de pagamento da parcela " + pcl.getNrParcela()+ " não cadastrado.";
                                       }
                                    }
                                }
                                sisbb.teclarAguardarTroca("@8");
                            }
                                
                            }
                            
                            
                          
                            pgto.setListComponentePagamentoParcelaCdc(listaCpntPgto);
                            listaPgto.add(pgto);
                            sisbb.teclarAguardarTroca("@12");
                        }
                        sisbb.teclarAguardarTroca("@3");
                        sisbb.aguardarInd(2, 39, "Detalhes");
                    }
                    
                    pcl.setListPagamentoParcelaCdc(listaPgto);
                    listaParc.add(pcl);   
                    sisbb.teclarAguardarTroca("@3");
                }
            }
            sisbb.teclarAguardarTroca("@8");
        }
        sisbb.teclarAguardarTroca("@5");
        sisbb.rotinaEncerramento();
        
        if(listaParc.size() > 0) {  
            opeCdc.setListParcelaOperacaoCdc(listaParc);
            daoOpe.salvar(opeCdc);
            PlanilhaExtratoCdc planilha = new PlanilhaExtratoCdc();
            planilha.criaPlanilha(opeCdc);   
            return "";
        }    
        else {
           return "Nenhum lançamento encontrado. Planilha não gerada.";    
        }
    }   
    
    public TipoComponenteCdc buscaComponenteLista(String texto){
        TipoComponenteCdc tipo = null; 
        
        for(TipoComponenteCdc c : listaCmpt) {
            if(c.getNome().equals(texto)){
                return c;
            }    
        }    
        return tipo;
    }
    
    public void carregaListaComponentesPgto(){
        
        ComponenteCdcDAO daoCpnt = new ComponenteCdcDAO();
        
        listaCmpt.clear();
        listaCmpt = daoCpnt.consultarTodos(TipoComponenteCdc.class);
    }
}