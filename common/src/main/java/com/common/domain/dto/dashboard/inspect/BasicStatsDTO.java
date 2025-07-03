package com.common.domain.dto.dashboard.inspect;

import lombok.Data;

@Data
public class BasicStatsDTO {
    private Integer today;  // 今日
    private Integer yesterday;  // 昨日
    private Integer completed;  // 已完成
}
