package com.dashboard.controller;


import com.common.domain.dto.dashboard.defect.DefectStatsDTO;
import com.common.domain.vo.JsonVO;
import com.common.domain.dto.dashboard.defect.DefectTypeDTO;
import com.dashboard.service.DefectService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/dashboard")
public class DefectController {

    @Autowired
    private DefectService defectService;

    @GetMapping("")  // 映射到/dashboard
    public String apiRoot() {
        return "Dashboard API 已就绪，可用接口：/defectOverview, /inspectSummary";
    }

    @GetMapping("/defectOverview")
    @ApiOperation("获取缺陷类型及对应数量")
    public JsonVO<List<DefectTypeDTO>> getDefectType () {
        // 先获取数据，避免重复调用service方法
        List<DefectTypeDTO> defectTypes = defectService.getDefectType();
        if (defectTypes == null) {
            return JsonVO.fail(null);
        }
        return JsonVO.success(defectTypes);
    }
    
    @GetMapping("/defect-stats")
    public JsonVO<DefectStatsDTO> getDefectStats() {
        return JsonVO.success(defectService.getDefectStats());
    }
}

