package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.entity.Dept;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface DeptMapper extends BaseMapper<Dept> {

    Page<DeptTreeVO> selectDepts(DeptQuery deptQuery, Page<DeptTreeVO> page);

    int checkDeptNameUnique(String deptName, Long parentId, Long excludeDeptId);

    int countUserByDeptId(Long deptId);

    int countChildrenByParentId(Long id);
}
