package com.club.campusclubmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.campusclubmanager.dto.ApplyJoinClubRequest;
import com.club.campusclubmanager.dto.CreateClubRequest;
import com.club.campusclubmanager.dto.ReviewApplicationRequest;
import com.club.campusclubmanager.dto.UpdateClubRequest;
import com.club.campusclubmanager.entity.Club;
import com.club.campusclubmanager.vo.ClubApplicationVO;
import com.club.campusclubmanager.vo.ClubDetailVO;
import com.club.campusclubmanager.vo.ClubMemberVO;
import com.club.campusclubmanager.vo.ClubVO;

import java.util.List;

/**
 * 社团服务接口
 */
public interface ClubService extends IService<Club> {

    /**
     * 创建社团（管理员）
     */
    void createClub(CreateClubRequest request);

    /**
     * 更新社团信息（管理员）
     */
    void updateClub(UpdateClubRequest request);

    /**
     * 删除社团（管理员）
     */
    void deleteClub(Long clubId);

    /**
     * 审核社团申请（管理员）
     */
    void reviewApplication(ReviewApplicationRequest request);

    /**
     * 分页查询社团列表
     */
    Page<ClubVO> listClubs(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 查询社团详情
     */
    ClubDetailVO getClubDetail(Long clubId);

    /**
     * 查询用户加入的社团列表
     */
    List<ClubVO> getMyClubs();

    /**
     * 申请加入社团
     */
    void applyJoinClub(ApplyJoinClubRequest request);

    /**
     * 查询社团成员列表
     */
    Page<ClubMemberVO> getClubMembers(Long clubId, Integer pageNum, Integer pageSize);

    /**
     * 查询待审核的申请列表（管理员）
     */
    Page<ClubApplicationVO> getPendingApplications(Integer pageNum, Integer pageSize);

    /**
     * 查询用户的申请记录
     */
    List<ClubApplicationVO> getMyApplications();

    /**
     * 设置社团负责人（管理员）
     */
    void setClubLeader(Long clubId, Long userId);

    /**
     * 取消社团负责人（管理员）
     */
    void removeClubLeader(Long clubId, Long userId);

    /**
     * 查询指定社团的待审核申请列表（社团管理员）
     */
    Page<ClubApplicationVO> getClubPendingApplications(Long clubId, Integer pageNum, Integer pageSize);

    /**
     * 审核社团申请（社团管理员）
     */
    void reviewApplicationByLeader(Long clubId, ReviewApplicationRequest request);

    /**
     * 更新社团信息（社团管理员）
     */
    void updateClubByLeader(Long clubId, UpdateClubRequest request);
}
