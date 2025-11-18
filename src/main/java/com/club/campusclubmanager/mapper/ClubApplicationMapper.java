package com.club.campusclubmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.campusclubmanager.entity.ClubApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社团申请Mapper
 */
@Mapper
public interface ClubApplicationMapper extends BaseMapper<ClubApplication> {
}
