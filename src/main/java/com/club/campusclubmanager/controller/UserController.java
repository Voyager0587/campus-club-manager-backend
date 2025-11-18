package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.dto.LoginRequest;
import com.club.campusclubmanager.dto.RegisterRequest;
import com.club.campusclubmanager.dto.UpdateUserRequest;
import com.club.campusclubmanager.service.UserService;
import com.club.campusclubmanager.vo.LoginResponse;
import com.club.campusclubmanager.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "公开接口，用户自助注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "公开接口，用户登录获取Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "需要登录，获取当前登录用户的详细信息")
    @SaCheckRole("user")
    @GetMapping("/info")
    public Result<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfo = userService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "需要登录，更新当前用户的个人资料")
    @SaCheckRole("user")
    @PostMapping(value ="/update")
    public Result<Void> updateUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        userService.updateUserInfo(request);
        return Result.success("更新成功", null);
    }
}
