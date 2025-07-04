package com.systemManager.security.filter;

import com.systemManager.entity.User;
import com.systemManager.security.service.UserDetailsServiceImpl;
import com.systemManager.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取请求头中的token
        String token = getTokenFromRequest(request);
        
        // 验证token是否有效
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 从token中获取用户名
            String username = jwtUtils.getUsernameFromToken(token);
            
            // 加载用户信息
            User userDetails = userDetailsService.loadUserByUsername(username);
            
            // 创建认证信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            
            // 设置认证详情
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 将认证信息存入SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取token
     * 
     * @param request HTTP请求
     * @return token字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头中获取token
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 