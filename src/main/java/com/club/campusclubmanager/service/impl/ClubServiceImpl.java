package com.club.campusclubmanager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.campusclubmanager.dto.ApplyJoinClubRequest;
import com.club.campusclubmanager.dto.CreateClubRequest;
import com.club.campusclubmanager.dto.ReviewApplicationRequest;
import com.club.campusclubmanager.dto.UpdateClubRequest;
import com.club.campusclubmanager.entity.Club;
import com.club.campusclubmanager.entity.ClubApplication;
import com.club.campusclubmanager.entity.ClubMember;
import com.club.campusclubmanager.entity.User;
import com.club.campusclubmanager.enums.ApplicationStatus;
import com.club.campusclubmanager.enums.ClubStatus;
import com.club.campusclubmanager.enums.MemberRole;
import com.club.campusclubmanager.exception.BusinessException;
import com.club.campusclubmanager.mapper.ClubApplicationMapper;
import com.club.campusclubmanager.mapper.ClubMapper;
import com.club.campusclubmanager.mapper.ClubMemberMapper;
import com.club.campusclubmanager.mapper.UserMapper;
import com.club.campusclubmanager.service.ClubService;
import com.club.campusclubmanager.vo.ClubApplicationVO;
import com.club.campusclubmanager.vo.ClubDetailVO;
import com.club.campusclubmanager.vo.ClubMemberVO;
import com.club.campusclubmanager.vo.ClubVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社团服务实现
 */
