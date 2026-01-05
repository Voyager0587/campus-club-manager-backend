package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.common.PageResult;
import com.club.campusclubmanager.dto.ApplyJoinClubRequest;
import com.club.campusclubmanager.service.ClubService;
import com.club.campusclubmanager.vo.ClubApplicationVO;
import com.club.campusclubmanager.vo.ClubDetailVO;
import com.club.campusclubmanager.vo.ClubMemberVO;
import com.club.campusclubmanager.vo.ClubVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社团控制器（用户端）
 */
@Tag(name = "社团管理（用户端）", description = "社团浏览、加入、查看等接口")
@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
@SaCheckLogin
public class ClubController {

    private final ClubService clubService;

    /**
     * 分页查询社团列表
     */
    @Operation(summary = "分页查询社团列表", description = "公开接口，支持关键词搜索")
    @GetMapping("/list")
    public Result<PageResult<ClubVO>> listClubs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword
    ) {
        Page<ClubVO> page = clubService.listClubs(pageNum, pageSize, keyword);
        PageResult<ClubVO> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        result.setPages(page.getPages());
        return Result.success(result);
    }

    /**
     * 查询社团详情
     */
    @Operation(summary = "查询社团详情", description = "公开接口，查看社团详细信息")
    @GetMapping("/{id}")
    public Result<ClubDetailVO> getClubDetail(
            @Parameter(description = "社团ID") @PathVariable Long id
    ) {
        ClubDetailVO detail = clubService.getClubDetail(id);
        return Result.success(detail);
    }

    /**
     * 查询用户加入的社团列表
     */
    @Operation(summary = "查询我加入的社团", description = "需要登录，查看当前用户已加入的社团列表")
    @GetMapping("/my")
    public Result<List<ClubVO>> getMyClubs() {
        List<ClubVO> clubs = clubService.getMyClubs();
        return Result.success(clubs);
    }

    /**
     * 申请加入社团
     */
    @Operation(summary = "申请加入社团", description = "需要登录，提交加入社团的申请")
    @PostMapping("/apply")
    public Result<Void> applyJoinClub(@Valid @RequestBody ApplyJoinClubRequest request) {
        clubService.applyJoinClub(request);
        return Result.success("申请已提交，请等待审核", null);
    }

    /**
     * 查询社团成员列表
     */
    @Operation(summary = "查询社团成员列表", description = "公开接口，查看社团的成员列表")
    @GetMapping("/{id}/members")
    public Result<Page<ClubMemberVO>> getClubMembers(
            @Parameter(description = "社团ID") @PathVariable Long id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<ClubMemberVO> page = clubService.getClubMembers(id, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询用户的申请记录
     */
    @Operation(summary = "查询我的申请记录", description = "需要登录，查看当前用户的所有社团申请记录")
    @GetMapping("/my/applications")
    public Result<List<ClubApplicationVO>> getMyApplications() {
        List<ClubApplicationVO> applications = clubService.getMyApplications();
        return Result.success(applications);
    }
}
