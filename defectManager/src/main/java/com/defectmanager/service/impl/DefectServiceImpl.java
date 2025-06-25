package com.defectmanager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.entity.Defect;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DefectServiceImpl implements DefectService {

    @Autowired
    private DefectMapper defectMapper;
    
    @Override
    /*
    * 分页查询缺陷信息
    * */
    public Page<Defect> queryByCondition(DefectQuery query) {
        Page<Defect> page = new Page<>(query.getPageIndex(), query.getPageSize());

        LambdaQueryWrapper<Defect> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getKeyword() != null, Defect::getDescription, query.getKeyword())
                .eq(query.getType() != null, Defect::getType, query.getType())
                .eq(query.getStatus() != null, Defect::getStatus, query.getStatus())
                .eq(query.getSeverity() != null, Defect::getSeverity, query.getSeverity());

        if (query.getStartTime() != null) {
            wrapper.ge(Defect::getFoundTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(Defect::getFoundTime, query.getEndTime());
        }

        return defectMapper.selectPage(page, wrapper);
    }

    /*
    * 添加缺陷信息
    * */

    @Override
    public Defect add(Defect defect) {
        defect.setCreateTime(LocalDateTime.now());
        defect.setUpdateTime(LocalDateTime.now());
        defect.setStatus("待确认"); // 默认状态

        // 生成缺陷编号，如：DEF-2024-001
        String defectCode = "DEF-" + LocalDate.now().getYear() + "-" +
                String.format("%03d", defectMapper.selectCount(null) + 1);
        defect.setCode(defectCode);
        defectMapper.insert(defect);
        return defect;
    }

    /*
    * 删除缺陷
    * */
    @Override
    public Boolean deleteDefect(Long id) {
        // 删除缺陷
        return defectMapper.deleteById(id) > 0;
    }
}
