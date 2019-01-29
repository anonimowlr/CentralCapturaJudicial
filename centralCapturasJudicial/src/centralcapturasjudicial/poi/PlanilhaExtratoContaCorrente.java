/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.poi;

import centralcapturasjudicial.model.entity.contacorrente.ContaCorrente;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author f8940147
 */
////pega chave inserida no sistema
//    opeCdc.setChaveFunci(System.getProperty("user.name")); 
////Data e hora atual do sistema
//    LocalDateTime agora = LocalDateTime.now();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
//    opeCdc.setHor_inc_func(LocalDateTime.parse(agora.format(formatter), formatter));

public class PlanilhaExtratoContaCorrente {

    private static String fileName;
    DateTimeFormatter fd = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 
    SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");

    public void criaPlanilha(ContaCorrente cc, String dataInicial, String dataFinal) throws IOException {

        //Criação da planilha
        Workbook workbook = new HSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheetPrest = workbook.createSheet("Prestação de Contas");
        Sheet sheetRevis = workbook.createSheet("Revisional");

        //Estilo da fonte do cabeçalho
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        
        //Estilo da fonte de subtítulo
        Font subFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);        

        //Estilo da célula
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Criação da Linha de cabeçalho 
        Row headerRowPrest = sheetPrest.createRow(0);
        Row headerRowRevis = sheetRevis.createRow(0);

        //Criação das células do cabeçalho Prestação de contas
        String[] cabPrestContas = cc.getCabecalho(1);
        IntStream.range(0, cabPrestContas.length).forEach(i -> {
            Cell cell = headerRowPrest.createCell(i);
            cell.setCellValue(cabPrestContas[i]);
            cell.setCellStyle(headerCellStyle);
            sheetPrest.autoSizeColumn(i);
        });
        
        //Criação das células do cabeçalho Revisional
        String[] cabRevisional = cc.getCabecalho(2);
        IntStream.range(0, cabRevisional.length).forEach(i -> {
            Cell cell = headerRowRevis.createCell(i);
            cell.setCellValue(cabRevisional[i]);
            cell.setCellStyle(headerCellStyle);
            sheetRevis.autoSizeColumn(i);
        });
        
        AtomicInteger numRowPrest = new AtomicInteger(1);
        AtomicInteger numRowRevis = new AtomicInteger(1);

        //Loop pelas listas de lancamentos para formar a tabela
        cc.getListMesConta().stream().forEach(tabMesCc -> {   
            if(numRowPrest.intValue() == 1) {
                Row saldoAntPrest = sheetPrest.createRow(numRowPrest.getAndIncrement());
                saldoAntPrest.createCell(0).setCellValue("Saldo Anterior em");
                sheetPrest.autoSizeColumn(0);
                saldoAntPrest.createCell(1).setCellValue(sdfr.format(tabMesCc.getDtSaldoAnterior())); 
                saldoAntPrest.createCell(2).setCellValue(String.format("%.2f", (tabMesCc.getVlSaldoAnterior())).replace(".", ","));
            }
            
            if(numRowRevis.intValue() == 1) {            
                Row saldoAntRevis = sheetRevis.createRow(numRowRevis.getAndIncrement());
                saldoAntRevis.createCell(0).setCellValue("Saldo Anterior em");
                sheetRevis.autoSizeColumn(0);
                saldoAntRevis.createCell(1).setCellValue(sdfr.format(tabMesCc.getDtSaldoAnterior())); 
                saldoAntRevis.createCell(2).setCellValue(String.format("%.2f", (tabMesCc.getVlSaldoAnterior())).replace(".", ","));            
            }
            
            tabMesCc.getListLancamentoConta().stream().forEach(lancamento -> {
                Row rowPrest = sheetPrest.createRow(numRowPrest.getAndIncrement());
                HashMap<Integer, String> valuesPrest = lancamento.getValues(1); 

                IntStream.rangeClosed(1, valuesPrest.size()).forEach(j -> {
                    rowPrest.createCell(j - 1).setCellValue(valuesPrest.get(j));
                });
                
                Row rowRevis = sheetRevis.createRow(numRowRevis.getAndIncrement());
                HashMap<Integer, String> valuesRevis = lancamento.getValues(2);

                IntStream.rangeClosed(1, valuesRevis.size()).forEach(j -> {
                    rowRevis.createCell(j - 1).setCellValue(valuesRevis.get(j));
                });
            });
        });
               
        try {
            fileName = "P:\\CENOP1915\\" + cc.getNomeCliente().toUpperCase() + "_ExtratoCc_" +cc.getIdConta().getNrAgencia()+ "_" + cc.getIdConta().getNrConta()+ "_" + dataInicial.substring(0,2) + dataInicial.substring(3) + " a " + dataFinal.substring(0, 2) + dataFinal.substring(3) + ".xls";
            
            FileOutputStream out
                    = new FileOutputStream(new File(PlanilhaExtratoContaCorrente.fileName));
            workbook.write(out);
            out.close();
            System.out.println("Arquivo Excel criado com sucesso!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro na edição do arquivo!");
        }

    }
}
