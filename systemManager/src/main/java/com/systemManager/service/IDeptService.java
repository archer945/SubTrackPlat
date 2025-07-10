package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.dto.systemManager.DeptUserDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.common.domain.vo.systemManager.UserVO;
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
    
    /**
     * 获取部门人员数量
     * @param deptId 部门ID
     * @return 人员数量
     */
    Long getDeptUserCount(Long deptId);
    
    /**
     * 获取部门人员列表
     * @param deptId 部门ID
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param username 用户名(可选，用于搜索)
     * @return 分页后的部门人员列表
     */
    PageDTO<UserVO> getDeptUsers(Long deptId, Integer pageIndex, Integer pageSize, String username);
    
    /**
     * 获取部门人员统计信息
     * @param deptId 部门ID
     * @return 部门人员统计信息
     */
    DeptUserDTO getDeptUserInfo(Long deptId);
}
