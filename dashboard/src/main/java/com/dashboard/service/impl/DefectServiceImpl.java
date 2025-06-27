package com.dashboard.service.impl;

import com.common.domain.dto.dashboard.DefectStatsDTO;
import com.common.domain.dto.dashboard.DefectTypeDTO;
import com.dashboard.mapper.DefectMapper;
import com.dashboard.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefectServiceImpl implements DefectService {

    @Autowired
    private DefectMapper defectMapper;
    @Override
    public List<DefectTypeDTO> getDefectType() {
        return defectMapper.getDefectType();
    }

    @Override
    public  List<DefectStatsDTO> getDefectStatsSummary(){

    };
}
