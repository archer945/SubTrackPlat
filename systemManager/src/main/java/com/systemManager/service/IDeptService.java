package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.entity.Dept;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface IDeptService extends IService<Dept> {

    PageDTO<DeptTreeVO> listDept(DeptQuery deptQuery);

    String saveDept(DeptDTO dto);

    String updateDept(Long id, DeptDTO dto);

    String removeDept(Long id);
    
    /**
     * 获取部门树形列表
     * @return 部门树形列表
     */
    List<DeptTreeVO> getDeptTree();
}
