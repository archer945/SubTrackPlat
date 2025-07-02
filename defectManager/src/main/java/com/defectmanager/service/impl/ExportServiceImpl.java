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
    public ByteArrayOutputStream exportDefectsToCsv(DefectQuery query, HttpServletResponse response) throws UnsupportedEncodingException {
        // 1. 设置响应头
        String fileName = "缺陷数据_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()) + ".csv";
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 2. 查询数据
        List<Defect> defects = defectMapper.selectByQuery(query);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 3. 生成CSV内容
            StringBuilder csvContent = new StringBuilder();

            // 3.1 创建表头
            String[] headers = {
                    "序号", "任务名称", "缺陷类型", "缺陷距离位置(m)", "是否属实", "严重程度", "缺陷长度(m)",
                    "缺陷面积(㎡)", "缺陷数量", "建议整改方式", "发现时间", "状态"
            };
            csvContent.append(String.join(",", headers)).append("\n");

            // 3.2 填充数据
            DateTimeFormatter csvDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Defect defect : defects) {
                csvContent.append(defect.getId() != null ? defect.getId() : "").append(",");
                csvContent.append(defect.getTaskName() != null ? escapeCsv(defect.getTaskName()) : "").append(",");
                csvContent.append(defect.getType() != null ? escapeCsv(defect.getType().getDisplayName()) : "").append(",");
                csvContent.append(defect.getLocation() != null ? defect.getLocation().toString() : "").append(",");
                csvContent.append(defect.getIsValid() != null ? (defect.getIsValid() ? "是" : "否") : "").append(",");
                csvContent.append(defect.getSeverity() != null ? escapeCsv(defect.getSeverity().getDisplayName()) : "").append(",");
                csvContent.append(defect.getDefectLength() != null ? defect.getDefectLength().doubleValue() : "0").append(",");
                csvContent.append(defect.getDefectArea() != null ? defect.getDefectArea().doubleValue() : "0").append(",");
                csvContent.append(defect.getDefectCount() != null ? defect.getDefectCount() : "0").append(",");
                csvContent.append(defect.getSuggestion() != null ? escapeCsv(defect.getSuggestion()) : "").append(",");
                csvContent.append(defect.getFoundTime() != null ? csvDateFormatter.format(defect.getFoundTime()) : "").append(",");
                csvContent.append(defect.getStatus() != null ? escapeCsv(defect.getStatus().getDisplayName()) : "").append("\n");
            }

            // 3.3 写入到输出流
            outputStream.write(csvContent.toString().getBytes("UTF-8"));
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    // CSV转义方法
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // 如果值包含逗号、双引号或换行符，则用双引号括起来，并把内部的双引号替换为两个双引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }


}