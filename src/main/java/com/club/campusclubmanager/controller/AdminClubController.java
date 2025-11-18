package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.dto.CreateClubRequest;
import com.club.campusclubmanager.dto.ReviewApplicationRequest;
import com.club.campusclubmanager.dto.UpdateClubRequest;
import com.club.campusclubmanager.service.ClubService;
import com.club.campusclubmanager.vo.ClubApplicationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 社团管理控制器（管理端）
 */
@Tag(name = "社团管理（管理端）", description = "社团创建、审核、管理等接口")
@RestController
@RequestMapping("/admin/club")
@RequiredArgsConstructor
@SaCheckRole("system_admin")
public class AdminClubController {

    private final ClubService clubService;

    /**
     * 创建社团
     */
    @Operation(summary = "创建社团", description = "需要管理员权限，创建新的社团")
    @PostMapping("/create")
    public Result<Void> createClub(@Valid @RequestBody CreateClubRequest request) {
        clubService.createClub(request);
        return Result.success("社团创建成功", null);
    }

    /**
     * 更新社团信息
     */
    @Operation(summary = "更新社团信息", description = "需要管理员权限，修改社团基本信息")
    @PutMapping("/update")
    public Result<Void> updateClub(@Valid @RequestBody UpdateClubRequest request) {
        clubService.updateClub(request);
        return Result.success("更新成功", null);
    }

    /**
     * 删除社团
     */
    @Operation(summary = "删除社团", description = "需要管理员权限，删除社团及相关数据")
    @DeleteMapping("/{id}")
    public Result<Void> deleteClub(
            @Parameter(description = "社团ID") @PathVariable Long id
    ) {
        clubService.deleteClub(id);
        return Result.success("删除成功", null);
    }

    /**
     * 查询待审核的申请列表
     */
    @Operation(summary = "查询待审核申请列表", description = "需要管理员权限，查看所有待审核的社团加入申请")
    @GetMapping("/applications/pending")
    public Result<Page<ClubApplicationVO>> getPendingApplications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<ClubApplicationVO> page = clubService.getPendingApplications(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 审核社团申请
     */
    @Operation(summary = "审核社团申请", description = "需要管理员权限，审核通过或拒绝用户的加入申请")
    @PostMapping("/applications/review")
    public Result<Void> reviewApplication(@Valid @RequestBody ReviewApplicationRequest request) {
        clubService.reviewApplication(request);
        return Result.success("审核完成", null);
    }

    /**
     * 设置社团负责人
     */
    @Operation(summary = "设置社团负责人", description = "需要管理员权限，将指定用户设置为社团负责人")
    @PostMapping("/{clubId}/leader/{userId}")
    public Result<Void> setClubLeader(
            @Parameter(description = "社团ID") @PathVariable Long clubId,
            @Parameter(description = "用户ID") @PathVariable Long userId
    ) {
        clubService.setClubLeader(clubId, userId);
        return Result.success("设置成功", null);
    }

    /**
     * 取消社团负责人
     */
    @Operation(summary = "取消社团负责人", description = "需要管理员权限，取消指定用户的社团负责人身份")
    @DeleteMapping("/{clubId}/leader/{userId}")
    public Result<Void> removeClubLeader(
            @Parameter(description = "社团ID") @PathVariable Long clubId,
            @Parameter(description = "用户ID") @PathVariable Long userId
    ) {
        clubService.removeClubLeader(clubId, userId);
        return Result.success("取消成功", null);
    }
}
