package com.systemManager.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.systemManager.entity.User;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MenuMapper menuMapper;
    
    /**
     * 根据用户名加载用户
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username));
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        // 查询用户权限
        Set<String> permissions = menuMapper.selectPermsByUserId(user.getUserId());
        
        // 设置用户权限
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        user.setAuthorities(authorities);
        
        return user;
    }
} 