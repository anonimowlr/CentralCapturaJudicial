/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.entity.contacorrente.ContaCorrente;
import centralcapturasjudicial.model.entity.contacorrente.IdConta;
import centralcapturasjudicial.model.entity.contacorrente.LancamentoConta;
import centralcapturasjudicial.model.entity.contacorrente.MesConta;
import java.beans.PropertyVetoException;
import static java.lang.Integer.parseInt;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author f6323539
 */
public class ExtratoContaCorrente {
    
    String agencia;
    String conta;
    String nomeTitular;
    String cpfTitular;
    int dataInicialInv;
    int dataFinalInv;
    Date dataAbertura;
  
    ArrayList<String> periodo = new ArrayList<String>();
    
    List <MesConta> listaMesCc = new ArrayList();
    String [][] arrayLcto = new String[12][11];  
    
    DateTimeFormatter fd = DateTimeFormatter.ofPattern("dd.MM.yyyy");        
    SimpleDateFormat fd2 = new SimpleDateFormat("yyyy-MM-dd");
    
    public ExtratoContaCorrente() {         
    }
    
    public ContaCorrente CapturaLancamentos(String agencia, String conta, int dataInicialInv, int dataFinalInv) throws PropertyVetoException, Throwable {            
         
        this.agencia = agencia;
        this.conta = conta;
        this.dataInicialInv = dataInicialInv;
        this.dataFinalInv = dataFinalInv;
        
        JanelaSisbb sisbb = new JanelaSisbb(); 
            
        sisbb.setTamanho(1000, 700);
        
        sisbb.Aplicativo(System.getProperty("user.name"), "DEB", true); 
        
        if(sisbb.copiar(1, 3, 8).equals("IBBM100B") ) {  
            sisbb.teclarAguardarTroca("@3");
        }
        
        sisbb.aguardarInd(1, 3, "DEBM");
        if(sisbb.copiar(1, 3, 8).equals("DEBM0018") ) {  
            sisbb.teclarAguardarTroca("@3");
        }
        
        sisbb.aguardarInd(1, 3, "DEBM0050");
        sisbb.colar(21, 11, "35");
        sisbb.teclarAguardarTroca("@E");
        
        sisbb.aguardarInd(1, 3, "DEBME000");
        sisbb.colar(16, 13, "09");
        sisbb.teclarAguardarTroca("@E");
       
        IdConta ccId = new IdConta();
        ccId.setNrAgencia(Short.parseShort(agencia));
        ccId.setNrConta(Long.valueOf(conta));
        
        ContaCorrente cc = new ContaCorrente();  
        cc.setIdConta(ccId);
        
        capturaLancamentos1997(sisbb, cc);
        
        capturaLancamentos2005(sisbb, cc);
        
        cc.setNomeCliente(nomeTitular);
        cc.setCpfCliente(cpfTitular);
        cc.setDtAbertura(dataAbertura);
        cc.setListMesConta(listaMesCc);
        
        sisbb.teclarAguardarTroca("@5");
        sisbb.rotinaEncerramento();
        
        return cc;
    }    
        
        
    public void capturaLancamentos1997(JanelaSisbb sisbb, ContaCorrente cc) throws RoboException, ParseException {                
        sisbb.aguardarInd(1, 3, "DEBME900");
        sisbb.colar(19, 16, "001");  
        sisbb.colar(20, 16, "    ");   
        sisbb.colar(20, 16, agencia);   
        sisbb.colar(21, 16, "          ");   
        sisbb.colar(21, 16, conta);   
        if(sisbb.copiar(6, 48, 1).equals("*")) {
            sisbb.colar(18, 16, "20");   
        }    
        else {
            sisbb.colar(18, 16, "21");   
        }
        sisbb.teclarAguardarTroca("@E");
        if(!sisbb.copiar(23, 3, 29).equals("Nenhuma ocorrência encontrada")) {
            while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
                sisbb.aguardarInd(1, 3, "DEBME92A"); 
                for(int linhaPeriodo=11;linhaPeriodo<21;linhaPeriodo++) {  
                    if(!sisbb.copiar(linhaPeriodo, 05, 3).equals("") && !sisbb.copiar(linhaPeriodo, 39, 14).equals("Não localizado")) {  
                        int dataSisbbInv = parseInt(sisbb.copiar(linhaPeriodo, 21, 4) + sisbb.copiar(linhaPeriodo, 18, 2));
                        if(dataSisbbInv >= dataInicialInv){
                            if(dataSisbbInv > dataFinalInv) {
                                break;   
                            }
                            if(!sisbb.copiar(linhaPeriodo, 42, 13).equals("processamento")) {
                                if(verificaPeriodoCapturado(sisbb.copiar(linhaPeriodo, 18, 7)) == false) {
                                sisbb.colar(linhaPeriodo, 3, "X");  
                                sisbb.teclarAguardarTroca("@E"); 
                                sisbb.aguardarInd(1, 3, "DEBME934");
                                if(sisbb.copiar(23, 3, 21).equals("Conta não movimentada")) {
                                   sisbb.teclarAguardarTroca("@3");   
                                   sisbb.aguardarInd(1, 3, "DEBME92A"); 
                                }
                                else 
                                {    
                                    MesConta mesCc = new MesConta();
                                    mesCc.setDtPeriodoLancamento(sisbb.copiar(3, 51, 7).replace(".", "/"));
                                    mesCc.setDtSaldoAnterior(fd2.parse(LocalDate.parse(sisbb.copiar(12, 27, 10), fd).toString()));
                                    Double valor = Double.parseDouble(sisbb.copiar(12, 61, 18).replaceAll("\\.", "").replaceAll(",", ".").trim());
                                    if (sisbb.copiar(12, 80, 1).equals("D")) { 
                                       valor = valor * -1;
                                    }
                                    mesCc.setVlSaldoAnterior(valor);
                                    if(!sisbb.copiar(7, 56, 25).trim().equals("")) {
                                       int ind = sisbb.copiar(7, 56, 25).indexOf("(");
                                        if(ind == -1) {
                                            mesCc.setVlLimite(Double.parseDouble(sisbb.copiar(7, 56, 25).replaceAll("\\.", "").trim())); 
                                        }
                                        else {
                                            mesCc.setVlLimite(Double.parseDouble(sisbb.copiar(7, 56, 25).substring(0, ind).replaceAll("\\.", "").replaceAll(",", ".").trim())); 
                                        } 

                                    } 
                                    if(!sisbb.copiar(8, 56, 10).trim().equals("")) {
                                       mesCc.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(8, 56, 10), fd).toString()));
                                    }  

                                    if(nomeTitular == null) {
                                        nomeTitular = sisbb.copiar(6, 16, 25).trim();
                                        cpfTitular = sisbb.copiar(7, 16, 20).trim();
                                        dataAbertura = fd2.parse(LocalDate.parse(sisbb.copiar(4, 71, 10), fd).toString());
                                    }

                                    List <LancamentoConta> listaLctoCc = new ArrayList();

                                    while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
                                        limpaArrayLcto();
                                        int idxArray = 0;
                                        for(int linhaLcto=12;linhaLcto<21;linhaLcto++) {
                                            if(!sisbb.copiar(linhaLcto, 40, 18).equals("")) {   
                                                if(!sisbb.copiar(linhaLcto, 59, 1).equals("*")) {
                                                    arrayLcto[idxArray][0] = sisbb.copiar(linhaLcto, 9, 3);
                                                    arrayLcto[idxArray][1] = sisbb.copiar(linhaLcto, 13, 10);
                                                    arrayLcto[idxArray][2] = sisbb.copiar(linhaLcto, 24, 15);
                                                    arrayLcto[idxArray][3] = sisbb.copiar(linhaLcto, 40, 18);
                                                    arrayLcto[idxArray][4] = sisbb.copiar(linhaLcto, 59, 1);
                                                    arrayLcto[idxArray][5] = sisbb.copiar(linhaLcto, 61, 18);
                                                    arrayLcto[idxArray][6] = sisbb.copiar(linhaLcto, 80, 1);
                                                    idxArray++;
                                                }    
                                            }
                                        }   

                                        sisbb.teclarAguardarTroca("@9"); 
                                        sisbb.aguardarInd(10, 34, "Lote");

                                        idxArray = 0;
                                        for(int linhaLcto=12;linhaLcto<21;linhaLcto++) {
                                            if(!sisbb.copiar(linhaLcto, 40, 18).equals("")) {   
                                                if(!sisbb.copiar(linhaLcto, 59, 1).equals("*")) {
                                                    arrayLcto[idxArray][7] = sisbb.copiar(linhaLcto, 16, 4);
                                                    arrayLcto[idxArray][8] = sisbb.copiar(linhaLcto, 24, 5);
                                                    arrayLcto[idxArray][9] = sisbb.copiar(linhaLcto, 33, 5);
                                                    String data = sisbb.copiar(3, 54, 4) + "-" + sisbb.copiar(linhaLcto, 6, 2) + "-" + sisbb.copiar(linhaLcto, 3, 2);                                               
                                                    arrayLcto[idxArray][10] = data;                                                
                                                    idxArray++;
                                                }
                                            }
                                        }

                                        for(int lin=0;lin<arrayLcto.length;lin++) {
                                            LancamentoConta lcto = new LancamentoConta();
                                            if (arrayLcto[lin][0].trim().equals("") && arrayLcto[lin][1].trim().equals("")) {
                                                break;
                                            }
                                            lcto.setNrHistorico(Integer.parseInt(arrayLcto[lin][0].trim()));
                                            lcto.setTxHistorico(arrayLcto[lin][1].trim());
                                            lcto.setNrDocumento(arrayLcto[lin][2].trim()); 
                                            if(!arrayLcto[lin][3].equals("")) {
                                               lcto.setVlDocumento(Double.parseDouble(arrayLcto[lin][3].trim().replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                            }  else {
                                                 lcto.setVlDocumento(Double.parseDouble("0"));
                                            } 
                                            lcto.setInOperacaoLancamento(arrayLcto[lin][4].trim().charAt(0));
                                            valor = Double.parseDouble(arrayLcto[lin][5].trim().replaceAll("\\.", "").replaceAll(",", ".").trim());                                                       
                                            if (!arrayLcto[lin][5].equals("0,00") && !arrayLcto[lin][5].trim().equals("") && arrayLcto[lin][6].equals("D")) { 
                                                valor = valor * -1;
                                            }
                                            lcto.setVlSaldo(valor);
                                            if(!arrayLcto[lin][7].equals("")) {
                                               lcto.setNrBanco(Integer.parseInt(arrayLcto[lin][7].trim()));
                                            }
                                            if(!arrayLcto[lin][8].equals("")) {
                                               lcto.setNrOrigem(Integer.parseInt(arrayLcto[lin][8].trim()));
                                            }
                                            if(!arrayLcto[lin][9].equals("")) {
                                               lcto.setNrLote(Integer.parseInt(arrayLcto[lin][9].trim()));      
                                            }   
                                            lcto.setDtLancamento(fd2.parse(arrayLcto[lin][10].trim()));
                                            lcto.setMesConta(mesCc);
                                            listaLctoCc.add(lcto);
                                        }

                                        sisbb.teclarAguardarTroca("@9"); 
                                        sisbb.aguardarInd(10, 30, "Documento");

                                        sisbb.teclarAguardarTroca("@8");   
                                }

                                mesCc.setListLancamentoConta(listaLctoCc);
                                mesCc.setContaCorrente(cc);
                                listaMesCc.add(mesCc);

                                sisbb.teclarAguardarTroca("@3");   
                                sisbb.aguardarInd(1, 3, "DEBME92A"); 
                            } 
                        } 
                        }        
                    }        
                }    
            }    
            sisbb.teclarAguardarTroca("@8");                 
            }    
            sisbb.teclarAguardarTroca("@3");   
        }    
    } 
    
    public void capturaLancamentos2005(JanelaSisbb sisbb, ContaCorrente cc) throws RoboException, ParseException {                
        sisbb.aguardarInd(1, 3, "DEBME900");
        sisbb.colar(18, 16, "02");
        sisbb.colar(20, 16, "    ");               
        sisbb.colar(20, 16, agencia);   
        sisbb.colar(21, 16, "          ");   
        sisbb.colar(21, 16, conta);   
        sisbb.teclarAguardarTroca("@E");
        if(!sisbb.copiar(23, 3, 14).equals("Não há extrato") && !sisbb.copiar(23, 3, 8).equals("Utilizar")) {   
            while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
                int dataSisbbInv = parseInt(sisbb.copiar(11, 11, 4) + sisbb.copiar(11, 8, 2));
                if(dataSisbbInv > dataInicialInv) {
                   sisbb.teclarAguardarTroca("@8");        
                }
                else 
                {
                   break;    
                }        
            }
            while(!sisbb.copiar(23, 3, 8).equals("Primeira")) {
                sisbb.aguardarInd(1, 3, "DEBME032"); 
                for(int linhaPeriodo=20;linhaPeriodo>10;linhaPeriodo--) {   
                if(!sisbb.copiar(linhaPeriodo, 8, 2).equals("")) {  
                    int dataSisbbInv = parseInt(sisbb.copiar(linhaPeriodo, 11, 4) + sisbb.copiar(linhaPeriodo, 8, 2));
                    if((parseInt(sisbb.copiar(linhaPeriodo, 11, 4) + sisbb.copiar(linhaPeriodo, 8, 2))) >= dataInicialInv) {
                        if(dataSisbbInv > dataFinalInv) {
                           break;   
                        }
                        if(verificaPeriodoCapturado(sisbb.copiar(linhaPeriodo, 8, 7)) == false) {
                            sisbb.colar(linhaPeriodo, 3, "X");  
                            sisbb.teclar("@E");
                            sisbb.aguardarInd(1, 3, "DEBME033");
                            if(sisbb.copiar(23, 3, 21).equals("Conta não movimentada")) {
                                sisbb.teclarAguardarTroca("@3");   
                                sisbb.aguardarInd(1, 3, "DEBME032"); 
                            }
                            else {
                                MesConta mesCc = new MesConta();
                                mesCc.setDtPeriodoLancamento(sisbb.copiar(3, 52, 7).replace(".", "/"));                           
                                mesCc.setDtSaldoAnterior(fd2.parse(LocalDate.parse(sisbb.copiar(9, 28, 10), fd).toString()));
                                Double valor = Double.parseDouble(sisbb.copiar(9, 62, 17).replaceAll("\\.", "").replaceAll(",", ".").trim());
                                if (sisbb.copiar(9, 80, 1).equals("D")) { 
                                    valor = valor * -1;
                                }
                                mesCc.setVlSaldoAnterior(valor);

                                sisbb.teclar("@2"); 
                                sisbb.aguardarInd(8, 34, "Cadastro");
                                if(!sisbb.copiar(18, 30, 30).trim().equals("")) {
                                    int ind = sisbb.copiar(18, 30, 30).indexOf("(");
                                    if(ind == -1) {
                                        mesCc.setVlLimite(Double.parseDouble(sisbb.copiar(18, 30, 30).replaceAll("\\.", "").replaceAll(",", ".").trim())); 
                                    }
                                    else {
                                        mesCc.setVlLimite(Double.parseDouble(sisbb.copiar(18, 30, 30).substring(0, ind).replaceAll("\\.", "").replaceAll(",", ".").trim())); 
                                    }
                                }
                                if(!sisbb.copiar(20, 30, 10).trim().equals("")) {
                                    mesCc.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(20, 30, 10), fd).toString()));
                                } 
                                if(nomeTitular == null) {
                                   nomeTitular = sisbb.copiar(13, 30, 30).trim();
                                   cpfTitular = sisbb.copiar(17, 30, 20).trim();
                                   dataAbertura = fd2.parse(LocalDate.parse(sisbb.copiar(15, 30, 10), fd).toString());
                                }

                                sisbb.teclar("@3"); 
                                sisbb.aguardarInd(7, 31, "Documento");

                                String dataLctoAnt = "";                                        
                                List <LancamentoConta> listaLctoCc = new ArrayList();

                                while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
                                    limpaArrayLcto();
                                    int idxArray = 0;

                                    for(int linhaLcto=9;linhaLcto<21;linhaLcto++) {
                                        if(!sisbb.copiar(linhaLcto, 3, 5).equals("")) {   
                                            if(!sisbb.copiar(linhaLcto, 60, 1).equals("*")) {
                                                arrayLcto[idxArray][0] = sisbb.copiar(linhaLcto, 9, 3);
                                                arrayLcto[idxArray][1] = sisbb.copiar(linhaLcto, 13, 10);
                                                arrayLcto[idxArray][2] = sisbb.copiar(linhaLcto, 25, 15);
                                                arrayLcto[idxArray][3] = sisbb.copiar(linhaLcto, 41, 18);
                                                arrayLcto[idxArray][4] = sisbb.copiar(linhaLcto, 60, 1);
                                                arrayLcto[idxArray][5] = sisbb.copiar(linhaLcto, 62, 17);
                                                arrayLcto[idxArray][6] = sisbb.copiar(linhaLcto, 80, 1);
                                                idxArray++;
                                            }    
                                        }
                                    }   

                                    sisbb.teclarAguardarTroca("@9"); 
                                    sisbb.aguardarInd(7, 34, "Lote");

                                    idxArray = 0;
                                    for(int linhaLcto=9;linhaLcto<21;linhaLcto++) {
                                        if(!sisbb.copiar(linhaLcto, 3, 5).equals("")) {   
                                            if(!sisbb.copiar(linhaLcto, 60, 1).equals("*")) {
                                                arrayLcto[idxArray][7] = sisbb.copiar(linhaLcto, 17, 4);
                                                arrayLcto[idxArray][8] = sisbb.copiar(linhaLcto, 25, 5);
                                                arrayLcto[idxArray][9] = sisbb.copiar(linhaLcto, 33, 5);
                                                String data = sisbb.copiar(3, 55, 4) + "-" + sisbb.copiar(linhaLcto, 6, 2) + "-" + sisbb.copiar(linhaLcto, 3, 2);                                               
                                                arrayLcto[idxArray][10] = data;                                                
                                                idxArray++;
                                            }
                                        }
                                    }

                                    for(int lin=0;lin<arrayLcto.length;lin++) {
                                        LancamentoConta lcto = new LancamentoConta();
                                        if (arrayLcto[lin][0].trim().equals("") && arrayLcto[lin][1].trim().equals("")) {
                                            break;
                                        }
                                        lcto.setNrHistorico(Integer.parseInt(arrayLcto[lin][0].trim()));
                                        lcto.setTxHistorico(arrayLcto[lin][1].trim());
                                        lcto.setNrDocumento(arrayLcto[lin][2].trim()); 
                                        if(!arrayLcto[lin][3].equals("")) {
                                           lcto.setVlDocumento(Double.parseDouble(arrayLcto[lin][3].trim().replaceAll("\\.", "").replaceAll(",", ".").trim()));  
                                        }
                                        else {
                                           lcto.setVlDocumento(Double.parseDouble("0"));   
                                        }
                                        lcto.setInOperacaoLancamento(arrayLcto[lin][4].trim().charAt(0));
                                        valor = Double.parseDouble(arrayLcto[lin][5].trim().replaceAll("\\.", "").replaceAll(",", ".").trim());                                                       
                                        if (!arrayLcto[lin][5].equals("0,00") && !arrayLcto[lin][5].trim().equals("") && arrayLcto[lin][6].equals("D")) { 
                                            valor = valor * -1;
                                        }
                                        lcto.setVlSaldo(valor);
                                        if(!arrayLcto[lin][7].equals("")) {
                                           lcto.setNrBanco(Integer.parseInt(arrayLcto[lin][7].trim()));
                                        }
                                        if(!arrayLcto[lin][8].equals("")) {
                                           lcto.setNrOrigem(Integer.parseInt(arrayLcto[lin][8].trim()));
                                        }
                                        if(!arrayLcto[lin][9].equals("")) {
                                           lcto.setNrLote(Integer.parseInt(arrayLcto[lin][9].trim()));      
                                        }   
                                        lcto.setDtLancamento(fd2.parse(arrayLcto[lin][10].trim()));

                                        lcto.setMesConta(mesCc);
                                        listaLctoCc.add(lcto);
                                        dataLctoAnt = arrayLcto[lin][10];
                                    }

                                    sisbb.teclarAguardarTroca("@9"); 
                                    sisbb.aguardarInd(7, 31, "Documento");

                                    sisbb.teclarAguardarTroca("@8");   
                                }
                                
                             //pega chave inserida no sistema
                                mesCc.setChaveFunci(System.getProperty("user.name")); 
                            //Data e hora atual do sistema
                                LocalDateTime agora = LocalDateTime.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                                mesCc.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));
                                
                                mesCc.setListLancamentoConta(listaLctoCc);
                                mesCc.setContaCorrente(cc);
                                listaMesCc.add(mesCc);
                                
                                sisbb.teclarAguardarTroca("@3");   
                                sisbb.aguardarInd(1, 3, "DEBME032"); 
                            }
                        }
                    }    
                }    
            }    
            sisbb.teclarAguardarTroca("@7");                 
            }       
        }
    }
        
    public void limpaArrayLcto() {
        for(int i=0;i<12;i++) {
            for(int j=0;j<11;j++) {
               arrayLcto[i][j] = "";
            }   
        }
    } 
    

    public Boolean verificaPeriodoCapturado(String per) {
        String s = per.replace(".", "/");     
        
        if(periodo.contains(s)){
            return true;
        }
        else{
            periodo.add(s);
            return false;
        }
    }
}