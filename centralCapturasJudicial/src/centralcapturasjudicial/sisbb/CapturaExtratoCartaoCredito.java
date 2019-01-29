/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.CartaoCreditoDAO;
import centralcapturasjudicial.model.dao.FaturaDAO;
import centralcapturasjudicial.model.entity.cartao.CartaoCredito;
import centralcapturasjudicial.model.entity.cartao.ExtratoCartao;
import centralcapturasjudicial.model.entity.cartao.LancamentoCartao;
import centralcapturasjudicial.model.entity.cartao.MesCartao;
import centralcapturasjudicial.poi.PlanilhaExtratoCartao;
import centralcapturasjudicial.poi.PlanilhaExtratoContaCorrente;
import com.jfoenix.controls.JFXTextField;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

/**
 *
 * @author f8940147
 */
public class CapturaExtratoCartaoCredito {

    int cont = 0;
    /**
     * Variáveis
     */
    JanelaSisbb sisbb;
    private List<CartaoCredito> listCartaoCredito;

    public CapturaExtratoCartaoCredito() throws PropertyVetoException {
        //sisbb = new JanelaSisbb();
    }

    /**
     * Função responsável pela validação dos dados informados na tela inicial e
     * chamar a captura da lista de cartões
     *
     * @param inputAgencia
     * @param inputConta
     * @param inputDataInicial
     * @param inputDataFinal
     * @param listaCartoes
     * @throws Throwable
     */
    public void validaCapturaCartao(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputCpf,
            ListView listaCartoes) throws Throwable {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        try {
            capturaListaCartoes(inputAgencia, inputConta, inputCpf, listaCartoes);
            alertSucesso();
        } catch (Exception e) {
            alert.setHeaderText("Problema de execução...");
            alert.setContentText("Execução interrompida!");
            alert.showAndWait();
        }
//        }
    }

