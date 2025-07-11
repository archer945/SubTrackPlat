package com.systemManager.security.filter;

import com.systemManager.entity.User;
import com.systemManager.security.service.UserDetailsServiceImpl;
import com.systemManager.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取请求头中的token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            log.info("获取到token: {}", token);
        } else {
            log.info("请求头中没有token");
        }
        
        // 验证token是否有效
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            try {
                // 从token中获取用户名
                String username = jwtUtils.getUsernameFromToken(token);
                log.info("从token获取的用户名: {}", username);
                
                // 从token中获取用户ID
                Long userId = jwtUtils.getUserIdFromToken(token);
                log.info("从token获取的用户ID: {}", userId);
                
                // 加载用户信息
                User userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails == null) {
                    log.error("无法加载用户信息: {}", username);
                } else {
                    log.info("成功加载用户信息: {}, 用户ID: {}", username, userDetails.getUserId());
                    
                    // 确保用户ID与token中的一致
                    if (userId != null) {
                        userDetails.setUserId(userId);
                        log.info("设置token中的用户ID: {}", userId);
                    }
                    
                    // 创建认证信息
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    // 设置认证详情
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 将认证信息存入SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("成功设置认证信息");
                }
            } catch (Exception e) {
                // 如果解析token异常，不做认证
                log.error("JWT认证失败", e);
            }
        } else if (StringUtils.hasText(token)) {
            log.warn("token无效");
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