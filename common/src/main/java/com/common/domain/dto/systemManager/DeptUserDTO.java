package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "部门人员统计DTO")
@Data
public class DeptUserDTO {
    @Schema(description = "部门ID", example = "1")
    private Long deptId;
    
    @Schema(description = "部门名称", example = "技术部")
    private String deptName;
    
    @Schema(description = "部门人员数量", example = "10")
    private Long userCount;
    
    @Schema(description = "用户ID列表")
    private List<Long> userIds;
} 