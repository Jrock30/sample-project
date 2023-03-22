package com.sample.project.view;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelWriteComponent {

    private Workbook workbook;
    private Map<String, Object> model;
    private HttpServletResponse response;

    public ExcelWriteComponent(Workbook workbook, Map<String, Object> model, HttpServletResponse response) {
        this.workbook = workbook;
        this.model = model;
        this.response = response;
    }

    public void create() {
        if(!ObjectUtils.isEmpty(model.get("list"))){
            MutipleExcelWriteComponent mutipleExcelWriteComponent = new MutipleExcelWriteComponent(workbook, model, response);
            mutipleExcelWriteComponent.create();

        }else{
            setFileName(response, mapToFileName());
            Sheet sheet = workbook.createSheet();
            createHead(sheet, mapToHeadList());
            createBody(sheet, mapToBodyList());
            if(Objects.nonNull(model.get("headSize"))){
                createHeadSize(sheet, (List<Integer>) model.get("headSize"));
            }
        }
    }

    private String mapToFileName() {
        return (String) model.get(ExcelConstant.FILE_NAME);
    }

    private List<String> mapToHeadList() {
        return (List<String>) model.get(ExcelConstant.HEAD);
    }

    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) model.get(ExcelConstant.BODY);
    }

    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + getFileExtension(fileName) + "\"");
    }

    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    private void createHead(Sheet sheet, List<String> headList) {
        createRow(sheet, headList, 0);
    }

    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i + 1);
        }
    }

    private void createHeadSize(Sheet sheet, List<Integer> headSizeList) {
        int rowSize = headSizeList.size();
        for (int i = 0; i < rowSize; i++) {
            sheet.setColumnWidth(i,headSizeList.get(i));
        }
    }

    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        sheet.autoSizeColumn(rowNum);
        sheet.setColumnWidth(rowNum, sheet.getColumnWidth(rowNum) + 3000);

        for (int i = 0; i < size; i++) {
            row.createCell(i).setCellValue((ObjectUtils.isEmpty(cellList.get(i))||cellList.get(i).equals("null"))?"":cellList.get(i));
            if(rowNum == 0) {
                row.getCell(i).setCellStyle(headCellStyle(workbook));
            }/* else {
				if(rowNum % 2 == 0) {
					row.getCell(i).setCellStyle(cellStyle(workbook));
				}
			}*/
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
        //cellStyle.setFillForegroundColor((short) 12);
        //cellStyle.setShrinkToFit(true);
        //cellStyle.setWrapText(true);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(234, 160, 156)).getIndex());
        cellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        // font.setFontHeightInPoints((short) 12);/
        font.setColor(IndexedColors.WHITE.index);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 컨텐츠 셀 스타일
     * @param wb
     * @return
     */
    public CellStyle cellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        //cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private CellStyle createCellStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREEN.index);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLUE.index);
        style.setBorderTop(BorderStyle.THIN);
        // style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
        style.setTopBorderColor(IndexedColors.BLACK.index);
        // style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        return style;
    }

}
