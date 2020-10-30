package com.cbs.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cbs.mapper.ApproveMapper;
import com.cbs.po.Approve;
import com.cbs.service.ApproveService;

@Transactional(propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT,timeout=5000)
@Service
public class ApproveServiceImpl implements ApproveService{

	@Autowired
	ApproveMapper approveMapper;
	
	public void save(String taskId, String procInstId,String apr) {
		Approve approve = new Approve();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String dateTime = format.format(date);
		approve.setCreate_time(dateTime);
		approve.setTask_id(taskId);
		if ("true".equals(apr)) {
			apr = "同意";
		} else {
			apr = "拒绝";
		}
		approve.setApproved(apr);
		approve.setProc_inst_id(procInstId);
		approveMapper.save(approve);
	}

}