    private void alertSucesso() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText("Captura efetuada com sucesso!");
        alertSucesso.setContentText("Extrato disponível para tratamento!");
        alertSucesso.showAndWait();
    }

    private Boolean alertConfirmacao(String texto) {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setTitle("Sucesso");
        alertSucesso.setHeaderText(texto);
        alertSucesso.setContentText("Deseja selecionar outro cartão?");
        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não");
        alertSucesso.getButtonTypes().setAll(buttonSim, buttonNao);
        Optional<ButtonType> result = alertSucesso.showAndWait();
        if (result.get() == buttonSim) {
            return true;
        } else {
            return false;
        }
    }

    private void alertSemLctos() {
        Alert alertSucesso = new Alert(Alert.AlertType.INFORMATION);
        alertSucesso.setHeaderText("Nenhum lançamento encontrado...");
        alertSucesso.setContentText("Planilha não gerada!");
        alertSucesso.showAndWait();
    }

    /**
     * Função que acessa o SISBB e captura uma lista com numero da operação e
     * modalidade dos cartões do cliente. A lista é mostrada na tela através de
     * um ListView.
     *
     * @param inputAgencia
     * @param inputConta
     * @param inputDataInicial
     * @param inputDataFinal
     * @param listaCartoes
     * @throws PropertyVetoException
     * @throws Throwable
     */
    private void capturaListaCartoes(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputCpf,
            ListView<CartaoCredito> listViewCartoes) throws PropertyVetoException, Throwable {

        ObservableList<CartaoCredito> observableListCartoes = FXCollections.observableArrayList();
        Integer operacao;
        String modalidade;
        int linha = 12;

        sisbb = new JanelaSisbb();

        sisbb.setTamanho(1000, 700);

        sisbb.Aplicativo(System.getProperty("user.name"), "CARTAO", true);

        sisbb.aguardarInd(1, 2, "VP00");
        sisbb.colar(21, 19, "11");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "VIP10300");
        sisbb.colar(21, 20, "02");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "VIP23060");
        sisbb.colar(17, 20, "04");
        if (inputCpf.getText().equals("")) {
            sisbb.colar(19, 20, inputAgencia.getText());
            sisbb.colar(19, 31, inputConta.getText());
        } else {
            sisbb.colar(20, 20, inputCpf.getText());
        }
        sisbb.teclarAguardarTroca("@E");
        sisbb.aguardarInd(1, 3, "VIP");

        if (sisbb.copiar(1, 3, 8).equals("VIP10080")) {
            do {
                operacao = Integer.valueOf(sisbb.copiar(linha, 7, 9));
                modalidade = sisbb.copiar(linha, 17, 29).trim();
                CartaoCredito cartao = new CartaoCredito();
                cartao.setOperacao(operacao);
                cartao.setModalidade(modalidade);
                //listCartaoCredito.add(cartao);
                observableListCartoes.add(cartao);
                System.out.println(operacao + " " + modalidade);
                linha++;
            } while (!sisbb.copiar(linha, 17, 29).trim().equals(""));
        } else { // VIP60041
            CartaoCredito cartao = new CartaoCredito();
            modalidade = sisbb.copiar(5, 53, 28).trim();
            sisbb.teclarAguardarTroca("@4");
            sisbb.aguardarInd(1, 3, "VIP60502");
            operacao = Integer.valueOf(sisbb.copiar(4, 34, 12).replace(".", "").trim());
            cartao.setOperacao(operacao);
            cartao.setModalidade(modalidade);
            observableListCartoes.add(cartao);
            System.out.println(operacao + " " + modalidade);
            sisbb.teclarAguardarTroca("@3");
            sisbb.aguardarInd(1, 3, "VIP60041");
        }
        listViewCartoes.setItems(observableListCartoes);
    }

    /**
     * Método responsável pela captura mês a mês das faturas de cartão de
     * crédito de Pessoas Físicas.
     *
     * @param listViewCartoes
     * @return ExtratoCartao fatura
     * @throws RoboException
     * @throws IOException
     */
    public void capturaFaturas(ListView<CartaoCredito> listViewCartoes) throws RoboException, IOException, Exception {

        int linha = 9;
        int totalLinhas = 20;

        boolean cartaoUnico;

        //Carrega cartão selecionado na lista pelo usuário e utiliza como parâmetro na captura do restante da fatura (Extrato de Cartão)
        CartaoCredito cartao = listViewCartoes.getSelectionModel().getSelectedItem();
        String contaOperacao = Integer.toString(cartao.getOperacao());

        //Instancia o extrato de cartão que será salvo (fatura) e o objeto de acesso a dados (dao)
        ExtratoCartao fatura = new ExtratoCartao();
        FaturaDAO dao = new FaturaDAO();
        CartaoCreditoDAO c = new CartaoCreditoDAO();

        try {
            c.removeRegistro(Integer.parseInt(contaOperacao));
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        //Associando Cliente e Cartão à Fatura
        fatura.setCartao(cartao);

        //Listas de meses e lancamentos
        List<MesCartao> listMeses = new ArrayList<MesCartao>();

        if (sisbb.copiar(1, 3, 8).equals("VIP60041")) {
            cartaoUnico = true;
            fatura.setNomeCliente(sisbb.copiar(5, 9, 35).trim());

            //Data e hora atual do sistema
            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            fatura.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));

        } else { // selecionar a operação
            cartaoUnico = false;
            fatura.setNomeCliente(sisbb.copiar(5, 19, 60).trim());
            fatura.setCpfCliente(sisbb.copiar(6, 19, 20).replaceAll("\\D", "").trim());

            //Data e hora atual do sistema
            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            fatura.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));

            IntStream.rangeClosed(linha, totalLinhas)
                    .forEach(i -> {
                        try {
                            if (sisbb.copiar(i, 7, 9).trim().equals(contaOperacao)) {
                                sisbb.colar(i, 4, "x");
                                sisbb.teclarAguardarTroca("@E");
                                sisbb.aguardarInd(1, 3, "VIP60041");
                            }
                        } catch (RoboException ex) {
                            Logger.getLogger(CapturaExtratoCartaoCredito.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        }

        // captura ano atual e meses anteriores
        while (!sisbb.copiar(23, 3, 20).equals("Tecla de funcao PF10")) {
            String tela = sisbb.copiar(1, 3, 8);
            switch (tela) {
                case "VIP60041":
                    while (!sisbb.copiar(23, 4, 5).equals("ltima") && !sisbb.copiar(22, 4, 5).equals("ltima")) {
                        IntStream.rangeClosed(9, 20).forEach(j -> {
                            try {
                                if (!sisbb.copiar(j, 6, 1).equals("") && !sisbb.copiar(j, 6, 1).equals("A")) {
                                    if (fatura.getNomeCliente() == null) {
                                        fatura.setNomeCliente(sisbb.copiar(5, 9, 35).trim());

                                        //Data e hora atual do sistema
                                        LocalDateTime agora = LocalDateTime.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
                                        fatura.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));

                                    }
                                    if (fatura.getCpfCliente() == null) {
                                        sisbb.teclarAguardarTroca("@9");
                                        sisbb.aguardarInd(1, 3, "VIP");
                                        if (sisbb.copiar(1, 3, 8).equals("VIP67171")) {
                                            fatura.setCpfCliente(sisbb.copiar(6, 61, 20).trim());
                                            sisbb.teclarAguardarTroca("@3");
                                        } else if (!sisbb.copiar(1, 3, 8).equals("VIP60041")) {
                                            sisbb.teclarAguardarTroca("@3");
                                        }
                                    }
                                    sisbb.colar(j, 3, "x");
                                    sisbb.teclarAguardarTroca("@E");
                                    sisbb.aguardarInd(1, 3, "VIP60631");
                                    MesCartao mes = new MesCartao();
                                    mes.setTxJuros(Double.parseDouble(sisbb.copiar(5, 56, 10).trim().replace(",", ".")));
                                    mes.setDataVencimento(sisbb.copiar(6, 12, 10));
                                    mes.setDataFaturamento(sisbb.copiar(6, 56, 10).replace("/", "."));
                                    mes.setSaldoReal(Double.parseDouble(sisbb.copiar(16, 21, 16).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(16, 60, 18).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 16).trim().replace(".", "").replace(",", ".")));
                                    sisbb.teclarAguardarTroca("@8");
                                    sisbb.aguardarInd(1, 3, "VIP60051");
                                    mes.setListLancamentoCartao(capturaLancamentos(sisbb, mes));
                                    mes.setExtrato(fatura);
                                    listMeses.add(mes);
                                    sisbb.teclarAguardarTroca("@3");
                                }
                            } catch (RoboException ex) {
                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        sisbb.teclarAguardarTroca("@8");
                    }
                    break;
                case "VIP14661":
                    while (!sisbb.copiar(23, 4, 5).equals("ltima") && !sisbb.copiar(22, 4, 5).equals("ltima")) {
                        IntStream.rangeClosed(10, 21).forEach(j -> {
                            try {
                                if (!sisbb.copiar(j, 6, 1).equals("")) {
                                    sisbb.colar(j, 3, "x");
                                    sisbb.teclarAguardarTroca("@E");
                                    sisbb.aguardarInd(1, 3, "VIP14691");
                                    MesCartao mes = new MesCartao();
                                    mes.setTxJuros(Double.parseDouble(sisbb.copiar(6, 56, 10).trim().replace(",", ".")));
                                    mes.setDataVencimento(sisbb.copiar(7, 12, 10));
                                    mes.setDataFaturamento(sisbb.copiar(7, 56, 10).replace("/", "."));
                                    mes.setSaldoReal(Double.parseDouble(sisbb.copiar(17, 21, 16).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(17, 60, 18).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 16).trim().replace(".", "").replace(",", ".")));
                                    sisbb.teclarAguardarTroca("@8");
                                    sisbb.aguardarInd(1, 3, "VIP14671");
                                    mes.setListLancamentoCartao(capturaLancamentos(sisbb, mes));
                                    mes.setExtrato(fatura);
                                    listMeses.add(mes);
                                    sisbb.teclarAguardarTroca("@3");
                                }
                            } catch (RoboException ex) {
                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        sisbb.teclarAguardarTroca("@8");
                    }
                    break;
                case "VIPM2575":
                    

                    
                    while (!sisbb.copiar(23, 4, 5).equals("ltima") && !sisbb.copiar(22, 4, 5).equals("ltima")) {
                        IntStream.rangeClosed(10, 21).forEach(j -> {
                            try {
                                if (!sisbb.copiar(j, 6, 1).equals("") && j <= 21) {
                                    sisbb.colar(j, 3, "x");
                                    sisbb.teclarAguardarTroca("@E");
                                    sisbb.aguardarInd(1, 3, "VIPM2578");//Acho que o erro está aqui
                                    MesCartao mes = new MesCartao();
                                    mes.setDataVencimento(sisbb.copiar(7, 12, 10));
                                    mes.setDataFaturamento(sisbb.copiar(7, 71, 10).replace("/", "."));
                                    if (!sisbb.copiar(8, 71, 10).trim().equals("")) {
                                        mes.setTxJuros(Double.parseDouble(sisbb.copiar(8, 71, 10).trim().replace(",", ".")));
                                    }
                                    mes.setSaldoReal(Double.parseDouble(sisbb.copiar(18, 21, 17).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(18, 61, 18).trim().replace(".", "").replace(",", ".")));
                                    mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 17).trim().replace(".", "").replace(",", ".")));
                                    sisbb.teclarAguardarTroca("@8");
                                    

//------------------------------------------------------------Erro -> VIPN2604- Nenhum lançamento recuperado. - OK!------------------------------------------------------------------------------------
                                    if (sisbb.copiar(23, 3, 38).equals("VIPN2604- Nenhum lançamento recuperado")) {
                                        sisbb.teclarAguardarTroca("@3");
                                        j = j + 1;
                                        return;

                                    }

                                    sisbb.aguardarInd(1, 3, "VIPM2604");
                                    mes.setListLancamentoCartao(capturaLancamentos(sisbb, mes));
                                    mes.setExtrato(fatura);
                                    listMeses.add(mes);
                                    sisbb.teclarAguardarTroca("@3");
                                    if (sisbb.copiar(1, 3, 8).equals("VIPM2578")) {
                                        sisbb.teclarAguardarTroca("@3");
                                    }
                                }
                            } catch (RoboException ex) {
                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        sisbb.teclarAguardarTroca("@8");
                    }
                    break;
                default:
                    break;
            }

            sisbb.teclarAguardarTroca("@10");
        }

        fatura.setListMesCartao(listMeses);
        sisbb.teclarAguardarTroca("@3");
        if (cartaoUnico == true) {
            while (!sisbb.copiar(1, 3, 8).equals("VIP60041")) {
                sisbb.teclarAguardarTroca("@3");
            }
        } else {
            while (!sisbb.copiar(1, 3, 8).equals("VIP10080")) {
                sisbb.teclarAguardarTroca("@3");
            }
        }

        try {
            c.save(cartao);
            dao.salvar(fatura);
            if (listMeses.size() > 0) {
                PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
                planilha.criaPlanilha(fatura);
                if (alertConfirmacao("Captura efetuada com sucesso! Extrato disponível para tratamento.") == false) {
                    sisbb.rotinaEncerramento();
                }
            } else {
                if (alertConfirmacao("Cartão não possui fatura(s) para captura!") == false) {
                    sisbb.rotinaEncerramento();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(CapturaExtratoCartaoCredito.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<LancamentoCartao> capturaLancamentos(JanelaSisbb sisbb, MesCartao mes) throws RoboException {

        List<LancamentoCartao> listLancamentos = new ArrayList<LancamentoCartao>();

        int c = 0;
        cont = 0;

        //while (!sisbb.copiar(23, 4, 5).equals("ltima")) {//fazer a página ser igual a última ou igual a 50 páginas - verificar
        if (c < 50 || !sisbb.copiar(23, 4, 5).equals("ltima")) {

            while ((c <= 50)) {

                cont++;
                c = cont;

                IntStream.rangeClosed(11, 21).forEach(l -> {
                    try {
                        //Realiza captura de atributos de Lançamentos
                        if (!sisbb.copiar(l, 5, 1).equals("") && !sisbb.copiar(l, 5, 1).equals("-")) {
                            LancamentoCartao lanc = new LancamentoCartao();
                            lanc.setDataTransacao(sisbb.copiar(l, 5, 10));
                            lanc.setNrCartao(Integer.parseInt(sisbb.copiar(l, 16, 4)));
                            lanc.setDescricao(sisbb.copiar(l, 21, 40).trim());
                            if (sisbb.copiar(l, 21, 4).equals("PGTO") || sisbb.copiar(l, 74, 1).equals("-")) {
                                lanc.setValor(Double.parseDouble(sisbb.copiar(l, 61, 13).replace("-", "").replace(".", "")
                                        .replace(",", ".")
                                        .trim()));
                            } else {
                                lanc.setValor(Double.parseDouble(sisbb.copiar(l, 61, 13).replace(".", "")//.replace("-", "")
                                        .replace(",", ".")
                                        .trim()) * (-1));
                            }
                            lanc.setMoeda(sisbb.copiar(l, 77, 4).trim());
                            lanc.setMesCartao(mes);
                            listLancamentos.add(lanc);
                        }
                    } catch (RoboException ex) {
                        Logger.getLogger(CapturaExtratoCartaoCredito.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                });
                sisbb.teclarAguardarTroca("@8");

                if (sisbb.copiar(23, 4, 5).equals("ltima")) {

                    c = 51;

                }

            }
        }
        return listLancamentos;
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------

    public void capturaListaCartoesPJAntigos(JFXTextField inputAgencia, JFXTextField inputConta, JFXTextField inputCnpj,
            ListView<CartaoCredito> listViewCartoes) throws PropertyVetoException, Throwable {

        ObservableList<CartaoCredito> observableListCartoes = FXCollections.observableArrayList();
        Integer operacao;
        String modalidade;

        int linha = 11;

        sisbb = new JanelaSisbb();

        sisbb.setTamanho(1000, 700);

        sisbb.Aplicativo(System.getProperty("user.name"), "CARTAO", true);

        sisbb.aguardarInd(1, 2, "VP00");
        sisbb.colar(21, 19, "12");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "VIP1312A");
        sisbb.colar(21, 20, "01");
        sisbb.teclarAguardarTroca("@E");

        sisbb.aguardarInd(1, 3, "VIP0842A");
        sisbb.colar(14, 20, "12");

        if (inputCnpj.getText().equals("")) {
            sisbb.colar(17, 20, inputAgencia.getText());//Pegando dados do Jframe
            sisbb.colar(17, 27, inputConta.getText());//Pegando dados do Jframe
        } else {
            sisbb.colar(15, 20, inputCnpj.getText());//Pegando dados do Jframe
        }
        sisbb.teclarAguardarTroca("@E");

        if (sisbb.copiar(1, 3, 8).equals("VIP10084")) {

            do {
                operacao = Integer.valueOf(sisbb.copiar(linha, 9, 8));
                modalidade = sisbb.copiar(linha, 18, 39).trim();
                CartaoCredito cartao = new CartaoCredito();
                cartao.setOperacao(operacao);
                cartao.setModalidade(modalidade);
                //listCartaoCredito.add(cartao);
                observableListCartoes.add(cartao);
                System.out.println(operacao + " " + modalidade);
                linha++;
            } while (!sisbb.copiar(linha, 19, 29).trim().equals(""));
        } else {
            if (sisbb.copiar(1, 3, 8).equals("VIP65291")) {
                sisbb.colar(21, 10, "21");
                sisbb.teclarAguardarTroca("@E");
                sisbb.aguardarInd(1, 3, "VIP60041");
                sisbb.teclarAguardarTroca("@4");
                sisbb.aguardarInd(1, 3, "VIP60505");
                operacao = Integer.valueOf(sisbb.copiar(9, 73, 8));
                modalidade = sisbb.copiar(6, 22, 30).trim() + " " + sisbb.copiar(6, 71, 10).trim();
                CartaoCredito cartao = new CartaoCredito();
                cartao.setOperacao(operacao);
                cartao.setModalidade(modalidade);
                observableListCartoes.add(cartao);
                sisbb.teclarAguardarTroca("@3");
                sisbb.aguardarInd(1, 3, "VIP60041");
                sisbb.teclarAguardarTroca("@3");
                sisbb.aguardarInd(1, 3, "VIP65291");
                //System.out.println(operacao + " " + modalidade);
            }
        }

        //observableListCartoes = FXCollections.observableArrayList(listCartaoCredito);
        //listViewCartoes.getItems().addAll(observableListCartoes);
        listViewCartoes.setItems(observableListCartoes);                                //DANDO ERRO AQUI!!! LISTA
//        listaCartoes.setCellFactory(CheckBoxListCell.forListView(cartoes));

        //sisbb.rotinaEncerramento();
    }

    /**
     *
     * Método responsável pela captura mês a mês das faturas de cartão de
     * crédito PJ antigos. Isto é, que foram pedidos pelo menos no dia anterior.
     *
     * @param listViewCartoes
     * @return
     * @throws RoboException
     */
    public ExtratoCartao capturaFaturaPJAntigos(ListView<CartaoCredito> listViewCartoes) throws RoboException, Exception {////////////////////////////////////////testar aki com ponto de para

        int linha = 11;
        int totalLinhas = 20;

        //Instancia o extrato de cartão que será salvo (fatura) e o objeto de acesso a dados (dao)
        ExtratoCartao fatura = new ExtratoCartao();
        FaturaDAO dao = new FaturaDAO();

        //Carrega cartão selecionado na lista pelo usuário e utiliza como parâmetro na captura do restante da fatura (Extrato de Cartão)
        CartaoCredito cartao = listViewCartoes.getSelectionModel().getSelectedItem();
        if (cartao == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Problema na captura do extrato...");
            alert.setContentText("Selecione o cartão!");
            alert.showAndWait();
            return fatura;
        }

        //Captura dados de cliente
        //sisbb.aguardarInd(1, 3, "VIP10084");
        sisbb.teclarAguardarTroca("@3");
        sisbb.aguardarInd(1, 3, "VIP0842A");
        sisbb.colar(14, 20, "11");
        sisbb.teclarAguardarTroca("@E");
        sisbb.aguardarInd(1, 3, "VIP08430");
        fatura.setNomeCliente(sisbb.copiar(5, 20, 60).trim());
        fatura.setCpfCliente(sisbb.copiar(6, 20, 30).replaceAll("\\D", "").trim());
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------        
        //pega chave inserida no sistema
        fatura.setChaveFunci(System.getProperty("user.name"));
        //Data e hora atual do sistema
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        fatura.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Volta para tela de cartões
        sisbb.teclarAguardarTroca("@3");
        sisbb.aguardarInd(1, 3, "VIP0842A");
        sisbb.colar(14, 20, "12");
        sisbb.teclarAguardarTroca("@E");

        String contaOperacao = Integer.toString(cartao.getOperacao());

        CartaoCreditoDAO c = new CartaoCreditoDAO();
        try {
            c.removeRegistro(Integer.parseInt(contaOperacao));
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //Associando Cliente e Cartão à Fatura
        fatura.setCartao(cartao);

        //Listas de meses e lancamentos
        List<MesCartao> listMeses = new ArrayList<MesCartao>();

        //Loop da captura dos lançamentos
        if (sisbb.copiar(1, 3, 8).equals("VIP10084")) {
            IntStream.rangeClosed(linha, totalLinhas)
                    .forEach(i -> {
                        try {
                            if (sisbb.copiar(i, 9, 8).trim().equals(contaOperacao)) {
                                sisbb.colar(i, 4, "x");
                                sisbb.teclarAguardarTroca("@E");
                                sisbb.aguardarInd(1, 3, "VIP65291");
                            }
                        } catch (RoboException ex) {
                            Logger.getLogger(CapturaExtratoCartaoCredito.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        }

//------------------------------------------------Primeira tela-----------------------------------------------------------------------------------------------------
        if (sisbb.copiar(1, 3, 8).equals("VIP65291")) {
            sisbb.colar(21, 10, "21");
            sisbb.teclarAguardarTroca("@E");
            sisbb.aguardarInd(1, 3, "VIP60041");//capturar cartões aqui!
//            sisbb.teclarAguardarTroca("@10");

        }//tela VIP60041
        if (!sisbb.copiar(9, 6, 1).trim().equals("") && sisbb.copiar(1, 3, 8).equals("VIP60041")) {//LINHA 776 verifica a linha 10 se está vazia
            sisbb.teclarAguardarTroca("@10");

            if (sisbb.copiar(10, 6, 1).equals("") && sisbb.copiar(1, 3, 8).equals("VIP14661")) {

                sisbb.teclarAguardarTroca("@10");

                while (!sisbb.copiar(23, 4, 5).equals("ltima") && !sisbb.copiar(22, 4, 5).equals("ltima")) {
                    IntStream.rangeClosed(10, 21).forEach(j -> {
                        try {
                            if (!sisbb.copiar(j, 6, 1).equals("")) {
                                cont = 0;
                                sisbb.colar(j, 3, "x");
                                sisbb.teclarAguardarTroca("@E");
                                sisbb.aguardarInd(1, 3, "VIPM2578");
                                MesCartao mes = new MesCartao();
                                mes.setDataVencimento(sisbb.copiar(7, 12, 10));
                                mes.setDataFaturamento(sisbb.copiar(7, 71, 10).replace("/", "."));
                                if (!sisbb.copiar(8, 71, 10).trim().equals("")) {
                                    mes.setTxJuros(Double.parseDouble(sisbb.copiar(8, 71, 10).trim().replace(",", ".")));
                                }
                                mes.setSaldoReal(Double.parseDouble(sisbb.copiar(18, 21, 17).trim().replace(".", "").replace(",", ".")));
                                mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(18, 61, 18).trim().replace(".", "").replace(",", ".")));
                                mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 17).trim().replace(".", "").replace(",", ".")));
                                sisbb.teclarAguardarTroca("@8");
                                sisbb.aguardarInd(1, 3, "VIPM2604");
                                mes.setListLancamentoCartao(capturaLancamentos(sisbb, mes));
                                mes.setExtrato(fatura);
                                listMeses.add(mes);
                                sisbb.teclarAguardarTroca("@3");
                                if (sisbb.copiar(1, 3, 8).equals("VIPM2578")) {
                                    sisbb.teclarAguardarTroca("@3");
                                }
                            }
                        } catch (RoboException ex) {
                            Logger.getLogger(CapturaExtratoCartaoCredito.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    sisbb.teclarAguardarTroca("@8");
                }

            }
            if (sisbb.copiar(1, 3, 8).equals("VIPM2575")) {
                sisbb.teclarAguardarTroca("@3");
                sisbb.teclarAguardarTroca("@3");
            }

            if (sisbb.copiar(10, 6, 1).equals("VIP16521")) {
                sisbb.teclarAguardarTroca("@3");
                sisbb.teclarAguardarTroca("@3");
            } else if (!sisbb.copiar(10, 6, 1).equals("") && sisbb.copiar(1, 3, 8).equals("VIP14661")) {

                while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                    IntStream.rangeClosed(10, 21).forEach(new IntConsumer() {
                        @Override
                        public void accept(int j) {
                            try {
                                if (!sisbb.copiar(j, 5, 10).equals("")) {
                                    sisbb.colar(j, 3, "x");
                                    sisbb.teclarAguardarTroca("@E");
                                    sisbb.aguardarInd(1, 3, "VIP14691");

                                    //Realiza captura de atributos do mês
                                    MesCartao mes = new MesCartao();
                                    mes.setTxJuros(Double.parseDouble(sisbb.copiar(6, 56, 6).trim().replace(",", ".")));
                                    mes.setDataVencimento(sisbb.copiar(7, 12, 10));
                                    mes.setDataFaturamento(sisbb.copiar(7, 56, 10));//.replace("/", "."));
                                    mes.setSaldoReal(Double.parseDouble(sisbb.copiar(17, 17, 20).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(14, 57, 21).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 16).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    List<LancamentoCartao> listLancamentos = new ArrayList<LancamentoCartao>();
                                    sisbb.teclarAguardarTroca("@8");
                                    sisbb.aguardarInd(1, 3, "VIP14671");
                                    while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                                        IntStream.rangeClosed(11, 20).forEach((int l) -> {
                                            try {
                                                //Realiza captura de atributos de Lançamentos
                                                if (!sisbb.copiar(l, 5, 10).equals("")) {
                                                    LancamentoCartao lanc = new LancamentoCartao();
                                                    lanc.setDataTransacao(sisbb.copiar(l, 5, 10));
                                                    lanc.setNrCartao(Integer.parseInt(sisbb.copiar(l, 16, 4)));
                                                    lanc.setDescricao(sisbb.copiar(l, 21, 39).trim());

                                                    if (sisbb.copiar(l, 74, 1).equals("-")) {
                                                        lanc.setValor(Double.parseDouble(sisbb.copiar(l, 61, 13)
                                                                .replaceAll("\\.", "")
                                                                .replace(",", ".")
                                                                .trim()));
                                                    } else {
                                                        lanc.setValor(Double.parseDouble(sisbb.copiar(l, 60, 15)//.replace("-", "")
                                                                .replaceAll("\\.", "")
                                                                .replace(",", ".")
                                                                .trim()) * (-1));
                                                    }
                                                    lanc.setMoeda(sisbb.copiar(l, 79, 2));
                                                    lanc.setMesCartao(mes);
                                                    listLancamentos.add(lanc);

                                                }
                                            } catch (RoboException ex) {
                                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                                        .getName()).log(Level.SEVERE, null, ex);
                                            }
                                        });

                                        sisbb.teclarAguardarTroca("@8");
                                    }
                                    mes.setListLancamentoCartao(listLancamentos);
                                    mes.setExtrato(fatura);
                                    listMeses.add(mes);
                                    sisbb.teclarAguardarTroca("@3");
                                    sisbb.aguardarInd(1, 3, "VIP14661");
//                                    sisbb.teclarAguardarTroca("@3");
//                                    sisbb.aguardarInd(1, 3, "VIPM2575");

                                }
                            } catch (RoboException ex) {
                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    sisbb.teclarAguardarTroca("@8");
                }

            }

            fatura.setListMesCartao(listMeses);
            // sisbb.teclarAguardarTroca("@3");

        }

        if (sisbb.copiar(23, 4, 5).equals("ltima") && sisbb.copiar(1, 3, 8).equals("VIP14661")) {//tela VIP14661 -> Verifica se chegou na última página

            sisbb.teclarAguardarTroca("@10");

            if (sisbb.copiar(1, 3, 8).equals("VIPM2575")) {
                while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                    IntStream.rangeClosed(10, 21).forEach(new IntConsumer() {
                        @Override
                        public void accept(int j) {
                            try {
                                if (!sisbb.copiar(j, 7, 1).equals("X") && (!sisbb.copiar(j, 7, 1).equals(""))) {
                                    sisbb.colar(j, 3, "x");
                                    sisbb.teclarAguardarTroca("@E");
                                    //sisbb.aguardarInd(1, 3, "VIMP2578");

                                    //Realiza captura de atributos do mês
                                    MesCartao mes = new MesCartao();
                                    mes.setTxJuros(Double.parseDouble(sisbb.copiar(8, 71, 10).trim().replace(",", ".")));
                                    mes.setDataVencimento(sisbb.copiar(7, 12, 10));
                                    mes.setDataFaturamento(sisbb.copiar(7, 71, 10));//.replace("/", "."));
                                    mes.setSaldoReal(Double.parseDouble(sisbb.copiar(18, 18, 20).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(15, 59, 20).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 22, 16).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                    List<LancamentoCartao> listLancamentos = new ArrayList<LancamentoCartao>();
                                    sisbb.teclarAguardarTroca("@8");
                                    //sisbb.aguardarInd(1, 3, "VIP2604");
                                    while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                                        IntStream.rangeClosed(12, 21).forEach((int l) -> {
                                            try {
                                                //Realiza captura de atributos de Lançamentos
                                                if (!sisbb.copiar(l, 5, 10).equals("")) {
                                                    LancamentoCartao lanc = new LancamentoCartao();
                                                    lanc.setDataTransacao(sisbb.copiar(l, 5, 10));
                                                    lanc.setNrCartao(Integer.parseInt(sisbb.copiar(l, 16, 4)));
                                                    lanc.setDescricao(sisbb.copiar(l, 21, 40).trim());

                                                    if (sisbb.copiar(l, 74, 1).equals("-")) {
                                                        lanc.setValor(Double.parseDouble(sisbb.copiar(l, 60, 14)
                                                                .replaceAll("\\.", "")
                                                                .replace(",", ".")
                                                                .trim()));
                                                    } else {
                                                        lanc.setValor(Double.parseDouble(sisbb.copiar(l, 61, 15)//.replace("-", "")
                                                                .replaceAll("\\.", "")
                                                                .replace(",", ".")
                                                                .trim()) * (-1));
                                                    }
                                                    lanc.setMoeda(sisbb.copiar(l, 79, 2));
                                                    lanc.setMesCartao(mes);
                                                    listLancamentos.add(lanc);

                                                }
                                            } catch (RoboException ex) {
                                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                                        .getName()).log(Level.SEVERE, null, ex);
                                            }
                                        });

                                        sisbb.teclarAguardarTroca("@8");
                                    }
                                    mes.setListLancamentoCartao(listLancamentos);
                                    mes.setExtrato(fatura);
                                    listMeses.add(mes);
                                    sisbb.teclarAguardarTroca("@3");
                                    sisbb.teclarAguardarTroca("@3");
                                    sisbb.aguardarInd(1, 3, "VIPM2575");
//                                sisbb.teclarAguardarTroca("@3");
//                                sisbb.aguardarInd(1, 3, "VIPM2575");

                                }
                            } catch (RoboException ex) {
                                Logger.getLogger(CapturaExtratoCartaoCredito.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    });
                    sisbb.teclarAguardarTroca("@8");
                }

                if (sisbb.copiar(23, 4, 5).equals("ltima") && sisbb.copiar(1, 3, 8).equals("VIPM2575")) {

                    sisbb.teclarAguardarTroca("@3");
                    sisbb.teclarAguardarTroca("@3");

                    if (!sisbb.copiar(10, 6, 1).trim().equals("") && sisbb.copiar(1, 3, 8).equals("VIP60041")) {//tela VIP60041 -> A FATURAR -> Caso tenha outros registros na tela A FATURAR esse else EXECUTA!!!

                        while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                            IntStream.rangeClosed(10, 21).forEach(new IntConsumer() {
                                @Override
                                public void accept(int j) {
                                    try {
                                        if (!sisbb.copiar(j, 7, 1).equals("X") && (!sisbb.copiar(j, 7, 1).equals(""))) {
                                            sisbb.colar(j, 3, "x");
                                            sisbb.teclarAguardarTroca("@E");
                                            sisbb.aguardarInd(1, 3, "VIP14691");

                                            //Realiza captura de atributos do mês
                                            MesCartao mes = new MesCartao();
                                            mes.setTxJuros(Double.parseDouble(sisbb.copiar(5, 56, 6).trim().replace(",", ".")));
                                            mes.setDataVencimento(sisbb.copiar(6, 12, 10));
                                            mes.setDataFaturamento(sisbb.copiar(6, 56, 10));//.replace("/", "."));
                                            mes.setSaldoReal(Double.parseDouble(sisbb.copiar(16, 18, 20).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                            mes.setSaldoDolar(Double.parseDouble(sisbb.copiar(13, 57, 21).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                            mes.setSaldoTotal(Double.parseDouble(sisbb.copiar(20, 21, 16).trim().replaceAll("\\.", "").replaceAll(",", ".")));
                                            List<LancamentoCartao> listLancamentos = new ArrayList<LancamentoCartao>();
                                            sisbb.teclarAguardarTroca("@8");
                                            sisbb.aguardarInd(1, 3, "VIP60051");
                                            while (!sisbb.copiar(23, 4, 5).equals("ltima")) {
                                                IntStream.rangeClosed(11, 20).forEach((int l) -> {
                                                    try {
                                                        //Realiza captura de atributos de Lançamentos
                                                        if (!sisbb.copiar(l, 5, 10).equals("")) {
                                                            LancamentoCartao lanc = new LancamentoCartao();
                                                            lanc.setDataTransacao(sisbb.copiar(l, 5, 10));
                                                            lanc.setNrCartao(Integer.parseInt(sisbb.copiar(l, 16, 4)));
                                                            lanc.setDescricao(sisbb.copiar(l, 21, 39).trim());

                                                            if (sisbb.copiar(l, 74, 1).equals("-")) {
                                                                lanc.setValor(Double.parseDouble(sisbb.copiar(l, 60, 14)
                                                                        .replaceAll("\\.", "")
                                                                        .replace(",", ".")
                                                                        .trim()));
                                                            } else {
                                                                lanc.setValor(Double.parseDouble(sisbb.copiar(l, 60, 15)//.replace("-", "")
                                                                        .replaceAll("\\.", "")
                                                                        .replace(",", ".")
                                                                        .trim()) * (-1));
                                                            }
                                                            lanc.setMoeda(sisbb.copiar(l, 79, 2));
                                                            lanc.setMesCartao(mes);
                                                            listLancamentos.add(lanc);

                                                        }
                                                    } catch (RoboException ex) {
                                                        Logger.getLogger(CapturaExtratoCartaoCredito.class
                                                                .getName()).log(Level.SEVERE, null, ex);
                                                    }
                                                });

                                                sisbb.teclarAguardarTroca("@8");
                                            }
                                            mes.setListLancamentoCartao(listLancamentos);
                                            mes.setExtrato(fatura);
                                            listMeses.add(mes);
                                            sisbb.teclarAguardarTroca("@3");
                                            sisbb.aguardarInd(1, 3, "VIP60041");
//                                sisbb.teclarAguardarTroca("@3");
//                                sisbb.aguardarInd(1, 3, "VIPM2575");

                                        }
                                    } catch (RoboException ex) {
                                        Logger.getLogger(CapturaExtratoCartaoCredito.class
                                                .getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            });
                            sisbb.teclarAguardarTroca("@8");
                        }
                        fatura.setListMesCartao(listMeses);

                        try {
                            if (listMeses.size() > 0) {
                                c.save(cartao);
                                dao.salvar(fatura);
                                PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
                                planilha.criaPlanilha(fatura);
                                if (alertConfirmacao("Captura efetuada com sucesso! Extrato disponível para tratamento.") == false) {
                                    sisbb.rotinaEncerramento();
                                }
                            } else {
                                if (alertConfirmacao("Cartão não possui fatura(s) para captura!") == false) {
                                    sisbb.rotinaEncerramento();
                                }
                            }

                        } catch (Exception ex) {
                            Logger.getLogger(CapturaExtratoCartaoCredito.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

            }
            fatura.setListMesCartao(listMeses);

            try {
                if (listMeses.size() > 0) {
                    c.save(cartao);
                    dao.salvar(fatura);
                    PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
                    planilha.criaPlanilha(fatura);
                    if (alertConfirmacao("Captura efetuada com sucesso! Extrato disponível para tratamento.") == false) {
                        sisbb.rotinaEncerramento();
                    }
                } else {
                    if (alertConfirmacao("Cartão não possui fatura(s) para captura!") == false) {
                        sisbb.rotinaEncerramento();
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CapturaExtratoCartaoCredito.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (sisbb.copiar(10, 6, 1).trim().equals("")) {

            try {
                if (listMeses.size() > 0) {
                    c.save(cartao);
                    dao.salvar(fatura);
                    PlanilhaExtratoCartao planilha = new PlanilhaExtratoCartao();
                    planilha.criaPlanilha(fatura);
                    if (alertConfirmacao("Captura efetuada com sucesso! Extrato disponível para tratamento.") == false) {
                        sisbb.rotinaEncerramento();
                    }
                } else {
                    if (alertConfirmacao("Cartão não possui fatura(s) para captura!") == false) {
                        sisbb.rotinaEncerramento();
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CapturaExtratoCartaoCredito.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (listMeses.size() == 0) {
            sisbb.teclarAguardarTroca("@3");
            sisbb.teclarAguardarTroca("@8");
        }
        //sisbb.rotinaEncerramento();

        return fatura;
    }

}
