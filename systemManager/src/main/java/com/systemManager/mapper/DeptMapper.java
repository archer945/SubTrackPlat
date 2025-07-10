package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.systemManager.DeptUserDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.Dept;
import org.apache.ibatis.annotations.Param;


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

    /**
     * 根据部门名称查询部门
     * @param deptName 部门名称
     * @return 部门信息
     */
    Dept selectByDeptName(@Param("deptName") String deptName);
    
    /**
     * 查询部门人员数量
     * @param deptId 部门ID
     * @return 人员数量
     */
    Long countDeptUserById(Long deptId);
    
    /**
     * 获取部门人员列表
     * @param deptId 部门ID
     * @param username 用户名（可选，用于搜索）
     * @param page 分页信息
     * @return 部门人员列表
     */
    Page<UserVO> selectUsersByDeptId(@Param("deptId") Long deptId, @Param("username") String username, Page<UserVO> page);
    
    /**
     * 获取部门人员信息（包含数量和ID列表）
     * @param deptId 部门ID
     * @return 部门人员信息
     */
    DeptUserDTO getDeptUserInfo(Long deptId);
}
