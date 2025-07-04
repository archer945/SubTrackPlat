package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 批量用户操作DTO
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Data
@Schema(description = "批量用户操作DTO")
public class BatchUserDTO {
    
    @NotEmpty(message = "用户ID列表不能为空")
    @Size(min = 1, message = "至少选择一个用户")
    @Schema(description = "用户ID列表")
    private List<Long> userIds;
} 