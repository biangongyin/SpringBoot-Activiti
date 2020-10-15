package com.cbs.mapper;

import com.cbs.po.User;

public interface LoginMapper {
	User getpwdbyname(String name);
}
