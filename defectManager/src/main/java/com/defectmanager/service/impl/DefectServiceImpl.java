package com.defectmanager.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.mapper.DefectImageMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DefectServiceImpl implements DefectService {

    @Autowired
    private DefectMapper defectMapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AliOSSUtils ossUtils;
    @Autowired
    private DefectImageMapper imageMapper;

    @Override
    public Page<Defect> queryByCondition(DefectQuery query) {
        // 1. 分页查询缺陷数据
        Page<Defect> page = new Page<>(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<Defect> wrapper = buildWrapper(query);
        Page<Defect> defectPage = defectMapper.selectPage(page, wrapper);

        // 2. 提取缺陷ID列表
        List<Long> ids = defectPage.getRecords().stream()
                .map(Defect::getId)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return defectPage;
        }

        // 3. 查询关联图片并按缺陷ID分组
        List<DefectImage> imgList = imageMapper.selectList(
                new LambdaQueryWrapper<DefectImage>()
                        .in(DefectImage::getDefectId, ids)
                        .orderByAsc(DefectImage::getUploadedAt)
        );
        Map<Long, List<DefectImage>> imgMap = imgList.stream()
                .collect(Collectors.groupingBy(DefectImage::getDefectId));

        // 4. 设置图片列表到缺陷对象
        defectPage.getRecords().forEach(d -> {
            List<DefectImage> images = imgMap.get(d.getId());
            d.setImages(images != null ? images : new ArrayList<>());
        });

        return defectPage;
    }

    /**
     * 根据查询条件构造 MyBatis-Plus 查询 Wrapper
     */
    private LambdaQueryWrapper<Defect> buildWrapper(DefectQuery query) {
        LambdaQueryWrapper<Defect> wrapper = new LambdaQueryWrapper<>();

//        if (StrUtil.isNotBlank(query.getKeyword())) {
//            wrapper.like(Defect::getDescription, query.getKeyword());
//        }

        if (StrUtil.isNotBlank(query.getType())) {
            wrapper.eq(Defect::getType, DefectTypeEnum.fromDbValue(query.getType()));
        }

        if (StrUtil.isNotBlank(query.getStatus())) {
            wrapper.eq(Defect::getStatus, DefectStatusEnum.fromDbValue(query.getStatus()));
        }

        if (StrUtil.isNotBlank(query.getSeverity())) {
            wrapper.eq(Defect::getSeverity, SeverityLevelEnum.fromDbValue(query.getSeverity()));
        }

        if (StrUtil.isNotBlank(query.getTaskId())) {
            wrapper.eq(Defect::getTaskId, query.getTaskId());
        }

        if (query.getIsValid() != null) {
            wrapper.eq(Defect::getIsValid, query.getIsValid());
        }

        if (query.getStartTime() != null) {
            wrapper.ge(Defect::getFoundTime, query.getStartTime());
        }

        if (query.getEndTime() != null) {
            wrapper.le(Defect::getFoundTime, query.getEndTime());
        }

        return wrapper;
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
                defect.setIsValid(true);
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




