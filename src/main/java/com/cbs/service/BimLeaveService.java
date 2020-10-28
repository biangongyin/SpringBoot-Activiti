package com.cbs.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;

import com.cbs.po.BimLeaveApply;


public interface BimLeaveService {
	public ProcessInstance startWorkflow(BimLeaveApply apply,String userid,Map<String,Object> variables);
	public List<BimLeaveApply> getpagedepttask(String userid,int firstrow,int rowcount);
	public int getalldepttask(String userid);
	public BimLeaveApply getleave(int id);
	public List<BimLeaveApply> getpagehrtask(String userid,int firstrow,int rowcount);
	public int getallhrtask(String userid);
	public void completereportback(String taskid, String realstart_time, String realend_time);
	public void updatecomplete(String taskid, BimLeaveApply leave,String reappply);
	public List<String> getHighLightedFlows(ProcessDefinitionEntity deployedProcessDefinition,List<HistoricActivityInstance> historicActivityInstances);
}
