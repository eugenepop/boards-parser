package nil.webparser.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import nil.webparser.entity.Advert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class SpreadSheetManager {

    private static final String FILE_NAME = "report/report.xlsx";

    public void generate(List<Advert> adverts) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("first sheet name");

        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Id");
        row.createCell(1).setCellValue("Title");
        row.createCell(2).setCellValue("District");
        row.createCell(3).setCellValue("Address");
        row.createCell(4).setCellValue("Price");
        row.createCell(5).setCellValue("Link");
        row.createCell(6).setCellValue("Seller");
        row.createCell(7).setCellValue("Phone");
        for (Advert advert : adverts) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(advert.getId());
            row.createCell(1).setCellValue(advert.getTitle());
            row.createCell(2).setCellValue(advert.getDistrict());
            row.createCell(3).setCellValue(advert.getAddress());
            row.createCell(4).setCellValue(advert.getPrice());
            row.createCell(5).setCellValue(advert.getLink());
            row.createCell(6).setCellValue(advert.getSellerName());
            row.createCell(7).setCellValue(advert.getSellerPhone());
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
