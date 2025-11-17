package com.club.campusclubmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.campusclubmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
