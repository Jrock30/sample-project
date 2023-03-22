package com.sample.project.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class MutipleExcelWriteComponent {

    private Workbook workbook;
    private Map<String, Object> model;
    private HttpServletResponse response;

    public MutipleExcelWriteComponent(Workbook workbook, Map<String, Object> model, HttpServletResponse response) {
        this.workbook = workbook;
        this.model = model;
        this.response = response;
    }

    public void create() {
        List<Map<String,Object>> modelList = (List<Map<String, Object>>) this.convertObjectToList(model.get("list"));
        modelList.forEach(item ->{
            setFileName(response, mapToFileName(item));
            Sheet sheet = workbook.createSheet(item.get(ExcelConstant.SHEET).toString());
            createHead(sheet, mapToHeadList(item));
            createBody(sheet, mapToBodyList(item));
            if(Objects.nonNull(model.get("headSize"))){
                createHeadSize(sheet, (List<Integer>) model.get("headSize"));
            }
        });
    }

    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) fileName += ".xlsx";
        if (workbook instanceof SXSSFWorkbook) fileName += ".xlsx";
        if (workbook instanceof HSSFWorkbook) fileName += ".xls";
        return fileName;
    }

    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + getFileExtension(fileName) + "\"");
    }

    private String mapToFileName(Map<String,Object> map) {
        return (String) map.get(ExcelConstant.FILE_NAME);
    }

    private void createHead(Sheet sheet, List<String> headList) {
        createRow(sheet, headList, 0);
    }

    private List<String> mapToHeadList(Map<String,Object> map) {
        return (List<String>) map.get(ExcelConstant.HEAD);
    }

    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        sheet.autoSizeColumn(rowNum);
        sheet.setColumnWidth(rowNum, sheet.getColumnWidth(rowNum) + 3000);

        for (int i = 0; i < size; i++) {
            row.createCell(i).setCellValue((ObjectUtils.isEmpty(cellList.get(i))||cellList.get(i).equals("null"))?"":cellList.get(i));
            if(rowNum == 0) row.getCell(i).setCellStyle(headCellStyle(workbook));
        }
    }

    private List<List<String>> mapToBodyList(Map<String,Object> map) {
        return (List<List<String>>) map.get(ExcelConstant.BODY);
    }

    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i + 1);
        }
    }

    /**
     * Header 용 Style
     *
     * @param wb
     * @return
     */
    public CellStyle headCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private void createHeadSize(Sheet sheet, List<Integer> headSizeList) {
        int rowSize = headSizeList.size();
        for (int i = 0; i < rowSize; i++) {
            sheet.setColumnWidth(i,headSizeList.get(i));
        }
    }

    private static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }

}
