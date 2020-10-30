package com.cbs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cbs.pagemodel.BimLeaveTask;
import com.cbs.pagemodel.DataGrid;
import com.cbs.pagemodel.HistoryProcess;
import com.cbs.pagemodel.MSG;
import com.cbs.pagemodel.RunningProcess;
import com.cbs.po.BimLeaveApply;
import com.cbs.po.Permission;
import com.cbs.po.Role;
import com.cbs.po.Role_permission;
import com.cbs.po.User;
import com.cbs.po.User_role;
import com.cbs.service.ApproveService;
import com.cbs.service.BimLeaveService;
import com.cbs.service.SystemService;

import io.swagger.annotations.Api;

@Api(value = "BIM实施细则流程接口")
@Controller
public class BimLeaveApplyController {
	@Autowired
	RepositoryService rep;
	@Autowired
	RuntimeService runservice;
	@Autowired
	FormService formservice;
	@Autowired
	IdentityService identityservice;
	@Autowired
	BimLeaveService bimLeaveservice;
	@Autowired
	TaskService taskservice;
	@Autowired
	HistoryService histiryservice;
	@Autowired
	SystemService systemservice;
	
	@Resource
	ApproveService approveService;

	/**
	 * BIM实施细则申请流程
	 * @return
	 */
	@RequestMapping(value = "/bimapply", method = RequestMethod.GET)
	public String bimapply() {
		return "bimprocess/bimapply";
	}
	
	/**
	 * BIM实施细则部门经理审批流程
	 * @return
	 */
	@RequestMapping(value = "/bimdeptleader", method = RequestMethod.GET)
	public String bimdeptleader() {
		return "bimprocess/bimdeptleader";
	}

	/**
	 * BIM实施细则人事审批流程
	 * @return
	 */
	@RequestMapping(value = "/bimhraudit", method = RequestMethod.GET)
	public String bimhraudit() {
		return "bimprocess/bimhraudit";
	}

	/**
	 * 查看bim实施流程
	 * @return
	 */
	@RequestMapping(value = "processList", method = RequestMethod.GET)
	String myleaves() {
		return "bimprocess/myleaves";
	}
	
