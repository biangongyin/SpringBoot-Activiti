package com.cbs.mapper;

import com.cbs.po.Approve;

/**
 * 流程意见接口
 * @author biangy
 *
 */
public interface ApproveMapper {
	/**
	 * 保存审批意见
	 * @param approve
	 */
	public void save(Approve approve);
	
	/**
	 * 根据流程节点，查询审批意见
	 * @param act_id
	 * @return
	 */
	public Approve getApproveByActId(String act_id);
	
	/**
	 * 根据id查询审批意见
	 * @param act_id
	 * @return
	 */
	public Approve getApproveById(String id);
}
