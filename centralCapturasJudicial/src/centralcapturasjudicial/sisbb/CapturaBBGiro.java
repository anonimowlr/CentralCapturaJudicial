/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import antlr.ANTLRTokdefParserTokenTypes;
import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.BBGiroDAO;
import centralcapturasjudicial.model.entity.bbgiro.AberturaTeto;
import centralcapturasjudicial.model.entity.bbgiro.EncargoSubcredito;
import centralcapturasjudicial.model.entity.bbgiro.ExtratoConsolidado;
import centralcapturasjudicial.model.entity.bbgiro.ExtratoSubcredito;
import centralcapturasjudicial.model.entity.bbgiro.ItemFinanciado;
import centralcapturasjudicial.model.entity.bbgiro.OperacaoGiro;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author f3295813
 */
public class CapturaBBGiro {

    SimpleDateFormat fd2 = new SimpleDateFormat("ddMMyyy");

    public OperacaoGiro capturaDadosGerais(JanelaSisbb sisbb, JFXTextField inputOpBBGiro, JFXTextField inputAgenciaBBGiro) throws PropertyVetoException, Throwable { // pega os dados principais da operação

        OperacaoGiro dadosGerais = new OperacaoGiro();
        //BBGiroDAO c = new BBGiroDAO();

        int agencia = Integer.parseInt(inputAgenciaBBGiro.getText());
        String operacao = inputOpBBGiro.getText();
        String data_proposta;

        
        sisbb.setTamanho(1000, 700);

        sisbb.Aplicativo(System.getProperty("user.name"), "COP", true); //Verificar qual aplicativo o funcionário vai querer usar.

        sisbb.aguardarInd(1, 18, "SISBB");//Segunda tela do SISBB

        Thread.sleep(300);

        if (sisbb.copiar(5, 38, 7).equals("ATENÇÃO")) {
            sisbb.teclarAguardarTroca("@3");
            Thread.sleep(300);
        }

        if (sisbb.copiar(3, 36, 10).equals("Comunicado")) {
            sisbb.teclarAguardarTroca("@E");
            Thread.sleep(300);
        }

        sisbb.aguardarInd(1, 3, "COPM0000");

        sisbb.colar(19, 24, "99");
        sisbb.teclarAguardarTroca("@E");
        sisbb.aguardarInd(1, 3, "COPM9504");

        sisbb.teclarAguardarTroca("@3");

        sisbb.aguardarInd(1, 3, "COPM0000");

        sisbb.colar(19, 24, "24");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "COPM1908");

        sisbb.colar(19, 26, "12");