	/**
	 * 查看bim历史流程
	 * @return
	 */
	@RequestMapping(value = "/bimhistoryprocess", method = RequestMethod.GET)
	public String history() {
		return "bimprocess/historyprocess";
	}
	@RequestMapping(value = "/startbimleave", method = RequestMethod.POST)
	@ResponseBody
	public MSG startBimLeave(BimLeaveApply apply, HttpSession session) {
		String userid = (String) session.getAttribute("username");
		if (userid == null) {
			return new MSG("请退出重新登陆！");
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("applyuserid", userid);
		ProcessInstance ins = bimLeaveservice.startWorkflow(apply, userid, variables);
		System.out.println("流程id" + ins.getId() + "已启动");
		return new MSG("sucess");
	}

	/**
	 * 查看发起的正在流转的流程
	 * @param session
	 * @param current
	 * @param rowCount
	 * @return
	 */
	@RequestMapping(value = "getbimprocessList", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<RunningProcess> getBimprocessList(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		System.out.print(userid);
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey("bimProcess").involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			BimLeaveApply l = bimLeaveservice.getleave(Integer.parseInt(p.getBusinessKey()));
			if (l.getUser_id().equals(userid))
				list.add(process);
			else
				continue;
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(list.size());
		grid.setRows(list);
		return grid;
	}
	
	/**
	 * 查看已经完成的流程
	 * @param session
	 * @param current
	 * @param rowCount
	 * @return
	 */
	@RequestMapping(value = "/getFinishBimProcess", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<HistoryProcess> getFinishProcess(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		String userid = (String) session.getAttribute("username");
		HistoricProcessInstanceQuery process = histiryservice.createHistoricProcessInstanceQuery()
				.processDefinitionKey("bimProcess").startedBy(userid).finished();
		int total = (int) process.count();
		int firstrow = (current - 1) * rowCount;
		List<HistoricProcessInstance> info = process.listPage(firstrow, rowCount);
		List<HistoryProcess> list = new ArrayList<HistoryProcess>();
		for (HistoricProcessInstance history : info) {
			HistoryProcess his = new HistoryProcess();
			String bussinesskey = history.getBusinessKey();
			BimLeaveApply apply = bimLeaveservice.getleave(Integer.parseInt(bussinesskey));
			his.setBimLeaveapply(apply);
			his.setBusinessKey(bussinesskey);
			his.setProcessDefinitionId(history.getProcessDefinitionId());
			list.add(his);
		}
		DataGrid<HistoryProcess> grid = new DataGrid<HistoryProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}
	
	/**
	 * 获取部门领导审批代办列表
	 * @param session
	 * @param current
	 * @param rowCount
	 * @return
	 */
	@RequestMapping(value = "/bimDeptTaskList", produces = {
			"application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<BimLeaveTask> getDeptTaskList(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		DataGrid<BimLeaveTask> grid = new DataGrid<BimLeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(0);
		grid.setRows(new ArrayList<BimLeaveTask>());
		// 先做权限检查，对于没有部门领导审批权限的用户,直接返回空
		String userid = (String) session.getAttribute("username");
		int uid = systemservice.getUidByusername(userid);
		User user = systemservice.getUserByid(uid);
		List<User_role> userroles = user.getUser_roles();
		if (userroles == null)
			return grid;
		boolean flag = false;// 默认没有权限
		for (int k = 0; k < userroles.size(); k++) {
			User_role ur = userroles.get(k);
			Role r = ur.getRole();
			int roleid = r.getRid();
			Role role = systemservice.getRolebyid(roleid);
			List<Role_permission> p = role.getRole_permission();
			for (int j = 0; j < p.size(); j++) {
				Role_permission rp = p.get(j);
				Permission permission = rp.getPermission();
				if (permission.getPermissionname().equals("部门领导审批"))
					flag = true;
				else
					continue;
			}
		}
		if (flag == false)// 无权限
		{
			return grid;
		} else {
			int firstrow = (current - 1) * rowCount;
			List<BimLeaveApply> results = bimLeaveservice.getpagedepttask(userid, firstrow, rowCount);
			int totalsize = bimLeaveservice.getalldepttask(userid);
			List<BimLeaveTask> tasks = new ArrayList<BimLeaveTask>();
			for (BimLeaveApply apply : results) {
				BimLeaveTask task = new BimLeaveTask();
				task.setApply_time(apply.getApply_time());
				task.setUser_id(apply.getUser_id());
				task.setEnd_time(apply.getEnd_time());
				task.setId(apply.getId());
				task.setLeave_type(apply.getLeave_type());
				task.setProcess_instance_id(apply.getProcess_instance_id());
				task.setProcessdefid(apply.getTask().getProcessDefinitionId());
				task.setReason(apply.getReason());
				task.setStart_time(apply.getStart_time());
				task.setTaskcreatetime(apply.getTask().getCreateTime());
				task.setTaskid(apply.getTask().getId());
				task.setTaskname(apply.getTask().getName());
				tasks.add(task);
			}
			grid.setRowCount(rowCount);
			grid.setCurrent(current);
			grid.setTotal(totalsize);
			grid.setRows(tasks);
			return grid;
		}
	}

	/**
	 * 获取人事审批流程
	 * @param session
	 * @param current
	 * @param rowCount
	 * @return
	 */
	@RequestMapping(value = "/bimhrtasklist", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<BimLeaveTask> getBimHrTaskList(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		DataGrid<BimLeaveTask> grid = new DataGrid<BimLeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(0);
		grid.setRows(new ArrayList<BimLeaveTask>());
		// 先做权限检查，对于没有人事权限的用户,直接返回空
		String userid = (String) session.getAttribute("username");
		int uid = systemservice.getUidByusername(userid);
		User user = systemservice.getUserByid(uid);
		List<User_role> userroles = user.getUser_roles();
		if (userroles == null)
			return grid;
		boolean flag = false;// 默认没有权限
		for (int k = 0; k < userroles.size(); k++) {
			User_role ur = userroles.get(k);
			Role r = ur.getRole();
			int roleid = r.getRid();
			Role role = systemservice.getRolebyid(roleid);
			List<Role_permission> p = role.getRole_permission();
			for (int j = 0; j < p.size(); j++) {
				Role_permission rp = p.get(j);
				Permission permission = rp.getPermission();
				if (permission.getPermissionname().equals("人事审批"))
					flag = true;
				else
					continue;
			}
		}
		if (flag == false)// 无权限
		{
			return grid;
		} else {
			int firstrow = (current - 1) * rowCount;
			List<BimLeaveApply> results = bimLeaveservice.getpagehrtask(userid, firstrow, rowCount);
			int totalsize = bimLeaveservice.getallhrtask(userid);
			List<BimLeaveTask> tasks = new ArrayList<BimLeaveTask>();
			for (BimLeaveApply apply : results) {
				BimLeaveTask task = new BimLeaveTask();
				task.setApply_time(apply.getApply_time());
				task.setUser_id(apply.getUser_id());
				task.setEnd_time(apply.getEnd_time());
				task.setId(apply.getId());
				task.setLeave_type(apply.getLeave_type());
				task.setProcess_instance_id(apply.getProcess_instance_id());
				task.setProcessdefid(apply.getTask().getProcessDefinitionId());
				task.setReason(apply.getReason());
				task.setStart_time(apply.getStart_time());
				task.setTaskcreatetime(apply.getTask().getCreateTime());
				task.setTaskid(apply.getTask().getId());
				task.setTaskname(apply.getTask().getName());
				tasks.add(task);
			}
			grid.setRowCount(rowCount);
			grid.setCurrent(current);
			grid.setTotal(totalsize);
			grid.setRows(tasks);
			return grid;
		}
	}
	
	/**
	 * 处理流程
	 * @param taskid
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/dealBimTask", method = RequestMethod.POST)
	@ResponseBody
	public BimLeaveApply dealBimTask(@RequestParam("taskid") String taskid, HttpServletResponse response) {
		Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
		ProcessInstance process = runservice.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
				.singleResult();
		BimLeaveApply leave = bimLeaveservice.getleave(new Integer(process.getBusinessKey()));
		return leave;
	}
	
	/**
	 * 部门审批
	 * @param session
	 * @param taskid
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/deptcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG deptcomplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("deptleaderapprove");
		variables.put("deptleaderapprove", approve);
		//根据任务id和用户领取任务
		taskservice.claim(taskid, userid);
		//根据任务id完成自己节点的任务
		taskservice.complete(taskid, variables);
		return new MSG("success");
	}
	
	/**
	 * 人事审批
	 * @param session
	 * @param taskid
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/hrcomplete/{taskid}/{procInstId}", method = RequestMethod.POST)
	@ResponseBody
	public MSG hrcomplete(HttpSession session, @PathVariable("taskid") String taskid,@PathVariable("procInstId") String procInstId, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("hrapprove");
		variables.put("hrapprove", approve);
		//根据任务id和用户领取任务
		taskservice.claim(taskid, userid);
		//根据任务id完成自己节点的任务
		taskservice.complete(taskid, variables);
		approveService.save(taskid,procInstId,approve);
		
		return new MSG("success");
	}
	
	@RequestMapping(value = "/bimProcessinfo", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricActivityInstance> processinfo(@RequestParam("instanceid") String instanceid) {
		List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery()
				.processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
		return his;
	}
}
