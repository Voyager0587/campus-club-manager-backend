package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.service.ActivityService;
import com.club.campusclubmanager.vo.ActivityDetailVO;
import com.club.campusclubmanager.vo.ActivitySignupVO;
import com.club.campusclubmanager.vo.ActivityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动控制器（学生端）
 */
@Tag(name = "活动管理（学生端）", description = "活动浏览、报名、取消报名等接口")
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 浏览活动列表
     */
    @Operation(summary = "浏览活动列表", description = "公开接口，支持关键词搜索和社团筛选")
    @GetMapping("/list")
    public Result<Page<ActivityVO>> listActivities(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "社团ID") @RequestParam(required = false) Long clubId
    ) {
        Page<ActivityVO> page = activityService.listActivities(pageNum, pageSize, keyword, clubId);
        return Result.success(page);
    }

    /**
     * 查看活动详情
     */
    @Operation(summary = "查看活动详情", description = "公开接口，查看活动详细信息")
    @GetMapping("/{id}")
    public Result<ActivityDetailVO> getActivityDetail(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        ActivityDetailVO detail = activityService.getActivityDetail(id);
        return Result.success(detail);
    }

    /**
     * 报名活动
     */
    @Operation(summary = "报名活动", description = "需要登录，用户报名参加活动")
    @SaCheckRole("user")
    @PostMapping("/{id}/signup")
    public Result<Void> signupActivity(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        activityService.signupActivity(id);
        return Result.success("报名成功", null);
    }

    /**
     * 取消报名
     */
    @Operation(summary = "取消报名", description = "需要登录，用户取消活动报名")
    @SaCheckRole("user")
    @DeleteMapping("/{id}/signup")
    public Result<Void> cancelSignup(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        activityService.cancelSignup(id);
        return Result.success("取消报名成功", null);
    }

    /**
     * 查看我的报名记录
     */
    @Operation(summary = "查看我的报名记录", description = "需要登录，查看当前用户的所有活动报名记录")
    @SaCheckRole("user")
    @GetMapping("/my-signups")
    public Result<List<ActivitySignupVO>> getMySignups() {
        List<ActivitySignupVO> signups = activityService.getMySignups();
        return Result.success(signups);
    }
}
