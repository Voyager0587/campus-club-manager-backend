package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.dto.CheckinRequest;
import com.club.campusclubmanager.dto.CreateActivityRequest;
import com.club.campusclubmanager.dto.UpdateActivityRequest;
import com.club.campusclubmanager.service.ActivityService;
import com.club.campusclubmanager.vo.ActivitySignupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 活动控制器（社团管理员）
 */
@Tag(name = "活动管理（社团管理员）", description = "社团负责人创建、管理活动等接口")
@RestController
@RequestMapping("/club-admin/activity")
@RequiredArgsConstructor
@SaCheckRole("user")  // 至少需要user角色，具体权限��Service层验证
public class ClubActivityController {

    private final ActivityService activityService;

    /**
     * 创建活动
     */
    @Operation(summary = "创建活动", description = "社团负责人创建活动")
    @PostMapping("/create")
    public Result<Void> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        activityService.createActivity(request);
        return Result.success("活动创建成功，等待系统管理员审核", null);
    }

    /**
     * 更新活动
     */
    @Operation(summary = "更新活动", description = "社团负责人更新活动信息")
    @PutMapping("/{id}")
    public Result<Void> updateActivity(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Valid @RequestBody UpdateActivityRequest request
    ) {
        activityService.updateActivity(id, request);
        return Result.success("活动更新成功", null);
    }

    /**
     * 取消活动
     */
    @Operation(summary = "取消活动", description = "社团负责人取消活动")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelActivity(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        activityService.cancelActivity(id);
        return Result.success("活动已取消", null);
    }

    /**
     * 查看活动报名列表
     */
    @Operation(summary = "查看活动报名列表", description = "社团负责人查看活动的所有报名人员")
    @GetMapping("/{id}/signups")
    public Result<Page<ActivitySignupVO>> getActivitySignups(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<ActivitySignupVO> page = activityService.getActivitySignups(id, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 活动签到/标记缺���
     */
    @Operation(summary = "活动签到/标记缺席", description = "社团负责人为报名用户签到或标记缺席")
    @PostMapping("/{id}/checkin")
    public Result<Void> checkinActivity(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Valid @RequestBody CheckinRequest request
    ) {
        activityService.checkinActivity(id, request);
        return Result.success("操作成功", null);
    }
}
