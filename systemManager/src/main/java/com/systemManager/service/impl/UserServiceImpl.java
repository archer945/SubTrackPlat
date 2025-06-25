package com.systemManager.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.User;
import com.systemManager.mapper.MsUserMapper;
import com.systemManager.mapper.UserMapper;
import com.systemManager.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    @Qualifier("msUserMapperImpl")
    private MsUserMapper msMapper;

    @Override
    @Transactional
    public String saveUser(AddUserDTO addUserDTO) {
        if(userMapper.selectUserByUsername(addUserDTO.getUsername())){
            throw new IllegalArgumentException("用户名已存在");
        }
        // 转换为DO
        User user = msMapper.addDtoToDo(addUserDTO);
        user.setStatus(1);
        // 插入
        if(userMapper.insert(user) != 1){
            throw new RuntimeException("添加用户数据失败");
        }

        return user.getUserId().toString();
    }

    @Override
    public PageDTO<UserVO> listUser(UserQuery userQuery) {
        // 分页对象
        Page<UserVO> page = new Page<>(userQuery.getPageIndex(), userQuery.getPageSize());
        // 查询
        Page<UserVO> userPage = userMapper.selectUser(userQuery, page);
        return PageDTO.create(userPage);
    }



    @Override
    @Transactional
    public String removeUser(Long id) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(2);
        if (userMapper.updateById(user) == 0) {
            throw new RuntimeException("删除用户失败");
        }
        return id.toString();
    }

    @Override
    public String updateUser(Long id, UpdateUserDTO dto) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        user = msMapper.dtoToDo(dto);
        if (userMapper.updateById(user) == 0) {
            throw new RuntimeException("修改用户失败");
        }
        return id.toString();
    }
}
