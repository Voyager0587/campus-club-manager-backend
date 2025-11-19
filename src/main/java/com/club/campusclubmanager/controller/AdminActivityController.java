package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.dto.ReviewActivityRequest;
import com.club.campusclubmanager.service.ActivityService;
import com.club.campusclubmanager.vo.ActivityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 活动控制器（系统管理员）
 */
@Tag(name = "活动管理（系统管理员）", description = "系统管理员审核、管理所有活动")
@RestController
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
@SaCheckRole("system_admin")  // 需要系统管理员角色
public class AdminActivityController {

    private final ActivityService activityService;

    /**
     * 查看所有活动
     */
    @Operation(summary = "查看所有活动", description = "系统管理员查看所有活动，支持关键词和状态筛选")
    @GetMapping("/list")
    public Result<Page<ActivityVO>> getAllActivities(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "活动状态") @RequestParam(required = false) String status
    ) {
        Page<ActivityVO> page = activityService.getAllActivities(pageNum, pageSize, keyword, status);
        return Result.success(page);
    }

    /**
     * 审核活动
     */
    @Operation(summary = "审核活动", description = "系统管理员审核活动（通过/拒绝）")
    @PutMapping("/{id}/review")
    public Result<Void> reviewActivity(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Valid @RequestBody ReviewActivityRequest request
    ) {
        activityService.reviewActivity(id, request);
        return Result.success("审核完成", null);
    }

    /**
     * 删除活动
     */
    @Operation(summary = "删除活动", description = "系统管理员删除活动")
    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        activityService.deleteActivity(id);
        return Result.success("删除成功", null);
    }
}
