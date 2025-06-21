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
import com.systemManager.service.MsUserMapper;
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

    @Resource
    private MsUserMapper msMapper;

    @Override
    public String saveUser(AddUserDTO addUserDTO) {
        User user = msMapper.addDtoToDo(addUserDTO);
        user.setStatus(1);
        if(userMapper.insert(user) != 1){
            throw new RuntimeException("添加用户数据失败");
        }

        return user.getUserId().toString();
    }

    @Override
    public PageDTO<UserVO> listUser(UserQuery userQuery) {
        Page<UserVO> page = new Page<>(userQuery.getPageIndex(), userQuery.getPageSize());
        Page<UserVO> userPage = userMapper.selectUser(userQuery, page);
        return PageDTO.create(userPage);
    }
}
