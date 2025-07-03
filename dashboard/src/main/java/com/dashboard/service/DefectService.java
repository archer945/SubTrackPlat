package com.dashboard.service;

import com.common.domain.dto.dashboard.defect.DefectStatsDTO;
import com.common.domain.dto.dashboard.defect.DefectTypeDTO;

import java.util.List;

public interface DefectService {
    List<DefectTypeDTO> getDefectType();

    DefectStatsDTO getDefectStats();


}
