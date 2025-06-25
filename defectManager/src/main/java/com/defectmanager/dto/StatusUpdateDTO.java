package com.defectmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateDTO {
    @NotBlank(message = "状态不能为空")
    private String status;

    private String remark; // 可选备注
    private Long operatorId; // 操作人ID（可从token获取）
}