        sisbb.colar(20, 26, "    ");
        sisbb.colar(20, 26, Integer.toString(agencia));
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "COPM1153");

        sisbb.colar(15, 28, "11");
        sisbb.colar(16, 28, operacao);
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "COPM1115");

        sisbb.colar(21, 20, "11");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "COPM1158");

        data_proposta = sisbb.copiar(9, 71, 10).trim().replace(".", "-");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date = (Date) formatter.parse(data_proposta);

        dadosGerais.setNrAgencia(agencia);
        dadosGerais.setDtProposta(date);
        dadosGerais.setTxMutuario(sisbb.copiar(6, 13, 43).trim());
        dadosGerais.setTxLinhaCredito(sisbb.copiar(5, 21, 50).trim());
        dadosGerais.setNrOperacao(Integer.parseInt(operacao));

     
        return dadosGerais;

    }

    public OperacaoGiro extratoConsolidado(JanelaSisbb sisbb, String dataProposta, OperacaoGiro dadosGerais) throws RoboException, InterruptedException, ParseException { // pega o extrato na opção 41 do aplicativo SISBB COP

        List<ExtratoConsolidado> listaExtCon = new ArrayList();
        String data_mov;
        String valor;
        String situacao;
        String valor2 = null;

        sisbb.aguardarInd(1, 3, "COPM1158");

        String data_parte1 = dataProposta.substring(0, 2); //pega o dia
        String data_parte2 = dataProposta.substring(2, 4); //pega o mês
        String data_parte3 = dataProposta.substring(4, 8); //pega o ano

        sisbb.aguardarInd(1, 3, "COPM1158");
        sisbb.teclarAguardarTroca("@3");
        sisbb.colar(21, 20, "41");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(7, 22, "Selecione");

        sisbb.colar(12, 29, "1");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "COPM1141");

        sisbb.colar(7, 54, "  ");
        sisbb.colar(7, 59, "  ");
        sisbb.colar(7, 64, "    ");

        Thread.sleep(200);

        sisbb.colar(7, 54, data_parte1);
        sisbb.colar(7, 59, data_parte2);
        sisbb.colar(7, 64, data_parte3);

        sisbb.teclarAguardarTroca("@E");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();

        int linha = 14;

        do {
            
            Thread.sleep(100);

            ExtratoConsolidado ec = new ExtratoConsolidado();

            ec.setOperacaoGiro(dadosGerais);

          
             Thread.sleep(100);
             
            data_mov = sisbb.copiar(linha, 3, 10).replace(".", "-");

            date = (Date) formatter.parse(data_mov);

            ec.setDtMovimento(date);
            ec.setTxEfeitoOperacao(sisbb.copiar(linha, 14, 10).trim());
            ec.setTxDescricaoLancamento(sisbb.copiar(linha, 25, 38).trim());

            situacao = sisbb.copiar(linha, 80, 1);

            valor = (sisbb.copiar(linha, 64, 16).replace(".", "").replace(",", "."));

            if (situacao.equals("D")) {

                valor = ("-").concat(valor);

            }

            System.out.println(valor);

            ec.setVlLancamento(Double.parseDouble(valor));

            linha++;

            if (sisbb.copiar(linha, 31, 7).equals("parcial") & (linha == 20)) {

                sisbb.teclar("@8");
                linha = 12;
                Thread.sleep(100);

            }

            if (linha == 20) {

                sisbb.teclar("@8");
                linha = 12;
                Thread.sleep(100);

            }

            listaExtCon.add(ec);

        } while (sisbb.copiar(3, 70, 4).equals("0001"));

        int linha2 = 12;

        LACO:
        do {

         //   Thread.sleep(100);
            
            ExtratoConsolidado ec = new ExtratoConsolidado();

            ec.setOperacaoGiro(dadosGerais);

            data_mov = sisbb.copiar(linha2, 3, 10).replace(".", "-");

            date = (Date) formatter.parse(data_mov);

            ec.setDtMovimento(date);
            ec.setTxEfeitoOperacao(sisbb.copiar(linha2, 14, 10).trim());
            ec.setTxDescricaoLancamento(sisbb.copiar(linha2, 25, 38).trim());

            situacao = sisbb.copiar(linha2, 80, 1);

            valor = (sisbb.copiar(linha2, 64, 16).replace(".", "").replace(",", "."));

            if (situacao.equals("D")) {

                valor = ("-").concat(valor);

            }
            String situacaoo = ec.getTxDescricaoLancamento();

            ec.setVlLancamento(Double.parseDouble(valor));

            linha2++;

            if (linha2 == 20) {

                sisbb.teclar("@8");
                linha2 = 12;
                Thread.sleep(90);

            }

            String desc = ec.getTxDescricaoLancamento();

            if (!desc.contains("Saldo parcial ----------")) {

                listaExtCon.add(ec);
                Double valor1 = ec.getVlLancamento();
                System.out.println(valor1);;

            }
            
            
             Thread.sleep(90);
            
        } while (!sisbb.copiar(23, 3, 6).equals("Ultima"));

        dadosGerais.setListExtratoConsolidado(listaExtCon);

        return dadosGerais;

    }

    public OperacaoGiro itensFinanciados(JanelaSisbb sisbb, String data_proposta, OperacaoGiro dadosGerais) throws RoboException, InterruptedException, ParseException { // itens financiados da opção 42 do SISBB COP

        String data_mov;
        String valor;
        String situacao;
        String valor2 = null;

        List<ExtratoSubcredito> listaExtSub = new ArrayList();
        List<ItemFinanciado> listaItemFinan = new ArrayList<>();

        String dataProposta = data_proposta.replace("-", "");
        String data_parte1 = dataProposta.substring(0, 2); //pega o dia
        String data_parte2 = dataProposta.substring(2, 4); //pega o mês
        String data_parte3 = dataProposta.substring(4, 8); //pega o ano

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();

        sisbb.teclarAguardarTroca("@3");

        sisbb.aguardarInd(7, 22, "Selecione");

        sisbb.teclarAguardarTroca("@3");

        sisbb.aguardarInd(1, 3, "COPM1115");

        sisbb.colar(21, 20, "42");

        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(6, 22, "Detalhamento");

        sisbb.colar(17, 38, "01");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(4, 27, "Item");

        int linhaSisbb = 7;
        int linhaVerificacao = 7;

        BREAKDO:
        do {

            ItemFinanciado itemf = new ItemFinanciado();

            itemf.setOperacaoGiro(dadosGerais);

            if (sisbb.copiar(7, 33, 7).equals("CAPITAL")) {

                if (linhaSisbb == 19) {

                    sisbb.teclar("@8");

                    linhaSisbb = 6;
                    Thread.sleep(100);

                }

               
                if (linhaVerificacao > 19) {

                    sisbb.teclar("@8");
                    Thread.sleep(100);

                }

                if (sisbb.copiar(linhaSisbb, 29, 3).equals("")) {

                    sisbb.teclarAguardarTroca("@8");
                    Thread.sleep(100);
                }

                if (sisbb.copiar(linhaSisbb, 29, 3).equals("") & (!sisbb.copiar(23, 3, 6).equals("Ultima"))) {

                    break BREAKDO;

                }

                sisbb.colar(linhaSisbb, 27, "X");

                Thread.sleep(100);

                sisbb.teclarAguardarTroca("@E");

                sisbb.aguardarInd(1, 3, "COPM1152");

                itemf.setTxItemFinanciado(sisbb.copiar(6, 25, 21).trim());

                itemf.setTxSubcredito(sisbb.copiar(7, 16, 60).trim());

                sisbb.colar(8, 54, "  ");
                sisbb.colar(8, 59, "  ");
                sisbb.colar(8, 64, "    ");

                Thread.sleep(100);

                sisbb.colar(8, 54, data_parte1);
                sisbb.colar(8, 59, data_parte2);
                sisbb.colar(8, 64, data_parte3);

                sisbb.teclarAguardarTroca("@E");

                int linha = 14;
                LACODO:
                do {

                    if (sisbb.copiar(23, 3, 6).equals("Ultima")) {
                        break LACODO;

                    }

                    ExtratoSubcredito es = new ExtratoSubcredito();

                    es.setItemFinanciado(itemf);

                    data_mov = sisbb.copiar(linha, 3, 10).replace(".", "-");

                    Thread.sleep(100);
                    date = (Date) formatter.parse(data_mov);

                    es.setDtMovimento(date);
                    es.setTxEfeitoOperacao(sisbb.copiar(linha, 14, 10).trim());
                    es.setTxDescricaoLancamento(sisbb.copiar(linha, 25, 38).trim());

                    situacao = sisbb.copiar(linha, 80, 1);

                    valor = (sisbb.copiar(linha, 64, 16).replace(".", "").replace(",", "."));

                    if (situacao.equals("D")) {

                        valor = ("-").concat(valor);

                    }

                    es.setVlLancamento(Double.parseDouble(valor));

                    linha++;

                    if (sisbb.copiar(linha, 31, 7).equals("parcial") & (linha == 20)) {

                        sisbb.teclarAguardarTroca("@8");
                        linha = 12;
                        Thread.sleep(200);

                    }

                    if (linha == 20) {

                        sisbb.teclarAguardarTroca("@8");
                        linha = 12;
                        Thread.sleep(100);

                    }

                    listaExtSub.add(es);

                } while (sisbb.copiar(3, 72, 3).equals("001"));

                int linha2 = 12;

                SEGUNDOLACO:
                do {

                    ExtratoSubcredito es = new ExtratoSubcredito();

                    if (sisbb.copiar(23, 3, 6).equals("Ultima")) {
                        break SEGUNDOLACO;

                    }

                    data_mov = sisbb.copiar(linha2, 3, 10).replace(".", "-");

                    es.setItemFinanciado(itemf);

                    date = (Date) formatter.parse(data_mov);

                    es.setDtMovimento(date);
                    es.setTxEfeitoOperacao(sisbb.copiar(linha2, 14, 10).trim());
                    es.setTxDescricaoLancamento(sisbb.copiar(linha2, 25, 38).trim());

                    situacao = sisbb.copiar(linha2, 80, 1);

                    valor = (sisbb.copiar(linha2, 64, 16).replace(".", "").replace(",", "."));

                    if (situacao.equals("D")) {

                        valor = ("-").concat(valor);

                    }

                    es.setVlLancamento(Double.parseDouble(valor));

                    linha2++;

                    String desc = es.getTxDescricaoLancamento();

                    if (!desc.contains("Saldo parcial ----------")) {

                        listaExtSub.add(es);
                        Double valor1 = es.getVlLancamento();
                        System.out.println(valor1);

                    }

                    if (linha2 == 20) {

                        sisbb.teclarAguardarTroca("@8");
                        linha2 = 12;
                        Thread.sleep(100);

                    }

                    if (sisbb.copiar(linha2, 25, 3).equals("")) {

                        sisbb.teclarAguardarTroca("@8");
                        Thread.sleep(100);

                    }

                } while (!sisbb.copiar(23, 3, 6).equals("Ultima"));

                Thread.sleep(100);
                sisbb.teclarAguardarTroca("@3");
                Thread.sleep(100);
                sisbb.aguardarInd(1, 3, "COPM1115");
                Thread.sleep(100);
                sisbb.teclarAguardarTroca("@E");

                sisbb.aguardarInd(6, 22, "Detalhamento");
                Thread.sleep(100);

                sisbb.colar(17, 38, "01");
                Thread.sleep(100);
                sisbb.teclarAguardarTroca("@E");
                Thread.sleep(100);

                sisbb.aguardarInd(4, 27, "Item");

                linhaSisbb++;
                linhaVerificacao++;

                listaItemFinan.add(itemf);

                itemf.setListExtratoSubcredito(listaExtSub);

            }

        } while (!sisbb.copiar(22, 27, 6).equals("Ultima"));

        dadosGerais.setListItemFinanciado(listaItemFinan);

        dadosGerais.setListItemFinanciado(encargosSubcreditos(sisbb, data_proposta, listaItemFinan));

        return dadosGerais;

    }

    public List encargosSubcreditos(JanelaSisbb sisbb, String data_proposta, List<ItemFinanciado> listaItemFinan) throws RoboException, InterruptedException { //encargos dos itens financiados opção 14 SISBB COP

        List<EncargoSubcredito> listaEncargoSub = new ArrayList();
        List<ItemFinanciado> listaRetorno = new ArrayList();
        List<EncargoSubcredito> listaEncargos = new ArrayList();
        List<EncargoSubcredito> listaTeste = new ArrayList();

        sisbb.teclarAguardarTroca("@3");

        sisbb.aguardarInd(1, 3, "COPM1115");

        sisbb.colar(21, 20, "14");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(4, 27, "Item");

        int linhaSisbb = 7;
        int linhaVerificacao = 7;

        BREAKDO:
        do {

            listaEncargoSub.clear();

            if (sisbb.copiar(7, 33, 7).equals("CAPITAL")) {

                if (linhaSisbb == 19) {

                    sisbb.teclar("@8");

                    linhaSisbb = 6;
                    Thread.sleep(100);

                }

                if (linhaVerificacao > 19) {

                    sisbb.teclar("@8");
                    Thread.sleep(100);

                }

                if (sisbb.copiar(linhaSisbb, 29, 3).equals("")) {

                    sisbb.teclarAguardarTroca("@8");
                    Thread.sleep(100);
                }

                if (sisbb.copiar(linhaSisbb, 29, 3).equals("") & (!sisbb.copiar(23, 3, 6).equals("Ultima"))) {

                    break BREAKDO;

                }

                sisbb.colar(linhaSisbb, 27, "X");

                sisbb.teclarAguardarTroca("@E");
                Thread.sleep(200);

                String itemFin_encargo = sisbb.copiar(7, 20, 21).trim();

                sisbb.aguardarInd(1, 3, "COPM1142");

                sisbb.teclarAguardarTroca("@2");

                sisbb.aguardarInd(1, 3, "COPM7545");

                int linhaEncargo = 15;

                listaEncargoSub.clear();

                do {

                    EncargoSubcredito enc_sub = new EncargoSubcredito();

                    enc_sub.setTxBaseCalculoEncargo(sisbb.copiar(linhaEncargo, 3, 23));
                    enc_sub.setTxTipoEncargo(sisbb.copiar(linhaEncargo, 27, 28));
                    enc_sub.setTxPercentual(sisbb.copiar(linhaEncargo, 58, 10));
                    enc_sub.setTxMoeda(sisbb.copiar(linhaEncargo, 71, 10));

                    listaEncargoSub.add(enc_sub);

                    linhaEncargo++;

                } while (!sisbb.copiar(linhaEncargo, 27, 6).equals(""));

                LACO:

                for (ItemFinanciado t : listaItemFinan) {

                    listaEncargos = listaEncargoSub;

                    String item = t.getTxItemFinanciado();

                    if (item.equals(itemFin_encargo)) {

                        for (EncargoSubcredito f : listaEncargos) {

                            f.setItemFinanciado(t);
                            listaTeste.add(f);

                        }

                        t.setListEncargoSubcredito(listaTeste);

                        listaRetorno.add(t);

                        break LACO;

                    }

                }

            }

            sisbb.teclarAguardarTroca("@3");
            sisbb.aguardarInd(1, 3, "COPM1142");
            sisbb.teclarAguardarTroca("@3");
            sisbb.aguardarInd(1, 3, "COPM1115");
            sisbb.teclarAguardarTroca("@E");
            sisbb.aguardarInd(4, 27, "Item");

            Thread.sleep(200);

            linhaSisbb++;
            linhaVerificacao++;

        } while (!sisbb.copiar(23, 3, 6).equals("Ultima"));

        return listaRetorno;
    }

    public OperacaoGiro aberturaTeto(JanelaSisbb sisbb, OperacaoGiro dadosGerais) throws RoboException, ParseException, InterruptedException {
        
        
        
        
        

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dataInicio = new Date();
        Date dataFim = new Date();
         List<AberturaTeto> ListaAberturaTeto = new ArrayList();
         
           sisbb.aguardarInd(4, 27, "Item");
           
            sisbb.teclarAguardarTroca("@3");
              
                        
           sisbb.aguardarInd(1, 3, "COPM1115");
           
           sisbb.teclarAguardarTroca("@E");
            
           sisbb.aguardarInd(4, 27, "Item");  

        if (sisbb.copiar(6, 33, 8).equals("ABERTURA")) {

            sisbb.colar(6, 27, "X");

            sisbb.teclarAguardarTroca("@E");

            sisbb.aguardarInd(1, 3, "COPM1142");

            sisbb.teclarAguardarTroca("@2");

            sisbb.aguardarInd(1, 3, "COPM7545");

            sisbb.teclarAguardarTroca("@2");

            sisbb.aguardarInd(7, 13, "Inicio");

          

            int linha = 9;
            
            
            
            do{
                
                  AberturaTeto ab = new AberturaTeto();
                  
                  ab.setOperacaoGiro(dadosGerais);
           

            String data_inicio = sisbb.copiar(linha, 13, 10).replace(".", "-");
            String data_fim = sisbb.copiar(linha, 24, 10).replace(".", "-");
            
                System.out.println(data_fim);

            if (!data_inicio.equals("")) {

                dataInicio = (Date) formatter.parse(data_inicio);
                ab.setDtInicio(dataInicio);

            }

            if (!data_fim.equals("")) {
                
                dataFim = (Date) formatter.parse(data_fim);
                ab.setDtFim(dataFim);
            }

            String taxa = sisbb.copiar(linha, 40, 5).replace(",",  ".");

            String situacao = sisbb.copiar(linha, 37, 1);

            if (situacao.equals("-")) {

                taxa = ("-").concat(taxa);

            }
            

            ab.setVlTaxa(Double.parseDouble(taxa));
            
            
            ListaAberturaTeto.add(ab);
            
            
            dadosGerais.setListAberturaTeto(ListaAberturaTeto);
            
              linha++;
            
          if(linha == 19){
              
              sisbb.teclarAguardarTroca("@8");
              linha = 9;
              
              
              Thread.sleep(100);             
              
          }  
            
          
            
            
            
             }while (!sisbb.copiar(linha, 40, 5 ).equals(""));
            

        } else {
            
            
            
            
            
            

        }

        return dadosGerais;

    }

}
