package com.systemManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.entity.Dept;
import com.systemManager.mapper.DeptMapper;
import com.systemManager.mapper.ms.DeptMsMapper;
import com.systemManager.service.IDeptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
@Slf4j
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {
    @Resource
    private DeptMapper deptMapper;

    @Autowired
    @Qualifier("deptMsMapperImpl")
    private DeptMsMapper msMapper;

    @Override
    public PageDTO<DeptTreeVO> listDept(DeptQuery deptQuery) {
        // 构建查询条件
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<Dept>()
                .eq(deptQuery.getStatus() != null, Dept::getStatus, deptQuery.getStatus())
                .like(StringUtils.hasText(deptQuery.getDeptName()), Dept::getDeptName, deptQuery.getDeptName())
                //.eq(deptQuery.getParentId() != null, Dept::getParentId, deptQuery.getParentId())
                .orderByAsc(Dept::getParentId, Dept::getOrderNum);

        // 分页查询原始部门数据
        Page<Dept> page = new Page<>(deptQuery.getPageIndex(), deptQuery.getPageSize());
        page = deptMapper.selectPage(page, queryWrapper);

        // 转换为树形结构
        List<DeptTreeVO> deptTree = buildDeptTree(page.getRecords());

        // 构建分页结果
        PageDTO<DeptTreeVO> pageResult = new PageDTO<>();
        pageResult.setPageIndex(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setRows(deptTree);
        return pageResult;
    }


    @Override
    @Transactional
    public String saveDept(DeptDTO dto) {
        log.info("saveDept 参数: deptName={}, parentId={}", dto.getDeptName(), dto.getParentId());
        // 处理 parentId 为 null 的情况（默认为 0）
        Long parentId = dto.getParentId() != null ? dto.getParentId() : 0L;
        // 校验部门名称唯一性
        if (!checkDeptNameUnique(dto.getDeptName(), dto.getParentId(), null)) {
            throw new IllegalArgumentException("同级部门名称已存在");
        }
        // 检查部门编码唯一性
        checkDeptCodeUnique(dto.getDeptCode());
        checkParentDeptExist(dto.getParentId());

        // 转换并保存
        log.info("转换前的 DTO: {}", dto);
        Dept dept = msMapper.dtoToDo(dto);
        log.info("转换后的 Dept: {}", dept);
        if(deptMapper.insert(dept) != 1){
            throw new RuntimeException("添加部门失败");
        }
        return dept.getDeptId().toString();
    }

    @Override
    @Transactional
    public String updateDept(Long id, DeptDTO dto) {
        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new IllegalArgumentException("部门不存在");
        }
        // 校验部门名称唯一性
        if (!checkDeptNameUnique(dto.getDeptName(), dto.getParentId(), id)) {
            throw new IllegalArgumentException("同级部门名称已存在");
        }
        // 检查部门编码唯一性
        checkDeptCodeUnique(dto.getDeptCode(), id);
        checkParentDeptExist(dto.getParentId());

        dept = msMapper.dtoToDo(dto);
        dept.setDeptId(id);
        if (deptMapper.updateById(dept) == 0) {
            throw new RuntimeException("修改部门失败");
        }
        return id.toString();
    }

    @Override
    @Transactional
    public String removeDept(Long id) {
        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new IllegalArgumentException("部门不存在");
        }
        // 检查是否有用户
        if (checkDeptHasUser(id)) {
            throw new IllegalArgumentException("部门下存在用户，不能删除");
        }

        // 检查是否有子部门
        if (deptMapper.countChildrenByParentId(id) > 0) {
            throw new IllegalArgumentException("部门下存在子部门，不能删除");
        }

        if (deptMapper.deleteById(dept) == 0) {
            throw new RuntimeException("删除部门失败");
        }

        return id.toString();
    }

    /**
     * 构建部门树形结构
     */
    private List<DeptTreeVO> buildDeptTree(List<Dept> deptList) {
        // 按父部门ID分组
        Map<Long, List<Dept>> deptMap = deptList.stream()
                .collect(Collectors.groupingBy(Dept::getParentId));

        // 构建树形结构
        return deptMap.getOrDefault(0L, Collections.emptyList()).stream()
                .map(dept -> convertToTreeVO(dept, deptMap))
                .sorted(Comparator.comparing(DeptTreeVO::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 递归转换部门为树形VO
     */
    private DeptTreeVO convertToTreeVO(Dept dept, Map<Long, List<Dept>> deptMap) {
        // 复制属性
        DeptTreeVO vo = msMapper.doToVo(dept);

        // 递归处理子部门
        List<Dept> children = deptMap.get(dept.getDeptId());
        if (children != null && !children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(child -> convertToTreeVO(child, deptMap))
                    .sorted(Comparator.comparing(DeptTreeVO::getOrderNum))
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    // 检查部门名称是否唯一
    private boolean checkDeptNameUnique(String deptName, Long parentId, Long excludeDeptId) {
        return deptMapper.checkDeptNameUnique(deptName, parentId, excludeDeptId) == 0;
    }

    // 检查部门编码是否唯一
    private void checkDeptCodeUnique(String deptCode) {
        checkDeptCodeUnique(deptCode, null);
    }

    private void checkDeptCodeUnique(String deptCode, Long excludeDeptId) {
        if (deptCode == null || deptCode.trim().isEmpty()) {
            throw new IllegalArgumentException("部门编码不能为空");
        }
        Long count = deptMapper.selectCount(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeptCode, deptCode.trim())
                .ne(excludeDeptId != null, Dept::getDeptId, excludeDeptId));
        if (count > 0) {
            throw new IllegalArgumentException(
                    String.format("部门编码'%s'已存在", deptCode.trim())
            );
        }
    }



    // 检查父部门是否存在
    private void checkParentDeptExist(Long parentId) {
        // 0表示顶级部门，无需检查
        if (parentId == 0) {
            return;
        }

        Long count = deptMapper.selectCount(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeptId, parentId));

        if (count == 0) {
            throw new IllegalArgumentException("父部门不存在或已被删除" );
        }
    }

    // 检查部门下是否存在用户
    public boolean checkDeptHasUser(Long deptId) {
        return deptMapper.countUserByDeptId(deptId) > 0;
    }

}
