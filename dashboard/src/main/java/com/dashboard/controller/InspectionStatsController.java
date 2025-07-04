package com.dashboard.controller;

import com.common.domain.dto.dashboard.inspect.InspectionStatsDTO;
import com.common.domain.vo.JsonVO;
import com.dashboard.service.InspectionStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class InspectionStatsController {

    @Autowired
    private  InspectionStatsService inspectionStatsService;



    /**
     * 获取巡视统计概览
     */
    @GetMapping("/inspectSummary")
    public JsonVO<InspectionStatsDTO> getInspectionSummary() {
        InspectionStatsDTO stats = inspectionStatsService.getInspectionStats();
        return JsonVO.success(stats);
    }

    /**
     * 按日期范围查询趋势数据（扩展接口示例）
     */
    @GetMapping("/inspectTrend")
    public JsonVO<InspectionStatsDTO> getInspectionTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // 可调用Service层扩展方法实现按日期筛选
        InspectionStatsDTO stats = inspectionStatsService.getInspectionStats();
        System.out.println("stats = " + stats);
        return JsonVO.success(stats);
    }
}
