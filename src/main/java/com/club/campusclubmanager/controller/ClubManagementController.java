package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
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
 * 社团管理控制器（社团负责人）
 */
@Tag(name = "社团管理（负责人）", description = "社团负责人管理社团的相关接口")
@RestController
@RequestMapping("/club/management")
@RequiredArgsConstructor
@SaCheckLogin
public class ClubManagementController {

    private final ClubService clubService;

    /**
     * 查询指定社团的待审核申请列表
     */
    @Operation(summary = "查询待审核申请列表", description = "需要社团负责人权限，查看自己管理的社团的待审核申请")
    @GetMapping("/{clubId}/applications/pending")
    public Result<Page<ClubApplicationVO>> getClubPendingApplications(
            @Parameter(description = "社团ID") @PathVariable Long clubId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<ClubApplicationVO> page = clubService.getClubPendingApplications(clubId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 审核社团申请
     */
    @Operation(summary = "审核社团申请", description = "需要社团负责人权限，审核通过或拒绝用户的加入申请")
    @PostMapping("/{clubId}/applications/review")
    public Result<Void> reviewApplication(
            @Parameter(description = "社团ID") @PathVariable Long clubId,
            @Valid @RequestBody ReviewApplicationRequest request
    ) {
        clubService.reviewApplicationByLeader(clubId, request);
        return Result.success("审核完成", null);
    }

    /**
     * 更新社团信息
     */
    @Operation(summary = "更新社团信息", description = "需要社团负责人权限，修改自己管理的社团的基本信息")
    @PutMapping("/{clubId}")
    public Result<Void> updateClub(
            @Parameter(description = "社团ID") @PathVariable Long clubId,
            @Valid @RequestBody UpdateClubRequest request
    ) {
        clubService.updateClubByLeader(clubId, request);
        return Result.success("更新成功", null);
    }
}