@Service
@RequiredArgsConstructor
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements ClubService {

    private final ClubMemberMapper clubMemberMapper;
    private final ClubApplicationMapper clubApplicationMapper;
    private final UserMapper userMapper;

    /**
     * 创建社团（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createClub(CreateClubRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查社团名称是否已存在
        LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Club::getName, request.getName());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException(10201, "社团名称已存在");
        }

        // 创建社团
        Club club = new Club();
        BeanUtils.copyProperties(request, club);
        club.setCreateUser(userId);
        club.setStatus(ClubStatus.NORMAL); // 管理员创建的社团直接设置为正常状态
        this.save(club);

        // 自动将创建人添加为社团负责人
        ClubMember leader = new ClubMember();
        leader.setClubId(club.getId());
        leader.setUserId(userId);
        leader.setRole(MemberRole.LEADER);
        leader.setJoinTime(LocalDateTime.now());
        clubMemberMapper.insert(leader);
    }

    /**
     * 更新社团信息（管理员）
     */
    @Override
    public void updateClub(UpdateClubRequest request) {
        // 检查社团是否存在
        Club club = this.getById(request.getId());
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 如果修改了名称，检查新名称是否已被其他社团使用
        if (!club.getName().equals(request.getName())) {
            LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Club::getName, request.getName());
            queryWrapper.ne(Club::getId, request.getId());
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException(10201, "社团名称已存在");
            }
        }

        // 更新社团信息
        BeanUtils.copyProperties(request, club);
        this.updateById(club);
    }

    /**
     * 删除社团（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClub(Long clubId) {
        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 逻辑删除社团
        this.removeById(clubId);

        // 同时删除相关的成员记录和申请记录
        LambdaQueryWrapper<ClubMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ClubMember::getClubId, clubId);
        clubMemberMapper.delete(memberWrapper);

        LambdaQueryWrapper<ClubApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.eq(ClubApplication::getClubId, clubId);
        clubApplicationMapper.delete(applicationWrapper);
    }

    /**
     * 审核社团申请（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(ReviewApplicationRequest request) {
        Long reviewerId = StpUtil.getLoginIdAsLong();

        // 查询申请记录
        ClubApplication application = clubApplicationMapper.selectById(request.getApplicationId());
        if (application == null) {
            throw new BusinessException(10204, "申请记录不存在");
        }

        // 检查申请状态
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(10205, "该申请已被处理");
        }

        // 验证审核结果
        ApplicationStatus status;
        try {
            status = ApplicationStatus.fromCode(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(10001, "审核结果不合法");
        }

        if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.REJECTED) {
            throw new BusinessException(10001, "审核结果只能是approved或rejected");
        }

        // 更新申请状态
        application.setStatus(status);
        application.setReviewNote(request.getReviewNote());
        application.setReviewTime(LocalDateTime.now());
        application.setReviewerId(reviewerId);
        clubApplicationMapper.updateById(application);

        // 如果审核通过，将用户添加为社团成员
        if (status == ApplicationStatus.APPROVED) {
            ClubMember member = new ClubMember();
            member.setClubId(application.getClubId());
            member.setUserId(application.getUserId());
            member.setRole(MemberRole.MEMBER);
            member.setJoinTime(LocalDateTime.now());
            clubMemberMapper.insert(member);
        }
    }

    /**
     * 分页查询社团列表
     */
    @Override
    public Page<ClubVO> listClubs(Integer pageNum, Integer pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Club::getStatus, ClubStatus.NORMAL); // 只查询正常状态的社团
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Club::getName, keyword)
                    .or()
                    .like(Club::getDescription, keyword)
            );
        }
        queryWrapper.orderByDesc(Club::getCreateTime);

        // 分页查询
        Page<Club> page = new Page<>(pageNum, pageSize);
        page = this.page(page, queryWrapper);

        // 转换为VO
        Page<ClubVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ClubVO> voList = page.getRecords().stream()
                .map(this::convertToClubVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 查询社团详情
     */
    @Override
    public ClubDetailVO getClubDetail(Long clubId) {
        // 查询社团信息
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 转换为VO
        ClubDetailVO vo = new ClubDetailVO();
        BeanUtils.copyProperties(club, vo);

        // 查询创建者信息
        User creator = userMapper.selectById(club.getCreateUser());
        if (creator != null) {
            vo.setCreateUsername(creator.getUsername());
        }

        // 统计成员数量
        LambdaQueryWrapper<ClubMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ClubMember::getClubId, clubId);
        Long memberCount = clubMemberMapper.selectCount(memberWrapper);
        vo.setMemberCount(memberCount.intValue());

        // 如果用户已登录，查询用户是否已加入该社团
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClubMember::getClubId, clubId)
                   .eq(ClubMember::getUserId, userId);
            ClubMember member = clubMemberMapper.selectOne(wrapper);
            vo.setIsJoined(member != null);
            if (member != null) {
                vo.setMemberRole(member.getRole().getCode());
            }
        } catch (Exception e) {
            // 未登录或获取用户信息失败，设置为未加入
            vo.setIsJoined(false);
        }

        return vo;
    }

    /**
     * 查询用户加入的社团列表
     */
    @Override
    public List<ClubVO> getMyClubs() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户加入的社团ID列表
        LambdaQueryWrapper<ClubMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ClubMember::getUserId, userId);
        List<ClubMember> members = clubMemberMapper.selectList(memberWrapper);

        if (members.isEmpty()) {
            return List.of();
        }

        // 查询社团信息
        List<Long> clubIds = members.stream()
                .map(ClubMember::getClubId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Club> clubWrapper = new LambdaQueryWrapper<>();
        clubWrapper.in(Club::getId, clubIds);
        clubWrapper.orderByDesc(Club::getCreateTime);
        List<Club> clubs = this.list(clubWrapper);

        // 转换为VO
        return clubs.stream()
                .map(this::convertToClubVO)
                .collect(Collectors.toList());
    }

    /**
     * 申请加入社团
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyJoinClub(ApplyJoinClubRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查社团是否存在
        Club club = this.getById(request.getClubId());
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 检查社团状态
        if (club.getStatus() != ClubStatus.NORMAL) {
            throw new BusinessException(10202, "该社团暂不可加入");
        }

        // 检查用户是否已经是社团成员
        LambdaQueryWrapper<ClubMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ClubMember::getClubId, request.getClubId())
                     .eq(ClubMember::getUserId, userId);
        if (clubMemberMapper.selectCount(memberWrapper) > 0) {
            throw new BusinessException(10203, "您已经是该社团成员");
        }

        // 检查是否有待审核的申请
        LambdaQueryWrapper<ClubApplication> applicationWrapper = new LambdaQueryWrapper<>();
        applicationWrapper.eq(ClubApplication::getClubId, request.getClubId())
                          .eq(ClubApplication::getUserId, userId)
                          .eq(ClubApplication::getStatus, ApplicationStatus.PENDING);
        if (clubApplicationMapper.selectCount(applicationWrapper) > 0) {
            throw new BusinessException(10203, "您已提交过申请，请等待审核");
        }

        // 创建申请记录
        ClubApplication application = new ClubApplication();
        application.setClubId(request.getClubId());
        application.setUserId(userId);
        application.setReason(request.getReason());
        application.setStatus(ApplicationStatus.PENDING);
        clubApplicationMapper.insert(application);
    }

    /**
     * 查询社团成员列表
     */
    @Override
    public Page<ClubMemberVO> getClubMembers(Long clubId, Integer pageNum, Integer pageSize) {
        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 分页查询成员
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId);
        wrapper.orderByDesc(ClubMember::getJoinTime);

        Page<ClubMember> page = new Page<>(pageNum, pageSize);
        page = clubMemberMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ClubMemberVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ClubMemberVO> voList = page.getRecords().stream()
                .map(member -> convertToClubMemberVO(member, club))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 查询待审核的申请列表（管理员）
     */
    @Override
    public Page<ClubApplicationVO> getPendingApplications(Integer pageNum, Integer pageSize) {
        // 查询待审核的申请
        LambdaQueryWrapper<ClubApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubApplication::getStatus, ApplicationStatus.PENDING);
        wrapper.orderByAsc(ClubApplication::getCreateTime);

        Page<ClubApplication> page = new Page<>(pageNum, pageSize);
        page = clubApplicationMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ClubApplicationVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ClubApplicationVO> voList = page.getRecords().stream()
                .map(this::convertToApplicationVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 查询用户的申请记录
     */
    @Override
    public List<ClubApplicationVO> getMyApplications() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户的申请记录
        LambdaQueryWrapper<ClubApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubApplication::getUserId, userId);
        wrapper.orderByDesc(ClubApplication::getCreateTime);

        List<ClubApplication> applications = clubApplicationMapper.selectList(wrapper);

        // 转换为VO
        return applications.stream()
                .map(this::convertToApplicationVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为ClubVO
     */
    private ClubVO convertToClubVO(Club club) {
        ClubVO vo = new ClubVO();
        BeanUtils.copyProperties(club, vo);

        // 查询创建者信息
        User creator = userMapper.selectById(club.getCreateUser());
        if (creator != null) {
            vo.setCreateUsername(creator.getUsername());
        }

        // 统计成员数量
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, club.getId());
        Long count = clubMemberMapper.selectCount(wrapper);
        vo.setMemberCount(count.intValue());

        return vo;
    }

    /**
     * 转换为ClubMemberVO
     */
    private ClubMemberVO convertToClubMemberVO(ClubMember member, Club club) {
        ClubMemberVO vo = new ClubMemberVO();
        BeanUtils.copyProperties(member, vo);

        // 设置社团信息
        vo.setClubName(club.getName());

        // 查询用户信息
        User user = userMapper.selectById(member.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
        }

        return vo;
    }

    /**
     * 转换为ClubApplicationVO
     */
    private ClubApplicationVO convertToApplicationVO(ClubApplication application) {
        ClubApplicationVO vo = new ClubApplicationVO();
        BeanUtils.copyProperties(application, vo);

        // 查询社团信息
        Club club = this.getById(application.getClubId());
        if (club != null) {
            vo.setClubName(club.getName());
        }

        // 查询申请人信息
        User user = userMapper.selectById(application.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
        }

        // 查询审核人信息
        if (application.getReviewerId() != null) {
            User reviewer = userMapper.selectById(application.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getRealName());
            }
        }

        return vo;
    }

    /**
     * 设置社团负责人（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setClubLeader(Long clubId, Long userId) {
        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10101, "用户不存在");
        }

        // 检查用户是否已是社团成员
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId);
        ClubMember member = clubMemberMapper.selectOne(wrapper);

        if (member == null) {
            // 用户不是成员，直接添加为负责人
            ClubMember newLeader = new ClubMember();
            newLeader.setClubId(clubId);
            newLeader.setUserId(userId);
            newLeader.setRole(MemberRole.LEADER);
            newLeader.setJoinTime(LocalDateTime.now());
            clubMemberMapper.insert(newLeader);
        } else if (member.getRole() == MemberRole.LEADER) {
            // 用户已是负责人
            throw new BusinessException(10206, "该用户已是社团负责人");
        } else {
            // 用户是普通成员，升级为负责人
            member.setRole(MemberRole.LEADER);
            clubMemberMapper.updateById(member);
        }
    }

    /**
     * 取消社团负责人（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeClubLeader(Long clubId, Long userId) {
        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 检查用户是否是社团成员
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId);
        ClubMember member = clubMemberMapper.selectOne(wrapper);

        if (member == null) {
            throw new BusinessException(10207, "该用户不是社团成员");
        }

        if (member.getRole() != MemberRole.LEADER) {
            throw new BusinessException(10208, "该用户不是社团负责人");
        }

        // 将负责人降级为普通成员
        member.setRole(MemberRole.MEMBER);
        clubMemberMapper.updateById(member);
    }

    /**
     * 查询指定社团的待审核申请列表（社团管理员）
     */
    @Override
    public Page<ClubApplicationVO> getClubPendingApplications(Long clubId, Integer pageNum, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证用户是否为该社团的负责人
        verifyClubLeader(clubId, userId);

        // 查询该社团的待审核申请
        LambdaQueryWrapper<ClubApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubApplication::getClubId, clubId)
               .eq(ClubApplication::getStatus, ApplicationStatus.PENDING)
               .orderByAsc(ClubApplication::getCreateTime);

        Page<ClubApplication> page = new Page<>(pageNum, pageSize);
        page = clubApplicationMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ClubApplicationVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ClubApplicationVO> voList = page.getRecords().stream()
                .map(this::convertToApplicationVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 审核社团申请（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplicationByLeader(Long clubId, ReviewApplicationRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证用户是否为该社团的负责人
        verifyClubLeader(clubId, userId);

        // 查询申请记录
        ClubApplication application = clubApplicationMapper.selectById(request.getApplicationId());
        if (application == null) {
            throw new BusinessException(10204, "申请记录不存在");
        }

        // 验证申请是否属于该社团
        if (!application.getClubId().equals(clubId)) {
            throw new BusinessException(10209, "无权审核其他社团的申请");
        }

        // 检查申请状态
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(10205, "该申请已被处理");
        }

        // 验证审核结果
        ApplicationStatus status;
        try {
            status = ApplicationStatus.fromCode(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(10001, "审核结果不合法");
        }

        if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.REJECTED) {
            throw new BusinessException(10001, "审核结果只能是approved或rejected");
        }

        // 更新申请状态
        application.setStatus(status);
        application.setReviewNote(request.getReviewNote());
        application.setReviewTime(LocalDateTime.now());
        application.setReviewerId(userId);
        clubApplicationMapper.updateById(application);

        // 如果审核通过，将用户添加为社团成员
        if (status == ApplicationStatus.APPROVED) {
            ClubMember member = new ClubMember();
            member.setClubId(application.getClubId());
            member.setUserId(application.getUserId());
            member.setRole(MemberRole.MEMBER);
            member.setJoinTime(LocalDateTime.now());
            clubMemberMapper.insert(member);
        }
    }

    /**
     * 更新社团信息（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClubByLeader(Long clubId, UpdateClubRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证用户是否为该社团的负责人
        verifyClubLeader(clubId, userId);

        // 验证请求中的clubId与路径参数一致
        if (!request.getId().equals(clubId)) {
            throw new BusinessException(10001, "参数错误");
        }

        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 如果修改了名称，检查新名称是否已被其他社团使用
        if (!club.getName().equals(request.getName())) {
            LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Club::getName, request.getName());
            queryWrapper.ne(Club::getId, clubId);
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException(10201, "社团名称已存在");
            }
        }

        // 更新社团信息（社团管理员不能修改状态）
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setLogo(request.getLogo());
        this.updateById(club);
    }

    /**
     * 验证用户是否为指定社团的负责人
     */
    private void verifyClubLeader(Long clubId, Long userId) {
        // 检查社团是否存在
        Club club = this.getById(clubId);
        if (club == null) {
            throw new BusinessException(10201, "社团不存在");
        }

        // 检查用户是否是该社团的负责人
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
               .eq(ClubMember::getUserId, userId)
               .eq(ClubMember::getRole, MemberRole.LEADER);
        ClubMember member = clubMemberMapper.selectOne(wrapper);

        if (member == null) {
            throw new BusinessException(10210, "您不是该社团的负责人，无权进行此操作");
        }
    }
}
