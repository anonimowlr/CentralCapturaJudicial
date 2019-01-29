/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.poi;


import centralcapturasjudicial.model.entity.cdc.OperacaoCdc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javax.persistence.criteria.Path;
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
public class PlanilhaExtratoCdc {

    private static String fileName;
    File diretorio;
    
    HashMap<Integer, String> values;
    
    public void criaPlanilha(OperacaoCdc operacao) throws IOException, ParseException, InterruptedException {

        //Criação da planilha
        Workbook workbook = new HSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheetOpe = workbook.createSheet("Operação");
        Sheet sheetPcl = workbook.createSheet("Parcelas");
        Sheet sheetCtr = workbook.createSheet("Contratos Vinculados");

        //Estilo da fonte
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        //Estilo da célula
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Criação da Linha de cabeçalho
        Row headerRowOpe = sheetOpe.createRow(0);
        Row headerRowPcl = sheetPcl.createRow(0);
        Row headerRowCtr = sheetCtr.createRow(0);

        //Criação das células do cabeçalho da Operação
        String[] cabecalhoOpe = operacao.getCabecalhoOperacao();
        IntStream.range(0, cabecalhoOpe.length).forEach(i -> {
            Cell cell = headerRowOpe.createCell(i);
            cell.setCellValue(cabecalhoOpe[i]);
            cell.setCellStyle(headerCellStyle);
            sheetOpe.autoSizeColumn(i);
        });
        
         //Criação das células do cabeçalho das Parcelas
        String[] cabecalhoPcl = operacao.getCabecalhoParcelas();
        IntStream.range(0, cabecalhoPcl.length).forEach(i -> {
            Cell cell = headerRowPcl.createCell(i);
            cell.setCellValue(cabecalhoPcl[i]);
            cell.setCellStyle(headerCellStyle);
            sheetPcl.autoSizeColumn(i);
        });
        
         //Criação das células do cabeçalho dos Contratos Vinculados
        String[] cabecalhoCtr = operacao.getCabecalhoContratos();
        IntStream.range(0, cabecalhoCtr.length).forEach(i -> {
            Cell cell = headerRowCtr.createCell(i);
            cell.setCellValue(cabecalhoCtr[i]);
            cell.setCellStyle(headerCellStyle);
            sheetCtr.autoSizeColumn(i);
        });
        
        //Criação da linha com os dados da Operação
        Row rowOpe = sheetOpe.createRow(1);
        values = operacao.getValues();
        IntStream.rangeClosed(1, values.size()).forEach(j -> {
            rowOpe.createCell(j - 1).setCellValue(values.get(j));
        });    
        
        //Criação das linhas com os dados das Parcelas
        AtomicInteger numRowPcl = new AtomicInteger(1);
        operacao.getListParcelaOperacaoCdc().stream().forEach(pcl -> {    
            if(pcl.getListPagamentoParcelaCdc().isEmpty()) {
                Row row = sheetPcl.createRow(numRowPcl.getAndIncrement());
                values = pcl.getValues();
                IntStream.rangeClosed(1, values.size()).forEach(j -> {
                    row.createCell(j - 1).setCellValue(values.get(j));      
                });    
            }
            else {
                pcl.getListPagamentoParcelaCdc().stream().forEach(pgto -> {
                    pgto.getListComponentePagamentoParcelaCdc().stream().forEach(cpnt -> {//erro aki nullpointer
                        Row row = sheetPcl.createRow(numRowPcl.getAndIncrement());
                        values = cpnt.getValues();
                        IntStream.rangeClosed(1, values.size()).forEach(j -> {
                            row.createCell(j - 1).setCellValue(values.get(j));      
                        });    
                    });
                });    
            }
           
        });

         //Criação das linhas com os dados dos Contratos Vinculados
        AtomicInteger numRowCtr = new AtomicInteger(1);
        operacao.getListContratoVinculadoCdc().stream().forEach(ctr -> {    
            Row row = sheetCtr.createRow(numRowCtr.getAndIncrement());
            values = ctr.getValues();
            IntStream.rangeClosed(1, values.size()).forEach(j -> {
                row.createCell(j - 1).setCellValue(values.get(j));      
            });    
        });

        try {
            
//----------------------------------------------------------------Criando um novo diretório(PASTA)-------------------------------------------------------            
            diretorio = new File("P:\\CENOP1915\\" + operacao.getNomeCliente().toUpperCase() +"_CPF_" + operacao.getCpfCliente()+ "");
            if(!diretorio.exists()){
                diretorio.mkdir();//mkdirs() cria diretórios e subdiretórios
                System.out.println("Diretório criado com sucesso!");
            }
            
            fileName = "P:\\CENOP1915\\" + operacao.getNomeCliente().toUpperCase() + "_ExtratoCDC_" + operacao.getId() + ".xls"; 
            FileOutputStream out = new FileOutputStream(new File(PlanilhaExtratoCdc.fileName));
            workbook.write(out);
            out.close();
            System.out.println("Arquivo Excel criado com sucesso!");
            
//-----------------------------------------------------------------Arquivo a ser movido----------------------------------------------------------------
            File arquivo = new File(fileName);
//-----------------------------------------------------------------------------------------------------------------------------------------------------            



//-----------------------------------------------------------------Diretório do destino----------------------------------------------------------------
            File diretorioDestino = new File("P:\\CENOP1915\\" + operacao.getNomeCliente().toUpperCase() +"_CPF_" + operacao.getCpfCliente()+ "");
//-----------------------------------------------------------------------------------------------------------------------------------------------------            
            
            boolean sucesso = arquivo.renameTo(new File(diretorioDestino, arquivo.getName()));
            if (sucesso) {
                System.out.println("Arquivo movido para '" + diretorioDestino.getAbsolutePath() + "'");
            } else {
                System.out.println("Erro ao mover arquivo '" + arquivo.getAbsolutePath() + "' para '"
                        + diretorioDestino.getAbsolutePath() + "'");
            }       

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro na edição do arquivo!");
            
        }
    }
    
 
}
