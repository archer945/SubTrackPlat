package com.dashboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String type;                  // 直接存储字符串，如"结构裂缝"
    private String description;
    private String severity;              // "高"、"中"、"低"
    @JsonProperty("is_valid")
    private Boolean isValid;
    @JsonProperty("defect_length")
    private BigDecimal defectLength;      // 缺陷长度(m)
    @JsonProperty("defect_area")
    private BigDecimal defectArea;        // 缺陷面积(㎡)
    @JsonProperty("defect_count")
    private Integer defectCount = 1;      // 缺陷数量，默认1
    private String suggestion;            // 整改建议
    @JsonProperty("found_time")
    private LocalDateTime foundTime;      // 发现时间
    @JsonProperty("found_by")
    private Long foundBy;                 // 发现人员ID
    @JsonProperty("task_id")
    private Long taskId;                  // 关联任务ID
    @JsonProperty("task_name")
    private String taskName;              // 任务名称（冗余存储）
    @JsonProperty("found_method")
    private String foundMethod;           // "人工巡检"、"设备检测"、"例行检查"
    private BigDecimal location;          // 距离起点位置(m)
    private String status;                // "待确认"、"已确认"、"处理中"等
    @JsonProperty("confirm_by")
    private Long confirmBy;               // 确认人员ID
    @JsonProperty("confirm_time")
    private LocalDateTime confirmTime;    // 确认时间
    @JsonProperty("handle_by")
    private Long handleBy;                // 处理人员ID
    @JsonProperty("handle_start")
    private LocalDateTime handleStart;    // 处理开始时间
    @JsonProperty("handle_end")
    private LocalDateTime handleEnd;      // 处理完成时间
    private String result;                // 处理结果
    @JsonProperty("create_by")
    private LocalDateTime createTime;     // 创建时间
    @JsonProperty("update_by")
    private LocalDateTime updateTime;     // 更新时间

    // 非数据库字段，用于关联查询'
    @TableField(exist = false)
    private List<DefectImage> images;
}