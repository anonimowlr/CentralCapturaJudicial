/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.ComponenteCdcDAO;
import centralcapturasjudicial.model.dao.OperacaoCdcDAO;
import centralcapturasjudicial.model.entity.cdc.ComponentePagamentoParcelaCdc;
import centralcapturasjudicial.model.entity.cdc.OperacaoCdc;
import centralcapturasjudicial.model.entity.cdc.ParcelaOperacaoCdc;
import centralcapturasjudicial.model.entity.cdc.PagamentoParcelaCdc;
import centralcapturasjudicial.model.entity.cdc.TipoComponenteCdc;
import centralcapturasjudicial.poi.PlanilhaExtratoCdc;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.scene.control.Alert;

/**
 *
 * @author F3161139
 */
public class CapturaFin {

    DateTimeFormatter fd = DateTimeFormatter.ofPattern("dd.MM.yy");
    DateTimeFormatter fd1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    SimpleDateFormat fd2 = new SimpleDateFormat("yyyy-MM-dd");
    DateTimeFormatter fd3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    List<TipoComponenteCdc> listaCmpt = new ArrayList();

    public CapturaFin() {
    }

    public void capturaExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputOp, JFXTextField inputCpf, JFXTextField inputOperacao) throws Throwable {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");

        if (inputOperacao.getText().matches("[0-9]+")) {
//            alert.setHeaderText("Problema no número da operação...");
//            alert.setContentText("Algum erro no número da operação, verifique!");
//            alert.showAndWait();
            geraExtratos(inputAgencia, inputConta, inputOp, inputCpf, inputOperacao);
        }

        if (inputCpf.getText().matches("[0-9]+")) {
//            alert.setHeaderText("Problema no número da operação...");
//            alert.setContentText("Algum erro no número da operação, verifique!");
//            alert.showAndWait();
            geraExtratos(inputAgencia, inputConta, inputOp, inputCpf, inputOperacao);

        } else {
            try {
                String msg = geraExtratos(inputAgencia, inputConta, inputOp, inputCpf, inputOperacao);
                if (msg.equals("")) {
                    alertSucesso();
                } else {
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
    }

    private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();
    }

    private String geraExtratos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputOp, JFXTextField inputCpf, JFXTextField inputOperacao) throws PropertyVetoException, Throwable {

        int linhaFin = 7;
        int totalLinhas = 16;

        OperacaoCdcDAO daoOpe = new OperacaoCdcDAO();

        if (inputCpf.getText().equals("")) {

            daoOpe.remover(OperacaoCdc.class, Long.parseLong(inputOperacao.getText()));

            // Carregar lista dos componentes de pagamento
            ComponenteCdcDAO daoCpnt = new ComponenteCdcDAO();

            listaCmpt = daoCpnt.consultarTodos(TipoComponenteCdc.class);
            if (listaCmpt.isEmpty()) {
                return "Lista de componentes não cadastrada na Base de Dados.";
            }

        }

        if (!inputCpf.getText().equals("")) {

            daoOpe.remover(OperacaoCdc.class, Long.parseLong(inputCpf.getText()));

            // Carregar lista dos componentes de pagamento
            ComponenteCdcDAO daoCpnt = new ComponenteCdcDAO();

            listaCmpt = daoCpnt.consultarTodos(TipoComponenteCdc.class);
            if (listaCmpt.isEmpty()) {
                return "Lista de componentes não cadastrada na Base de Dados.";
            }

        }

        JanelaSisbb sisbb = new JanelaSisbb();

        sisbb.setTamanho(1000, 700);

        sisbb.Aplicativo(System.getProperty("user.name"), "FIN", true);

        sisbb.aguardarInd(1, 18, "SISBB");

        Thread.sleep(300);

        sisbb.aguardarInd(1, 3, "FINM000A");
        sisbb.colar(20, 20, "13");
        sisbb.teclarAguardarTroca("@E");

        Thread.sleep(300);

        sisbb.aguardarInd(1, 3, "FINM0000");
        sisbb.colar(21, 20, "56");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "FINM5600");
        

//-----------------------------------------------------------------If respectivo a Operação------------------------------------------------------------------------
        if (inputCpf.getText().equals("")) {
            sisbb.colar(15, 24, "01");
            sisbb.colar(17, 24, inputOperacao.getText());

            sisbb.teclarAguardarTroca("@E");
            sisbb.aguardarInd(1, 3, "FINM5640");
            
           // if (!sisbb.copiar(4, 23, 10).equals("")) {

            //while (!sisbb.copiar(19, 23, 5).equals("ltima")) {

                OperacaoCdc opeCdc = new OperacaoCdc();

               // IntStream.rangeClosed(7, 16).forEach((int j) -> {
                    List<ParcelaOperacaoCdc> listaParc = new ArrayList();
                    try {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //if (!sisbb.copiar(j, 25, 8).equals("")) {
                            //sisbb.colar(j, 23, "x");
                            //sisbb.teclarAguardarTroca("@E");
                            //sisbb.aguardarInd(1, 3, "FINM5640");

                            opeCdc.setId(Long.parseLong(sisbb.copiar(4, 19, 10).replace(".", "")));//captura número da proposta
                            opeCdc.setNomeCliente(sisbb.copiar(5, 32, 50).trim());//captura nome cliente
                            opeCdc.setCpfCliente(sisbb.copiar(7, 19, 14).replaceAll("\\D", ""));//captura CPF
                            opeCdc.setNrAgenciaDebito(sisbb.copiar(11, 19, 10).trim());//captura ag.débito
                            opeCdc.setNrContaDebito(sisbb.copiar(12, 19, 20).trim());//captura conta débito
                            opeCdc.setTxSituacaoCdc(sisbb.copiar(12, 50, 25).trim());//captura situação
                            opeCdc.setDtPrimeiraParcela(fd2.parse(LocalDate.parse(sisbb.copiar(13, 50, 10), fd1).toString()));//captura data 1º vencimento
                            opeCdc.setVlTotalFinanciado(Double.parseDouble(sisbb.copiar(17, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor financiamento
                            opeCdc.setVlSolicitado(Double.parseDouble(sisbb.copiar(18, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor requerido
                            opeCdc.setVlIof(Double.parseDouble(sisbb.copiar(19, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor IOF
                            if (!sisbb.copiar(20, 24, 12).equals("")) {
                                opeCdc.setVlJurosCarencia(Double.parseDouble(sisbb.copiar(20, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura juros carência
                            }
                            opeCdc.setQtParcela(Short.parseShort(sisbb.copiar(17, 60, 12).trim()));//captura prazo
                            opeCdc.setTxJurosMensal(Float.parseFloat(sisbb.copiar(18, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura Taxa Nominativa
                            opeCdc.setTxJurosAnual(Float.parseFloat(sisbb.copiar(19, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura Taxa Efetiva
                            opeCdc.setVlPrestacao(Double.parseDouble(sisbb.copiar(20, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                            opeCdc.setDtSaldoDevedor(fd2.parse(LocalDate.parse(sisbb.copiar(21, 62, 10), fd1).toString()));

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            sisbb.teclarAguardarTroca("@4");
                            sisbb.aguardarInd(1, 3, "FINM5641");

                            while (!sisbb.copiar(23, 3, 5).equals("ltima")) {
                                IntStream.rangeClosed(10, 21).forEach((int l) -> {

                                    try {

                                        if (!sisbb.copiar(l, 8, 3).equals("")) {
                                            ParcelaOperacaoCdc pcl = new ParcelaOperacaoCdc();

                                            try {
                                                Thread.sleep(400);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            pcl.setNrParcela(Integer.parseInt(sisbb.copiar(l, 8, 6)));
                                            pcl.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(l, 29, 10), fd1).toString()));//Captura data do vencimento
                                            pcl.setVlCapital(Double.parseDouble(sisbb.copiar(l, 15, 11).replaceAll("\\.", "").replaceAll(",", ".").trim()));//Captura valor base
                                            pcl.setOperacaoCdc(opeCdc);

                                            sisbb.colar(l, 5, "x");
                                            sisbb.teclarAguardarTroca("@E");

                                            //Captura dados dos pagamentos da parcela
                                            PagamentoParcelaCdc pgto = new PagamentoParcelaCdc();
                                            pgto.setParcelaOperacaoCdc(pcl);

                                            ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                            TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();

                                            List<PagamentoParcelaCdc> listaPgto = new ArrayList();
                                            if (sisbb.copiar(6, 31, 21).equals("DETALHES DA PRESTACAO")) {

                                                if (!sisbb.copiar(17, 44, 10).equals("")) {

                                                    pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(17, 44, 10), fd1).toString()));
                                                    short nrRec = 1;
                                                    pgto.setNrRecebimento(nrRec);

                                                    if (!sisbb.copiar(8, 42, 12).trim().equals(sisbb.copiar(14, 42, 12).trim())) {

                                                    }

                                                }

                                                if (sisbb.copiar(17, 44, 10).equals("")) {

                                                    //pgto.setDtRec(fd2.parse(LocalDate.parse(sisbb.copiar(17, 44, 10), fd1).toString()));
                                                    short nrRec = 1;
                                                    pgto.setNrRecebimento(nrRec);

                                                    if (!sisbb.copiar(8, 42, 12).trim().equals(sisbb.copiar(14, 42, 12).trim())) {

                                                    }

                                                }

                                                List<ComponentePagamentoParcelaCdc> listaCpntPgto = new ArrayList();
                                                for (int linCpntRec = 8; linCpntRec < 12; linCpntRec++) {

                                                    if (!sisbb.copiar(linCpntRec, 42, 12).trim().equals("")) {

                                                        tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 16).toUpperCase().replaceAll("\\.", "").trim());
                                                        if (tipoCpntPgto != null) {

                                                            cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                                            cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 42, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                                            cpntPgto.setPagamentoParcelaCdc(pgto);
                                                            listaCpntPgto.add(cpntPgto);
                                                        }
//                                                        
                                                    }

                                                }
                                                pgto.setListComponentePagamentoParcelaCdc(listaCpntPgto);
                                                listaPgto.add(pgto);

                                            }

                                            pcl.setListPagamentoParcelaCdc(listaPgto);
                                            listaParc.add(pcl);

                                        }
                                    } catch (RoboException | ParseException ex) {
                                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    try {
                                        // }

                                        if (sisbb.copiar(6, 31, 21).equals("DETALHES DA PRESTACAO")) {
                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            sisbb.teclarAguardarTroca("@3");
                                        }

                                        if (l == 21) {
                                            sisbb.teclarAguardarTroca("@8");

                                        }

                                    } catch (RoboException ex) {
                                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                });

                            }

                            sisbb.teclarAguardarTroca("@3");
                            sisbb.teclarAguardarTroca("@3");

                        //}
                    } catch (RoboException ex) {
                        Logger.getLogger(CapturaExtratoCartaoCredito.class
                                .getName()).log(Level.SEVERE, null, ex);
                    } catch (ParseException ex) {
                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (listaParc.size() > 0) {
                        opeCdc.setListParcelaOperacaoCdc(listaParc);
                        try {
                            daoOpe.salvar(opeCdc);
                        } catch (Exception ex) {
                            Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PlanilhaExtratoCdc planilha = new PlanilhaExtratoCdc();
                        try {
                            planilha.criaPlanilha(opeCdc);
                        } catch (Exception ex) {
                            try {
                                throw new Exception(ex);
                            } catch (Exception ex1) {
                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                    }

                //});//respectivo ao foreach da linha 162
                sisbb.teclarAguardarTroca("@8");

            //}//respectivo ao while da linha 158

            sisbb.rotinaEncerramento();

        //}//respectivo ao if da linha 156

//-----------------------------------------------------------------Else respectivo ao CPF------------------------------------------------------------------------
        } else {
            sisbb.colar(15, 24, "04");
            sisbb.colar(20, 24, inputCpf.getText());
        }

        sisbb.teclarAguardarTroca("@E");

        if (!sisbb.copiar(4, 23, 10).equals("")) {

            while (!sisbb.copiar(19, 23, 5).equals("ltima")) {

                OperacaoCdc opeCdc = new OperacaoCdc();

                IntStream.rangeClosed(7, 16).forEach((int j) -> {
                    List<ParcelaOperacaoCdc> listaParc = new ArrayList();
                    try {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!sisbb.copiar(j, 25, 8).equals("")) {
                            sisbb.colar(j, 23, "x");
                            sisbb.teclarAguardarTroca("@E");
                            sisbb.aguardarInd(1, 3, "FINM5640");

                            opeCdc.setId(Long.parseLong(sisbb.copiar(4, 19, 10).replace(".", "")));//captura número da proposta
                            opeCdc.setNomeCliente(sisbb.copiar(5, 32, 50).trim());//captura nome cliente
                            opeCdc.setCpfCliente(sisbb.copiar(7, 19, 14).replaceAll("\\D", ""));//captura CPF
                            opeCdc.setNrAgenciaDebito(sisbb.copiar(11, 19, 10).trim());//captura ag.débito
                            opeCdc.setNrContaDebito(sisbb.copiar(12, 19, 20).trim());//captura conta débito
                            opeCdc.setTxSituacaoCdc(sisbb.copiar(12, 50, 25).trim());//captura situação
                            opeCdc.setDtPrimeiraParcela(fd2.parse(LocalDate.parse(sisbb.copiar(13, 50, 10), fd1).toString()));//captura data 1º vencimento
                            opeCdc.setVlTotalFinanciado(Double.parseDouble(sisbb.copiar(17, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor financiamento
                            opeCdc.setVlSolicitado(Double.parseDouble(sisbb.copiar(18, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor requerido
                            opeCdc.setVlIof(Double.parseDouble(sisbb.copiar(19, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura valor IOF
                            if (!sisbb.copiar(20, 24, 12).equals("")) {
                                opeCdc.setVlJurosCarencia(Double.parseDouble(sisbb.copiar(20, 24, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura juros carência
                            }
                            opeCdc.setQtParcela(Short.parseShort(sisbb.copiar(17, 60, 12).trim()));//captura prazo
                            opeCdc.setTxJurosMensal(Float.parseFloat(sisbb.copiar(18, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura Taxa Nominativa
                            opeCdc.setTxJurosAnual(Float.parseFloat(sisbb.copiar(19, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));//captura Taxa Efetiva
                            opeCdc.setVlPrestacao(Double.parseDouble(sisbb.copiar(20, 60, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                            opeCdc.setDtSaldoDevedor(fd2.parse(LocalDate.parse(sisbb.copiar(21, 62, 10), fd1).toString()));

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            sisbb.teclarAguardarTroca("@4");
                            sisbb.aguardarInd(1, 3, "FINM5641");

                            while (!sisbb.copiar(23, 3, 5).equals("ltima")) {
                                IntStream.rangeClosed(10, 21).forEach((int l) -> {

                                    try {

                                        if (!sisbb.copiar(l, 8, 3).equals("")) {
                                            ParcelaOperacaoCdc pcl = new ParcelaOperacaoCdc();

                                            try {
                                                Thread.sleep(400);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            pcl.setNrParcela(Integer.parseInt(sisbb.copiar(l, 8, 6)));
                                            pcl.setDtVencimento(fd2.parse(LocalDate.parse(sisbb.copiar(l, 29, 10), fd1).toString()));//Captura data do vencimento
                                            pcl.setVlCapital(Double.parseDouble(sisbb.copiar(l, 15, 11).replaceAll("\\.", "").replaceAll(",", ".").trim()));//Captura valor base
                                            pcl.setOperacaoCdc(opeCdc);

                                            sisbb.colar(l, 5, "x");
                                            sisbb.teclarAguardarTroca("@E");

                                            //Captura dados dos pagamentos da parcela
                                            PagamentoParcelaCdc pgto = new PagamentoParcelaCdc();
                                            pgto.setParcelaOperacaoCdc(pcl);

                                            ComponentePagamentoParcelaCdc cpntPgto = new ComponentePagamentoParcelaCdc();
                                            TipoComponenteCdc tipoCpntPgto = new TipoComponenteCdc();

                                            List<PagamentoParcelaCdc> listaPgto = new ArrayList();
                                            if (sisbb.copiar(6, 31, 21).equals("DETALHES DA PRESTACAO")) {

                                                if (!sisbb.copiar(17, 44, 10).equals("")) {

                                                    pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(17, 44, 10), fd1).toString()));
                                                    short nrRec = 1;
                                                    pgto.setNrRecebimento(nrRec);

                                                    if (!sisbb.copiar(8, 42, 12).trim().equals(sisbb.copiar(14, 42, 12).trim())) {

                                                    }

                                                }

                                                if (sisbb.copiar(17, 44, 10).equals("")) {

                                                    pgto.setDtRecebimento(fd2.parse(LocalDate.parse(sisbb.copiar(17, 44, 10), fd1).toString()));
                                                    short nrRec = 1;
                                                    pgto.setNrRecebimento(nrRec);

                                                    if (!sisbb.copiar(8, 42, 12).trim().equals(sisbb.copiar(14, 42, 12).trim())) {

                                                    }

                                                }

                                                List<ComponentePagamentoParcelaCdc> listaCpntPgto = new ArrayList();
                                                for (int linCpntRec = 8; linCpntRec < 12; linCpntRec++) {

                                                    if (!sisbb.copiar(linCpntRec, 42, 12).trim().equals("")) {

                                                        tipoCpntPgto = buscaComponenteLista(sisbb.copiar(linCpntRec, 24, 16).toUpperCase().replaceAll("\\.", "").trim());
                                                        if (tipoCpntPgto != null) {

                                                            cpntPgto.setTipoComponenteCdc(tipoCpntPgto);
                                                            cpntPgto.setVlComponente(Double.parseDouble(sisbb.copiar(linCpntRec, 42, 12).replaceAll("\\.", "").replaceAll(",", ".").trim()));
                                                            cpntPgto.setPagamentoParcelaCdc(pgto);
                                                            listaCpntPgto.add(cpntPgto);
                                                        }
//                                                        
                                                    }

                                                }
                                                pgto.setListComponentePagamentoParcelaCdc(listaCpntPgto);
                                                listaPgto.add(pgto);

                                            }

                                            pcl.setListPagamentoParcelaCdc(listaPgto);
                                            listaParc.add(pcl);

                                        }
                                    } catch (RoboException | ParseException ex) {
                                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    try {
                                        // }

                                        if (sisbb.copiar(6, 31, 21).equals("DETALHES DA PRESTACAO")) {
                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            sisbb.teclarAguardarTroca("@3");
                                        }

                                        if (l == 21) {
                                            sisbb.teclarAguardarTroca("@8");

                                        }

                                    } catch (RoboException ex) {
                                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                });

                            }

                            sisbb.teclarAguardarTroca("@3");
                            sisbb.teclarAguardarTroca("@3");

                        }
                    } catch (RoboException ex) {
                        Logger.getLogger(CapturaExtratoCartaoCredito.class
                                .getName()).log(Level.SEVERE, null, ex);
                    } catch (ParseException ex) {
                        Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (listaParc.size() > 0) {
                        opeCdc.setListParcelaOperacaoCdc(listaParc);
                        try {
                            daoOpe.salvar(opeCdc);
                        } catch (Exception ex) {
                            Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PlanilhaExtratoCdc planilha = new PlanilhaExtratoCdc();
                        try {
                            planilha.criaPlanilha(opeCdc);
                        } catch (Exception ex) {
                            try {
                                throw new Exception(ex);
                            } catch (Exception ex1) {
                                Logger.getLogger(CapturaFin.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                    }

                });
                sisbb.teclarAguardarTroca("@8");

            }

            sisbb.rotinaEncerramento();

        }
        return null;

    }//Fecha função geraExtratos()

    public TipoComponenteCdc buscaComponenteLista(String texto) {
        TipoComponenteCdc tipo = null;

        for (TipoComponenteCdc c : listaCmpt) {
            if (c.getNome().equals(texto)) {
                return c;
            }
        }
        return tipo;
    }

    public void carregaListaComponentesPgto() {

        ComponenteCdcDAO daoCpnt = new ComponenteCdcDAO();

        listaCmpt.clear();
        listaCmpt = daoCpnt.consultarTodos(TipoComponenteCdc.class);
    }

}
