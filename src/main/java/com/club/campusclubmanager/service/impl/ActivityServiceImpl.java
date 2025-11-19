package com.club.campusclubmanager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.campusclubmanager.dto.CheckinRequest;
import com.club.campusclubmanager.dto.CreateActivityRequest;
import com.club.campusclubmanager.dto.ReviewActivityRequest;
import com.club.campusclubmanager.dto.UpdateActivityRequest;
import com.club.campusclubmanager.entity.*;
import com.club.campusclubmanager.enums.ActivityStatus;
import com.club.campusclubmanager.enums.MemberRole;
import com.club.campusclubmanager.enums.SignupStatus;
import com.club.campusclubmanager.exception.BusinessException;
import com.club.campusclubmanager.mapper.*;
import com.club.campusclubmanager.service.ActivityService;
import com.club.campusclubmanager.vo.ActivityDetailVO;
import com.club.campusclubmanager.vo.ActivitySignupVO;
import com.club.campusclubmanager.vo.ActivityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务实现
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivitySignupMapper activitySignupMapper;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final UserMapper userMapper;

    // ==================== 社团管理员操作 ====================

    /**
     * 创建活动（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(CreateActivityRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证社团是否存在
        Club club = clubMapper.selectById(request.getClubId());
        if (club == null) {
            throw new BusinessException(20101, "社团不存在");
        }

        // 验证用户是否为该社团的负责人
        verifyClubLeader(request.getClubId(), userId);

        // 验证时间合法性
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException(20102, "活动结束时间不能早于开始时间");
        }

        if (request.getSignupEndTime() != null && request.getSignupStartTime() != null) {
            if (request.getSignupEndTime().isBefore(request.getSignupStartTime())) {
                throw new BusinessException(20102, "报名截止时间不能早于报名开始时间");
            }
        }

        // 创建活动
        Activity activity = new Activity();
        BeanUtils.copyProperties(request, activity);
        activity.setCreateUser(userId);
        activity.setStatus(ActivityStatus.PENDING); // 创建后默认为待审核状态
        activity.setCurrentMembers(0);
        this.save(activity);
    }

    /**
     * 更新活动（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long activityId, UpdateActivityRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 验证用户是否为该社团的负责人
        verifyClubLeader(activity.getClubId(), userId);

        // 只有草稿和待审核状态的活动可以修改
        if (activity.getStatus() != ActivityStatus.DRAFT && activity.getStatus() != ActivityStatus.PENDING) {
            throw new BusinessException(20103, "该状态的活动无法修改");
        }

        // 验证时间合法性
        if (request.getEndTime() != null && request.getStartTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new BusinessException(20102, "活动结束时间不能早于开始时间");
            }
        }

        // 更新活动信息（只更新非空字段）
        if (request.getTitle() != null) activity.setTitle(request.getTitle());
        if (request.getContent() != null) activity.setContent(request.getContent());
        if (request.getCover() != null) activity.setCover(request.getCover());
        if (request.getLocation() != null) activity.setLocation(request.getLocation());
        if (request.getStartTime() != null) activity.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) activity.setEndTime(request.getEndTime());
        if (request.getSignupStartTime() != null) activity.setSignupStartTime(request.getSignupStartTime());
        if (request.getSignupEndTime() != null) activity.setSignupEndTime(request.getSignupEndTime());
        if (request.getMaxMembers() != null) activity.setMaxMembers(request.getMaxMembers());

        this.updateById(activity);
    }

    /**
     * 取消活动（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelActivity(Long activityId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 验证用户是否为该社团的负责人
        verifyClubLeader(activity.getClubId(), userId);

        // 已取消或已完成的活动无法再次取消
        if (activity.getStatus() == ActivityStatus.CANCELLED) {
            throw new BusinessException(20103, "活动已被取消");
        }
        if (activity.getStatus() == ActivityStatus.COMPLETED) {
            throw new BusinessException(20103, "活动已完成，无法取消");
        }

        // 更新活动状态为已取消
        activity.setStatus(ActivityStatus.CANCELLED);
        this.updateById(activity);

        // 将所有已报名未取消的报名记录状态更新为已取消
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, SignupStatus.REGISTERED);
        List<ActivitySignup> signups = activitySignupMapper.selectList(wrapper);
        for (ActivitySignup signup : signups) {
            signup.setStatus(SignupStatus.CANCELLED);
            activitySignupMapper.updateById(signup);
        }
    }

    /**
     * 查看活动报名列表（社团管理员）
     */
    @Override
    public Page<ActivitySignupVO> getActivitySignups(Long activityId, Integer pageNum, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 验证用户是否为该社团的负责人
        verifyClubLeader(activity.getClubId(), userId);

        // 查询报名列表
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .orderByDesc(ActivitySignup::getSignupTime);

        Page<ActivitySignup> page = new Page<>(pageNum, pageSize);
        page = activitySignupMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ActivitySignupVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ActivitySignupVO> voList = page.getRecords().stream()
                .map(this::convertToActivitySignupVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 活动签到/标记缺席（社团管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkinActivity(Long activityId, CheckinRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 验证用户是否为该社团的负责人
        verifyClubLeader(activity.getClubId(), userId);

        // 验证操作类型
        if (!"check_in".equals(request.getAction()) && !"absent".equals(request.getAction())) {
            throw new BusinessException(20104, "无效的操作类型");
        }

        // 批量更新报名状态
        for (Long signupUserId : request.getUserIds()) {
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getUserId, signupUserId);
            ActivitySignup signup = activitySignupMapper.selectOne(wrapper);

            if (signup != null && signup.getStatus() == SignupStatus.REGISTERED) {
                if ("check_in".equals(request.getAction())) {
                    signup.setStatus(SignupStatus.CHECKED_IN);
                    signup.setCheckinTime(LocalDateTime.now());
                } else {
                    signup.setStatus(SignupStatus.ABSENT);
                }
                activitySignupMapper.updateById(signup);
            }
        }
    }

    // ==================== 系统管理员操作 ====================

    /**
     * 查看所有活动（系统管理员）
     */
    @Override
    public Page<ActivityVO> getAllActivities(Integer pageNum, Integer pageSize, String keyword, String status) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Activity::getTitle, keyword)
                    .or()
                    .like(Activity::getContent, keyword));
        }

        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Activity::getStatus, ActivityStatus.fromCode(status));
        }

        wrapper.orderByDesc(Activity::getCreateTime);

        Page<Activity> page = new Page<>(pageNum, pageSize);
        page = this.page(page, wrapper);

        // 转换为VO
        Page<ActivityVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ActivityVO> voList = page.getRecords().stream()
                .map(this::convertToActivityVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 审核活动（系统管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewActivity(Long activityId, ReviewActivityRequest request) {
        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 只有待审核状态的活动可以审核
        if (activity.getStatus() != ActivityStatus.PENDING) {
            throw new BusinessException(20103, "该活动不在待审核状态");
        }

        // 验证审核状态
        if ("published".equals(request.getStatus())) {
            activity.setStatus(ActivityStatus.PUBLISHED);
        } else if ("rejected".equals(request.getStatus())) {
            activity.setStatus(ActivityStatus.CANCELLED);
        } else {
            throw new BusinessException(20104, "无效的审核状态");
        }

        this.updateById(activity);
    }

    /**
     * 删除活动（系统管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long activityId) {
        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 逻辑删除活动
        this.removeById(activityId);

        // 同时删除相关的报名记录
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId);
        activitySignupMapper.delete(wrapper);
    }

    // ==================== 学生端操作 ====================

    /**
     * 浏览活动列表
     */
    @Override
    public Page<ActivityVO> listActivities(Integer pageNum, Integer pageSize, String keyword, Long clubId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 只显示已发布的活动
        wrapper.eq(Activity::getStatus, ActivityStatus.PUBLISHED);

        // 社团ID筛选
        if (clubId != null) {
            wrapper.eq(Activity::getClubId, clubId);
        }

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Activity::getTitle, keyword)
                    .or()
                    .like(Activity::getContent, keyword));
        }

        wrapper.orderByDesc(Activity::getStartTime);

        Page<Activity> page = new Page<>(pageNum, pageSize);
        page = this.page(page, wrapper);

        // 转换为VO
        Page<ActivityVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ActivityVO> voList = page.getRecords().stream()
                .map(this::convertToActivityVOWithUserStatus)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查看活动详情
     */
    @Override
    public ActivityDetailVO getActivityDetail(Long activityId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        ActivityDetailVO vo = new ActivityDetailVO();
        BeanUtils.copyProperties(activity, vo);

        // 查询社团信息
        Club club = clubMapper.selectById(activity.getClubId());
        if (club != null) {
            vo.setClubName(club.getName());
        }

        // 查询创建者信息
        User creator = userMapper.selectById(activity.getCreateUser());
        if (creator != null) {
            vo.setCreateUsername(creator.getUsername());
        }

        // 如果用户已登录，查询用户报名状态
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getUserId, userId);
            ActivitySignup signup = activitySignupMapper.selectOne(wrapper);

            vo.setIsSignedUp(signup != null && signup.getStatus() != SignupStatus.CANCELLED);
            if (signup != null) {
                vo.setSignupStatus(signup.getStatus().getCode());
            }

            // 判断是否可以报名
            boolean canSignup = checkCanSignup(activity, userId);
            vo.setCanSignup(canSignup);
            if (!canSignup) {
                vo.setSignupDisabledReason(getSignupDisabledReason(activity, userId));
            }
        } catch (Exception e) {
            // 未登录
            vo.setIsSignedUp(false);
            vo.setCanSignup(false);
            vo.setSignupDisabledReason("请先登录");
        }

        return vo;
    }

    /**
     * 报名活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signupActivity(Long activityId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 验证活动状态
        if (activity.getStatus() != ActivityStatus.PUBLISHED) {
            throw new BusinessException(20105, "该活动不可报名");
        }

        // 验证报名时间
        LocalDateTime now = LocalDateTime.now();
        if (activity.getSignupStartTime() != null && now.isBefore(activity.getSignupStartTime())) {
            throw new BusinessException(20105, "报名尚未开始");
        }
        if (activity.getSignupEndTime() != null && now.isAfter(activity.getSignupEndTime())) {
            throw new BusinessException(20105, "报名已截止");
        }

        // 检查是否已报名
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId);
        ActivitySignup existingSignup = activitySignupMapper.selectOne(wrapper);

        if (existingSignup != null) {
            if (existingSignup.getStatus() == SignupStatus.REGISTERED) {
                throw new BusinessException(20106, "您已报名该活动");
            } else if (existingSignup.getStatus() == SignupStatus.CANCELLED) {
                // 重新报名
                existingSignup.setStatus(SignupStatus.REGISTERED);
                existingSignup.setSignupTime(LocalDateTime.now());
                activitySignupMapper.updateById(existingSignup);

                // 更新活动报名人数
                activity.setCurrentMembers(activity.getCurrentMembers() + 1);
                this.updateById(activity);
                return;
            }
        }

        // 检查人数限制
        if (activity.getMaxMembers() != null && activity.getCurrentMembers() >= activity.getMaxMembers()) {
            throw new BusinessException(20105, "活动报名人数已满");
        }

        // 创建报名记录
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setStatus(SignupStatus.REGISTERED);
        signup.setSignupTime(LocalDateTime.now());
        activitySignupMapper.insert(signup);

        // 更新活动报名人数
        activity.setCurrentMembers(activity.getCurrentMembers() + 1);
        this.updateById(activity);
    }

    /**
     * 取消报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSignup(Long activityId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(20101, "活动不存在");
        }

        // 查询报名记录
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId);
        ActivitySignup signup = activitySignupMapper.selectOne(wrapper);

        if (signup == null || signup.getStatus() != SignupStatus.REGISTERED) {
            throw new BusinessException(20107, "您未报名该活动");
        }

        // 检查是否可以取消报名（活动开始前才能取消）
        if (LocalDateTime.now().isAfter(activity.getStartTime())) {
            throw new BusinessException(20108, "活动已开始，无法取消报名");
        }

        // 更新报名状态
        signup.setStatus(SignupStatus.CANCELLED);
        activitySignupMapper.updateById(signup);

        // 更新活动报名人数
        activity.setCurrentMembers(Math.max(0, activity.getCurrentMembers() - 1));
        this.updateById(activity);
    }

    /**
     * 查看我的报名记录
     */
    @Override
    public List<ActivitySignupVO> getMySignups() {
        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getUserId, userId)
                .orderByDesc(ActivitySignup::getSignupTime);

        List<ActivitySignup> signups = activitySignupMapper.selectList(wrapper);
        return signups.stream()
                .map(this::convertToActivitySignupVO)
                .collect(Collectors.toList());
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证用户是否为社团负责人
     */
    private void verifyClubLeader(Long clubId, Long userId) {
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getRole, MemberRole.LEADER);
        ClubMember member = clubMemberMapper.selectOne(wrapper);

        if (member == null) {
            throw new BusinessException(20109, "您不是该社团的负责人，无法进行此操作");
        }
    }

    /**
     * 检查用户是否可以报名
     */
    private boolean checkCanSignup(Activity activity, Long userId) {
        // 活动状态检查
        if (activity.getStatus() != ActivityStatus.PUBLISHED) {
            return false;
        }

        // 报名时间检查
        LocalDateTime now = LocalDateTime.now();
        if (activity.getSignupStartTime() != null && now.isBefore(activity.getSignupStartTime())) {
            return false;
        }
        if (activity.getSignupEndTime() != null && now.isAfter(activity.getSignupEndTime())) {
            return false;
        }

        // 人数限制检查
        if (activity.getMaxMembers() != null && activity.getCurrentMembers() >= activity.getMaxMembers()) {
            return false;
        }

        // 已报名检查
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activity.getId())
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, SignupStatus.REGISTERED);
        return activitySignupMapper.selectCount(wrapper) == 0;
    }

    /**
     * 获取不能报名的原因
     */
    private String getSignupDisabledReason(Activity activity, Long userId) {
        if (activity.getStatus() != ActivityStatus.PUBLISHED) {
            return "活动未发布";
        }

        LocalDateTime now = LocalDateTime.now();
        if (activity.getSignupStartTime() != null && now.isBefore(activity.getSignupStartTime())) {
            return "报名尚未开始";
        }
        if (activity.getSignupEndTime() != null && now.isAfter(activity.getSignupEndTime())) {
            return "报名已截止";
        }

        if (activity.getMaxMembers() != null && activity.getCurrentMembers() >= activity.getMaxMembers()) {
            return "报名人数已满";
        }

        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activity.getId())
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, SignupStatus.REGISTERED);
        if (activitySignupMapper.selectCount(wrapper) > 0) {
            return "您已报名该活动";
        }

        return "";
    }

    /**
     * 转换为ActivityVO（不包含用户状态）
     */
    private ActivityVO convertToActivityVO(Activity activity) {
        ActivityVO vo = new ActivityVO();
        BeanUtils.copyProperties(activity, vo);

        // 查询社团信息
        Club club = clubMapper.selectById(activity.getClubId());
        if (club != null) {
            vo.setClubName(club.getName());
        }

        // 查询创建者信息
        User creator = userMapper.selectById(activity.getCreateUser());
        if (creator != null) {
            vo.setCreateUsername(creator.getUsername());
        }

        vo.setIsSignedUp(false);
        return vo;
    }

    /**
     * 转换为ActivityVO（包含用户报名状态）
     */
    private ActivityVO convertToActivityVOWithUserStatus(Activity activity) {
        ActivityVO vo = convertToActivityVO(activity);

        try {
            Long userId = StpUtil.getLoginIdAsLong();
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, activity.getId())
                    .eq(ActivitySignup::getUserId, userId);
            ActivitySignup signup = activitySignupMapper.selectOne(wrapper);

            vo.setIsSignedUp(signup != null && signup.getStatus() != SignupStatus.CANCELLED);
            if (signup != null) {
                vo.setSignupStatus(signup.getStatus().getCode());
            }
        } catch (Exception e) {
            // 未登录
            vo.setIsSignedUp(false);
        }

        return vo;
    }

    /**
     * 转换为ActivitySignupVO
     */
    private ActivitySignupVO convertToActivitySignupVO(ActivitySignup signup) {
        ActivitySignupVO vo = new ActivitySignupVO();
        BeanUtils.copyProperties(signup, vo);

        // 查询活动信息
        Activity activity = this.getById(signup.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }

        // 查询用户信息
        User user = userMapper.selectById(signup.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setStudentId(user.getStudentId());
        }

        return vo;
    }
}
