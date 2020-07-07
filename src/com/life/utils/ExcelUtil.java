package com.life.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    private final static String excel2003L = ".xls";
    private final static String excel2007U = ".xlsx";

    public static void main(String[] args) {

    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param values    内容
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        try {
            //创建内容
            for (int i = 0; i < values.length; i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < values[i].length; j++) {
                    //将内容按顺序赋给对应的列对象
                    row.createCell(j).setCellValue(values[i][j]);
                }
            }
        } catch (Exception e) {

        }

        return wb;
    }


    //读取文件数据
    public static List<List<Object>> getExcelList(InputStream is, String fileName) throws Exception {
        List<List<Object>> list = new ArrayList<List<Object>>();
        Workbook workbook = null;


        //验证文件格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(excel2003L)) {
            workbook = new HSSFWorkbook(is);
        } else if (suffix.equals(excel2007U)) {
            workbook = new XSSFWorkbook(is);
        }


        Sheet sheet = null;
        Row row = null;
        Cell cell = null;


        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            System.out.println("getNumberOfSheets:" + workbook.getNumberOfSheets());
            sheet = workbook.getSheetAt(i);
            if (sheet == null) continue;
            //遍历当前sheet中全部行
            for (int j = sheet.getFirstRowNum() + 1; j < sheet.getLastRowNum() + 1; j++) {
                System.out.println("RowNum:" + sheet.getLastRowNum());
                row = sheet.getRow(j);
                if (row == null) continue;
                //循环当前row中全部列
                List<Object> li = new ArrayList<Object>();
                for (int k = row.getFirstCellNum(); k < row.getLastCellNum(); k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        li.add(getCellValue(cell));
                    }
                }
                list.add(li);
            }
        }


        return list;
    }


    //单元格数据类型格式化
    public static Object getCellValue(Cell cell) {
        Object value = null;
        DecimalFormat decimalFormat1 = new DecimalFormat("0");
        DecimalFormat decimalFormat2 = new DecimalFormat("0.00");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = decimalFormat1.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = dateFormat.format(cell.getDateCellValue());
                } else {
                    value = decimalFormat2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
        }
        return value;
    }

}