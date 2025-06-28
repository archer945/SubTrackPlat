package com.defectmanager.service.impl;

import com.defectmanager.entity.Defect;
import com.defectmanager.enmu.*;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.exportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportServiceImpl implements exportService {

    @Autowired
    private DefectMapper defectMapper;

    @Override
    public ByteArrayOutputStream exportDefectsToExcel(DefectQuery query, HttpServletResponse response) throws UnsupportedEncodingException {
        // 1. 设置响应头
        String fileName = "缺陷数据_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 2. 查询数据 - 使用枚举转换后的查询条件
        List<Defect> defects = defectMapper.selectByQuery(query);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 3. 生成Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("缺陷数据");

            // 3.1 创建表头
            createHeaderRow(workbook, sheet);

            // 3.2 填充数据
            fillDataRows(workbook, sheet, defects);

            // 3.3 写入到输出流
            workbook.write(outputStream);
            response.getOutputStream().write(outputStream.toByteArray());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 定义表头
        String[] headers = {
                "序号","任务名称", "缺陷类型","缺陷距离位置(m)","是否属实", "严重程度",  "缺陷长度(m)",
                "缺陷面积(㎡)", "缺陷数量","建议整改方式","发现时间",
                "状态"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Workbook workbook, Sheet sheet, List<Defect> defects) {
        int rowNum = 1;
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));

        for (Defect defect : defects) {
            Row row = sheet.createRow(rowNum++);

            // 填充数据
            row.createCell(0).setCellValue(defect.getId());
            row.createCell(1).setCellValue(defect.getTaskName());

            // 处理枚举类型字段
            row.createCell(2).setCellValue(
                    defect.getType() != null ? defect.getType().getDisplayName() : "");

            row.createCell(3).setCellValue(
                    defect.getLocation() != null ? defect.getLocation().toString() : "");

            row.createCell(4).setCellValue(
                    defect.getIsValid() != null ? (defect.getIsValid() ? "是" : "否") : "");

            row.createCell(5).setCellValue(
                    defect.getSeverity() != null ? defect.getSeverity().getDisplayName() : "");

            row.createCell(6).setCellValue(
                    defect.getDefectLength() != null ? defect.getDefectLength().doubleValue() : 0);

            row.createCell(7).setCellValue(
                    defect.getDefectArea() != null ? defect.getDefectArea().doubleValue() : 0);

            row.createCell(8).setCellValue(
                    defect.getDefectCount() != null ? defect.getDefectCount() : 0);

            row.createCell(9).setCellValue(
                    defect.getSuggestion() != null ? defect.getSuggestion() : "");

            Cell dateCell = row.createCell(10);
            if (defect.getFoundTime() != null) {
                dateCell.setCellValue(defect.getFoundTime());
                dateCell.setCellStyle(dateStyle);
            }

            row.createCell(11).setCellValue(
                    defect.getStatus() != null ? defect.getStatus().getDisplayName() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}