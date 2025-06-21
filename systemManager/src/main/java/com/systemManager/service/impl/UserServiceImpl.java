package com.systemManager.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.User;
import com.systemManager.mapper.UserMapper;
import com.systemManager.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public String saveUser(AddUserDTO addUserDTO) {
        return "";
    }

    @Override
    public PageDTO<UserVO> listUser(UserQuery userQuery) {
        Page<UserVO> page = new Page<>(userQuery.getPageIndex(), userQuery.getPageSize());
        Page<UserVO> userPage = userMapper.selectUser(userQuery, page);
        return PageDTO.create(userPage);
    }
}
