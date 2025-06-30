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
@RequestMapping("/api/defects")
public class DefectController {

    @Autowired
    private DefectService defectService;


    @GetMapping("/overview")
    @ApiOperation("获取缺陷类型及对应数量")
    public JsonVO<List<DefectTypeDTO>> getDefectType () {

        if (defectService.getDefectType() == null) {
            return JsonVO.fail(null);
        }
        return JsonVO.success(defectService.getDefectType());
    }
        @GetMapping("/defect-stats")
        public JsonVO<DefectStatsDTO> getDefectStats() {
            return JsonVO.success(defectService.getDefectStats());
        }
    }

