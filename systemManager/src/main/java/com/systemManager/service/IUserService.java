package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.BatchUserDTO;
import com.common.domain.dto.systemManager.ResetPasswordDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    
    /**
     * 重置用户密码
     * @param id 用户 ID
     * @param dto 密码信息
     * @return 用户 ID
     */
    String resetPassword(Long id, ResetPasswordDTO dto);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
    
    /**
     * 批量删除用户
     * @param dto 包含用户ID列表的DTO
     * @return 操作结果
     */
    String batchRemoveUsers(BatchUserDTO dto);
    
    /**
     * 导入用户
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importUsers(MultipartFile file) throws IOException;
    
    /**
     * 导出用户
     * @param userQuery 用户查询条件
     * @param response HTTP响应
     */
    void exportUsers(UserQuery userQuery, HttpServletResponse response) throws IOException;
}
