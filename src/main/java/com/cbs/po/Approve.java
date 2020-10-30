package com.cbs.po;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 流程审批意见表
 * @author biangy
 *
 */
public class Approve implements Serializable{
	@ApiModelProperty("主键")
	int id;
	@ApiModelProperty("流程实例id")
	String proc_inst_id;
	@ApiModelProperty("审批节点id")
	String act_id;
	@ApiModelProperty("审批节点名称")
	String act_name;
	@ApiModelProperty("任务id")
	String task_id;
	@ApiModelProperty("审批意见")
	String approved;
	@ApiModelProperty("创建时间")
	String create_time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProc_inst_id() {
		return proc_inst_id;
	}
	public void setProc_inst_id(String proc_inst_id) {
		this.proc_inst_id = proc_inst_id;
	}
	public String getAct_id() {
		return act_id;
	}
	public void setAct_id(String act_id) {
		this.act_id = act_id;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getAct_name() {
		return act_name;
	}
	public void setAct_name(String act_name) {
		this.act_name = act_name;
	}
	public String getApproved() {
		return approved;
	}
	public void setApproved(String approved) {
		this.approved = approved;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
}
