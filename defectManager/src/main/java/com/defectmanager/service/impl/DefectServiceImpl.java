package com.defectmanager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import com.defectmanager.service.ImageService;
import com.defectmanager.utils.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class DefectServiceImpl implements DefectService {

    @Autowired
    private DefectMapper defectMapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AliOSSUtils ossUtils;
    
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

        Defect defect = defectMapper.selectById(id);
        if (defect == null) {
            return false;
        }
        // 先查询图片记录（用于后续删除OSS文件）
        List<DefectImage> images = imageService.getImagesByDefectId(id);


        boolean defectDeleted = defectMapper.deleteById(id) > 0;
        // 异步清理OSS文件（避免阻塞主流程）
        if (defectDeleted && !images.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                images.forEach(img -> ossUtils.delete(img.getImageUrl()));
            });
        }
        // 删除缺陷
        return defectDeleted;
    }

    /*
    * 状态修改
    * */
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status, Long operatorId) {
        // 1. 获取当前缺陷
        Defect defect = defectMapper.selectById(id);
        if (defect == null) throw new RuntimeException("缺陷不存在");

        // 2. 硬编码状态流转校验
        validateTransition(defect.getStatus(), status);

        // 3. 更新状态及关联字段（操作人+时间戳）
        updateStatusFields(defect, status, operatorId);

        // 4. 持久化到数据库
        return defectMapper.updateById(defect) > 0;
    }
    // 状态流转校验（硬编码版）
    private void validateTransition(String currentStatus, String newStatus) {
        Map<String, List<String>> rules = Map.of(
                "待确认", List.of("已确认", "已驳回"),
                "已确认", List.of("处理中", "已关闭"),
                "处理中", List.of("已整改", "需复查")
        );
        if (!rules.getOrDefault(currentStatus, List.of()).contains(newStatus)) {
            throw new RuntimeException("非法状态流转: " + currentStatus + " → " + newStatus);
        }
    }

    // 更新字段的通用方法
    private void updateStatusFields(Defect defect, String newStatus, Long operatorId) {
        defect.setStatus(newStatus);
        defect.setUpdateTime(LocalDateTime.now());

        switch (newStatus) {
            case "已确认":
                defect.setConfirmBy(operatorId);
                defect.setConfirmTime(LocalDateTime.now());
                break;
            case "处理中":
                defect.setHandleBy(operatorId);
                defect.setHandleStart(LocalDateTime.now());
                break;
            case "已整改":
                defect.setHandleBy(operatorId); // 处理人可能和确认人不同
                defect.setHandleEnd(LocalDateTime.now());
                break;
            case "已关闭":
                break;
        }
    }

    /*
    * 根据id获取图片信息
    * */

    @Override
    public Defect getById(Long id) {
        return defectMapper.selectById(id);
    }


}




