/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.sisbb;

import br.com.bb.jibm3270.RoboException;
import centralcapturasjudicial.model.dao.LeasingDAO;
import centralcapturasjudicial.model.entity.leasing.OperacaoLeasing;
import centralcapturasjudicial.model.entity.leasing.LancamentoLeasing;
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
import sun.security.krb5.internal.rcache.DflCache;

/**
 *
 * @author f3295813
 */
public class CapturaLeasing {

    SimpleDateFormat fd = new SimpleDateFormat("dd.MM.yyyy");

    public OperacaoLeasing CapturaLeasing(JanelaSisbb sisbb, JFXTextField inputOperacao) throws PropertyVetoException, Throwable { // pega os dados principais da operação

        OperacaoLeasing oprLeasing = new OperacaoLeasing();
        LeasingDAO dao = new LeasingDAO();

        String operacao = inputOperacao.getText();

        sisbb.Aplicativo(System.getProperty("user.name"), "LSG", true); //Verificar qual aplicativo o funcionário vai querer usar.

        sisbb.aguardarInd(1, 3, "LSGM0000");//Primeira tela do leasing

        sisbb.colar(21, 20, "31");
        sisbb.teclarAguardarTroca("@E");
        sisbb.aguardarInd(1, 3, "LSGM3100");

        sisbb.colar(21, 20, operacao);
        sisbb.colar(17, 20, "08");
        sisbb.teclarAguardarTroca("@E");

        if(sisbb.copiar(1, 3, 8).equals("LSGMD330")){
            sisbb.colar(21, 42, "02");
            sisbb.teclarAguardarTroca("@E");
        }

        sisbb.aguardarInd(1, 3, "LSGM3180");
        sisbb.teclarAguardarTroca("@11");
        sisbb.aguardarInd(1, 3, "LSGM3181");

        Long mci = Long.valueOf(sisbb.copiar(4, 13, 11).replace(".", ""));
        String nomeCLiente = sisbb.copiar(4, 25, 55);
        String cpfCnpj = sisbb.copiar(5, 13, 24);
        Long contrato = Long.valueOf(operacao);
        String custoArrendamento = sisbb.copiar(6, 26, 25);
        String indexador = sisbb.copiar(6, 63, 18);
        int diaVencimento = Integer.valueOf(sisbb.copiar(7, 45, 2));
        String prazo = sisbb.copiar(7, 63, 18);
        Date assinatura = fd.parse(sisbb.copiar(8, 56, 10));
        Date vencimento = fd.parse(sisbb.copiar(9, 56, 10));
        Double valorBem = Double.valueOf(sisbb.copiar(10, 45, 22).replace(".","").replace(",","."));

        oprLeasing.setNrMciCliente(mci);
        oprLeasing.setTxNomeCliente(nomeCLiente);
        oprLeasing.setTxCpfCnpjCliente(cpfCnpj);
        oprLeasing.setNrContrato(contrato);
        oprLeasing.setTxCustoArrendamento(custoArrendamento);
        oprLeasing.setTxIndexador(indexador);
        oprLeasing.setNrDiaVencimentoParcelaMensal(diaVencimento);
        oprLeasing.setTxPrazo(prazo);
        oprLeasing.setDtAssinaturaTra(assinatura);
        oprLeasing.setDtVencimentoPrimeiraParcela(vencimento);
        oprLeasing.setVlBem(valorBem);
        
        sisbb.teclarAguardarTroca("@3");
        sisbb.aguardarInd(1, 3, "LSGM3180");
        
        return oprLeasing;
    }
    
    public OperacaoLeasing CapturaLancamentosLeasing(JanelaSisbb sisbb, OperacaoLeasing oprLeasing) throws RoboException, InterruptedException, ParseException{
        
        List<LancamentoLeasing> listLctoLsg = new ArrayList<>();
        Date dtLancamento;
        String historico;
        Double vlLancamento;
        Double saldoArrendamento;

        while(!sisbb.copiar(23, 4, 5).equals("ltima")) {
            for(int i=13; i<22 ;i++) {
                if(sisbb.copiar(i, 3, 10).equals("")) {
                    break;
                }
                
                LancamentoLeasing lcto = new LancamentoLeasing();
                
                dtLancamento = fd.parse(sisbb.copiar(i, 3, 10));
                historico = sisbb.copiar(i, 14, 35);
                vlLancamento = Double.valueOf(sisbb.copiar(i, 50, 15).replace(".","").replace(",","."));
                saldoArrendamento = Double.valueOf(sisbb.copiar(i, 66, 15).replace(".","").replace(",","."));
                
                lcto.setOperacaoLeasing(oprLeasing);
                lcto.setDtLancamento(dtLancamento);
                lcto.setTxHistorico(historico);
                lcto.setVlLancamento(vlLancamento);
                lcto.setVlSaldoArrendamento(saldoArrendamento);

                listLctoLsg.add(lcto);
            }
            sisbb.teclarAguardarTroca("@8");
        }
        
        oprLeasing.setListLancamentoLeasing(listLctoLsg);

        return oprLeasing;
    }

}
