package com.dashboard.service;

import com.common.domain.dto.dashboard.DefectStatsDTO;
import com.common.domain.dto.dashboard.DefectTypeDTO;

import java.util.List;

public interface DefectService {
    List<DefectTypeDTO> getDefectType();

    List<DefectStatsDTO >getDefectStatsSummary();
}
