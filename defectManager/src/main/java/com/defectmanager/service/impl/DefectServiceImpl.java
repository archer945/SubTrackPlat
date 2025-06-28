package com.defectmanager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.FoundMethodEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
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
    public Page<Defect> queryByCondition(DefectQuery query) {
        Page<Defect> page = new Page<>(query.getPageIndex(), query.getPageSize());

        LambdaQueryWrapper<Defect> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getKeyword() != null, Defect::getDescription, query.getKeyword())
                .eq(query.getType() != null, Defect::getType,
                        query.getType() != null ? DefectTypeEnum.fromDbValue(query.getType()) : null)
                .eq(query.getStatus() != null, Defect::getStatus,
                        query.getStatus() != null ? DefectStatusEnum.fromDbValue(query.getStatus()) : null)
                .eq(query.getSeverity() != null, Defect::getSeverity,
                        query.getSeverity() != null ? SeverityLevelEnum.fromDbValue(query.getSeverity()) : null);

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

        // 生成缺陷编号，如：DEF-2024-001
        String defectCode = "DEF-" + LocalDate.now().getYear() + "-" +
                String.format("%03d", defectMapper.selectCount(null) + 1);
        defect.setCode(defectCode);
        // 设置默认发现状态（如果不为空）
        if (defect.getStatus() == null) {
            defect.setStatus(DefectStatusEnum.PENDING);
        }
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
    public boolean updateStatus(Long id, DefectStatusEnum newStatus, Long operatorId) {
        // 1. 获取当前缺陷
        Defect defect = defectMapper.selectById(id);
        if (defect == null) {
            throw new RuntimeException("缺陷不存在");
        }

        // 2. 使用枚举的状态流转校验
        validateTransition(defect.getStatus(), newStatus);

        // 3. 更新状态及关联字段
        updateStatusFields(defect, newStatus, operatorId);

        // 4. 持久化到数据库
        return defectMapper.updateById(defect) > 0;
    }
    // 状态流转校验（枚举版）
    private void validateTransition(DefectStatusEnum currentStatus, DefectStatusEnum newStatus) {
        Map<DefectStatusEnum, List<DefectStatusEnum>> rules = Map.of(
                DefectStatusEnum.PENDING, List.of(DefectStatusEnum.CONFIRMED, DefectStatusEnum.REJECTED),
                DefectStatusEnum.CONFIRMED, List.of(DefectStatusEnum.PROCESSING, DefectStatusEnum.CLOSED),
                DefectStatusEnum.PROCESSING, List.of(DefectStatusEnum.FIXED, DefectStatusEnum.REVIEW_NEEDED),
                DefectStatusEnum.REVIEW_NEEDED, List.of(DefectStatusEnum.PROCESSING, DefectStatusEnum.FIXED)
        );

        if (!rules.getOrDefault(currentStatus, List.of()).contains(newStatus)) {
            throw new RuntimeException(
                    String.format("非法状态流转: %s → %s",
                            currentStatus.getDisplayName(),
                            newStatus.getDisplayName())
            );
        }
    }
    // 更新字段的通用方法（枚举版）
    private void updateStatusFields(Defect defect, DefectStatusEnum newStatus, Long operatorId) {
        defect.setStatus(newStatus);
        defect.setUpdateTime(LocalDateTime.now());

        switch (newStatus) {
            case CONFIRMED:
                defect.setConfirmBy(operatorId);
                defect.setConfirmTime(LocalDateTime.now());
                break;
            case PROCESSING:
                defect.setHandleBy(operatorId);
                defect.setHandleStart(LocalDateTime.now());
                break;
            case FIXED:
                defect.setHandleBy(operatorId); // 处理人可能和确认人不同
                defect.setHandleEnd(LocalDateTime.now());
                break;
            case CLOSED:
                // 关闭状态不需要特殊处理
                break;
            default:
                // 其他状态不处理
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




