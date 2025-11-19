package com.club.campusclubmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.campusclubmanager.dto.CheckinRequest;
import com.club.campusclubmanager.dto.CreateActivityRequest;
import com.club.campusclubmanager.dto.ReviewActivityRequest;
import com.club.campusclubmanager.dto.UpdateActivityRequest;
import com.club.campusclubmanager.entity.Activity;
import com.club.campusclubmanager.vo.ActivityDetailVO;
import com.club.campusclubmanager.vo.ActivitySignupVO;
import com.club.campusclubmanager.vo.ActivityVO;

import java.util.List;

/**
 * 活动服务接口
 */
public interface ActivityService extends IService<Activity> {

    // ==================== 社团管理员操作 ====================

    /**
     * 创建活动（社团管理员）
     */
    void createActivity(CreateActivityRequest request);

    /**
     * 更新活动（社团管理员）
     */
    void updateActivity(Long activityId, UpdateActivityRequest request);

    /**
     * 取消活动（社团管理员）
     */
    void cancelActivity(Long activityId);

    /**
     * 查看活动报名列表（社团管理员）
     */
    Page<ActivitySignupVO> getActivitySignups(Long activityId, Integer pageNum, Integer pageSize);

    /**
     * 活动签到/标记缺席（社团管理员）
     */
    void checkinActivity(Long activityId, CheckinRequest request);

    // ==================== 系统管理员操作 ====================

    /**
     * 查看所有活动（系统管理员）
     */
    Page<ActivityVO> getAllActivities(Integer pageNum, Integer pageSize, String keyword, String status);

    /**
     * 审核活动（系统管理员）
     */
    void reviewActivity(Long activityId, ReviewActivityRequest request);

    /**
     * 删除活动（系统管理员）
     */
    void deleteActivity(Long activityId);

    // ==================== 学生端操作 ====================

    /**
     * 浏览活动列表
     */
    Page<ActivityVO> listActivities(Integer pageNum, Integer pageSize, String keyword, Long clubId);

    /**
     * 查看活动详情
     */
    ActivityDetailVO getActivityDetail(Long activityId);

    /**
     * 报名活动
     */
    void signupActivity(Long activityId);

    /**
     * 取消报名
     */
    void cancelSignup(Long activityId);

    /**
     * 查看我的报名记录
     */
    List<ActivitySignupVO> getMySignups();
}
