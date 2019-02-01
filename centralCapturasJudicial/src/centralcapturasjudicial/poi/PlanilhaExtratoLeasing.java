/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.poi;

import centralcapturasjudicial.model.entity.leasing.OperacaoLeasing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
public class PlanilhaExtratoLeasing {

    private static String fileName;

    public void criaPlanilha(OperacaoLeasing oprLeasing) throws IOException {
        SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy");

        //Criação da planilha
        Workbook workbook = new HSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        
        //Aba de lançamentos
        Sheet sheet = workbook.createSheet("Extrato Leasing");

        //Estilo da fonte
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        //Estilo da célula
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Criação da Linha de cabeçalho
        Row headerRow = sheet.createRow(0);

        //Criação das células do cabeçalho
        String[] cabecalho = {"Data Lançamento", "Histórico", "Valor Lançamento", "Saldo Arrendamento"};
        IntStream.range(0, cabecalho.length).forEach(i -> {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cabecalho[i]);
            cell.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(i);
        });

        AtomicInteger numRow = new AtomicInteger(1);

        //Loop pelas listas de lancamentos para formar a tabela
        oprLeasing.getListLancamentoLeasing().stream().forEach(lancamento -> {
            Row row = sheet.createRow(numRow.getAndIncrement());
            HashMap<Integer, String> values = lancamento.getValues();
            //int numCell = lancamento.getClass().getDeclaredFields().length;
            int numCell = values.size();

            IntStream.rangeClosed(1, numCell).forEach(j -> {
                row.createCell(j - 1).setCellValue(values.get(j));                  
                //if(j == 1) sheet.autoSizeColumn(j - 1);
                //sheet.autoSizeColumn(j - 1);
            });
        });
        
        

        //aba de dados da operação
        
        Sheet sheet2 = workbook.createSheet("Dados Leasing");

        //Estilo da fonte
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setColor(IndexedColors.BLACK.getIndex());

        //Estilo da célula
        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleFont);

        //Criação de Linha
        Row titleRow = sheet2.createRow(0);
        Row rowDados = sheet2.createRow(0);
        
        //Criação de coluna
        Cell cellDados = titleRow.createCell(0);
        Cell cellDados2 = rowDados.createCell(1);
        
        String texto = "";
        
    //LINHA 1
        
        //Setando os campos
        texto = "Cliente: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);
        
        cellDados2.setCellValue(oprLeasing.getNrMciCliente()+" - "+oprLeasing.getTxNomeCliente());

    //LINHA 2
        titleRow = sheet2.createRow(1);
        rowDados = sheet2.createRow(1);
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "CNPJ/CPF: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getTxCpfCnpjCliente());
        
        cellDados = titleRow.createCell(2);
        cellDados2 = rowDados.createCell(3);
        
        texto = "Contrato: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getNrContrato());
        
    //LINHA 3
        titleRow = sheet2.createRow(2);
        rowDados = sheet2.createRow(2);        
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "Custo do Arrendamento: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getTxCustoArrendamento());
        
        cellDados = titleRow.createCell(2);
        cellDados2 = rowDados.createCell(3);
        
        texto = "Indexador: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getTxIndexador());
        
    //LINHA 4
        titleRow = sheet2.createRow(3);
        rowDados = sheet2.createRow(3);        
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "Dia do Vencimento da Parcela mensal: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getNrDiaVencimentoParcelaMensal());
        
        cellDados = titleRow.createCell(2);
        cellDados2 = rowDados.createCell(3);
        
        texto = "Prazo: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getTxPrazo());
        
    //LINHA 5
        titleRow = sheet2.createRow(4);
        rowDados = sheet2.createRow(4);        
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "Data da Assinatura do Tra: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(fd.format(oprLeasing.getDtAssinaturaTra()));
        
    //LINHA 6
        titleRow = sheet2.createRow(5);
        rowDados = sheet2.createRow(5);        
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "Vencimento da Primeira Parcela: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(fd.format(oprLeasing.getDtVencimentoPrimeiraParcela()));
        
    //LINHA 7
        titleRow = sheet2.createRow(6);
        rowDados = sheet2.createRow(6);        
        cellDados = titleRow.createCell(0);
        cellDados2 = rowDados.createCell(1);
        
        texto = "Valor do Bem: ";
        cellDados.setCellValue(texto);
        cellDados.setCellStyle(titleCellStyle);        
        cellDados2.setCellValue(oprLeasing.getVlBem());
        
    //AJUSTES DE TAMANHO DE COLUNAS
        sheet.autoSizeColumn(1);
        
        sheet2.autoSizeColumn(0);
        sheet2.autoSizeColumn(1);
        sheet2.autoSizeColumn(2);
        sheet2.autoSizeColumn(3);

        try {
            fileName = "P:\\CENOP1915\\" + oprLeasing.getTxNomeCliente().toUpperCase() + "_ExtratoLeasing_" + String.valueOf(oprLeasing.getNrContrato())+ ".xls";
            
            FileOutputStream out
                    = new FileOutputStream(new File(PlanilhaExtratoLeasing.fileName));
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


//    static <T> List<Field> getCabecalho(Class<T> klazz) {
//
//        List<Field> fields = Arrays.asList(klazz.getDeclaredFields());
    //Field[] fields = klazz.getDeclaredFields();
    //        System.out.printf("%d fields:%n", fields.length);
    //        for (Field field : fields) {
    //            System.out.printf("%s %s %s%n",
    //                    Modifier.toString(field.getModifiers()),
    //                    field.getType().getSimpleName(),
    //                    field.getName()
    //            );
    //        }
//        fields.removeIf(s -> s.getName() == "id");    
    //System.out.println(fields.toString());
//        return fields;
//    }
}
