package com.defectmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.FoundMethodEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("defect")
public class Defect {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private DefectTypeEnum type;
    private String description;
    private SeverityLevelEnum severity;

    @JsonProperty("isValid")
    private Boolean isValid;

    @JsonProperty("defectLength")
    private BigDecimal defectLength;

    @JsonProperty("defectArea")
    private BigDecimal defectArea;

    @JsonProperty("defectCount")
    private Integer defectCount = 1;

    private String suggestion;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")  // ✅ 匹配 JSON 时间格式
    @TableField("found_time")  // ✅ 显式指定数据库列名
    @JsonProperty("foundTime")  // ✅ 确保 JSON 反序列化
    private LocalDateTime foundTime;

    @JsonProperty("foundBy")
    private Long foundBy;

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("taskname")
    private String taskName;

    @JsonProperty("foundMethod")
    private FoundMethodEnum foundMethod;

    private BigDecimal location;
    private DefectStatusEnum status;

    @JsonProperty("confirmBy")
    private Long confirmBy;

    @JsonProperty("confirmTime")
    private LocalDateTime confirmTime;

    @JsonProperty("handleBy")
    private Long handleBy;

    @JsonProperty("handleStart")
    private LocalDateTime handleStart;

    @JsonProperty("handleEnd")
    private LocalDateTime handleEnd;

    private String result;

    @JsonProperty("createTime")  // ✅ 修正错误的注解
    private LocalDateTime createTime;

    @JsonProperty("updateTime")  // ✅ 修正错误的注解
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<DefectImage> images;
}