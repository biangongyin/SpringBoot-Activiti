package com.cbs.mapper;

import java.util.List;

import com.cbs.po.UserRole;


public interface UserRoleMapper {
    int deleteByPrimaryKey(Integer urid);

    int insert(UserRole record);

    UserRole selectByPrimaryKey(Integer urid);

    int updateByPrimaryKeySelective(UserRole record);

    List<UserRole> listUserRoleByUid(int uid);
}