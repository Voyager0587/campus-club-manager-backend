package com.club.campusclubmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.campusclubmanager.entity.ClubMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社团成员Mapper
 */
@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {
}
