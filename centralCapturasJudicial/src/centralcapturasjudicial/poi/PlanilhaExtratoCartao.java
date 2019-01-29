/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralcapturasjudicial.poi;

import centralcapturasjudicial.model.entity.cartao.ExtratoCartao;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class PlanilhaExtratoCartao {

    private static String fileName;

    public void criaPlanilha(ExtratoCartao fatura) throws IOException {

        //Criação da planilha
        Workbook workbook = new HSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Extrato Cartão");

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
        String[] cabecalho = fatura.getCabecalho();
        IntStream.range(0, cabecalho.length).forEach(i -> {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cabecalho[i]);
            cell.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(i);
        });

        AtomicInteger numRow = new AtomicInteger(1);

        //Loop pelas listas de lancamentos para formar a tabela
        fatura.getListMesCartao().stream().forEach(mes -> {

            mes.getListLancamentoCartao().stream().forEach(lancamento -> {

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
        });

        try {
            fileName = "P:\\CENOP1915\\" + fatura.getNomeCliente().toUpperCase() + "_ExtratoCartaoCredito_" + fatura.getCartao() + ".xls";
            
            FileOutputStream out
                    = new FileOutputStream(new File(PlanilhaExtratoCartao.fileName));
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
