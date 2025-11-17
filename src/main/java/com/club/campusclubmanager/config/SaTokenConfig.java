package com.club.campusclubmanager.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.club.campusclubmanager.entity.User;
import com.club.campusclubmanager.enums.UserRole;
import com.club.campusclubmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token配置类
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer, StpInterface {

    private final UserService userService;

    /**
     * 注册Sa-Token拦截器
     * 注意：由于context-path已设置为/api，这里的路径不包含/api前缀
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/doc.html/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                );
    }

    /**
     * 获取用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本系统暂不使用细粒度权限，返回空列表
        return new ArrayList<>();
    }

    /**
     * 获取用户角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();

        // 从数据库查询用户角色
        Long userId;
        if (loginId instanceof String) {
            userId = Long.parseLong((String) loginId);
        } else if (loginId instanceof Long) {
            userId = (Long) loginId;
        } else {
            return roles; // 无法识别的类型，返回空列表
        }

        User user = userService.getById(userId);
        if (user != null && user.getRole() != null) {
            roles.add(user.getRole().getCode());
        }

        return roles;
    }
}

