package com.cbs.mapper;

import com.cbs.po.BimLeaveApply;

public interface BimLeaveApplyMapper {
	void save(BimLeaveApply apply);

	BimLeaveApply getBimLeaveApply(int id);

	int updateByPrimaryKey(BimLeaveApply record);
}
