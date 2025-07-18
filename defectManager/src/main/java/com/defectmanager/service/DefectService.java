package com.defectmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.entity.Defect;
import com.defectmanager.query.DefectQuery;

public interface DefectService {
    Page<Defect> queryByCondition(DefectQuery query);

    Defect add(Defect defect);

    Boolean deleteDefect(Long id);

    boolean updateStatus(Long id, DefectStatusEnum newStatus, Long operatorId);

// 在DefectService.java中添加

    Defect getById(Long id);

}
