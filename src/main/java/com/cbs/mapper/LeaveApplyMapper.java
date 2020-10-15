package com.cbs.mapper;

import com.cbs.po.LeaveApply;

public interface LeaveApplyMapper {
	void save(LeaveApply apply);

	LeaveApply getLeaveApply(int id);

	int updateByPrimaryKey(LeaveApply record);
}
