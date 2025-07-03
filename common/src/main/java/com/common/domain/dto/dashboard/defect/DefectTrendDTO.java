package com.common.domain.dto.dashboard.defect;


import lombok.Data;

@Data
public class DefectTrendDTO {
    private String date;      // 日期（yyyy-MM-dd）
    private Integer count;    // 当日缺陷数
}
