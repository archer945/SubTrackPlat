package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.User;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface IUserService extends IService<User> {

    /**
     * 添加用户
     * @param addUserDTO 添加用户信息
     * @return 用户 ID
     */
    String saveUser(AddUserDTO addUserDTO);

    /**
     * 获取用户列表（条件+分页）
     * @param userQuery 用户信息查询条件
     * @return 用户信息列表
     */
    PageDTO<UserVO> listUser(UserQuery userQuery);

    /**
     * 删除用户
     * @param id 用户 ID
     * @return 用户 ID
     */
    String removeUser(Long id);

    /**
     * 更新用户
     * @param id 用户 ID
     * @param dto 更新用户信息
     * @return 用户 ID
     */
    String updateUser(Long id, UpdateUserDTO dto);
}
