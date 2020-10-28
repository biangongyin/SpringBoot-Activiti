package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DeployProcess extends ActivitiTestCase{
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource 
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
 
	@Test
	public void testDeploy(){
		String filePath="C:/Users/biangy/git/SpringBoot-Activiti/target/classes/process/bimprocess.bpmn";
		try {
			InputStream is = Class.class.getClass().getResource(filePath).openStream();
			
			// 部署流程定义
			repositoryService.createDeployment().addInputStream("bimprocess.bpmn",is).deploy();
			
			// 启动流程实例
			String procId = runtimeService.startProcessInstanceByKey("bimprocess").getId();
			/*
			*//******************第一个任务开始******************************//*
			List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("sales").list();
			for (Task task : taskList) {
				 
	            System.out.println("Following task is available for sales group: " + task.getName());
	 
	            // 认领任务这里由foozie认领，因为fozzie是sales组的成员
	            taskService.claim(task.getId(), "gonzo");
	        }
 
			//查看fozzie是否能够获取该任务
			taskList = taskService.createTaskQuery().taskAssignee("gonzo").list();
			System.out.println("fozzie当前的任务数:" + taskList.size());
			for(Task task : taskList){
				System.out.println("the task name is :" + task.getName());
				 // 执行(完成)任务
				taskService.complete(task.getId());
			}
			System.out.println("gonzo当前的任务数:" +  taskService.createTaskQuery().taskAssignee("gonzo").count());
			
			*//******************第一个任务结束******************************//*
			
			
			*//******************第二个任务开始******************************//*
			taskList = taskService.createTaskQuery().taskCandidateGroup("management").list();
			for(Task task : taskList){
				 System.out.println("Following task is available for management group: " + task.getName());
				 taskService.claim(task.getId(), "kermit");
			}
			
			//查看kermit是否能够获取改任务
			taskList = taskService.createTaskQuery().taskAssignee("kermit").list();
			System.out.println("kermit当前任务数：" + taskList.size());
			for(Task task : taskList){
				System.out.println("the task name is :" + task.getName());
				taskService.complete(task.getId());
			}
			System.out.println("kermit当前任务数：" +  taskService.createTaskQuery().taskAssignee("kermit").count());
			
			*//******************第二个任务结束******************************//*
			
			
			*//******************第三个任务开始******************************//*
			//获取第三个任务
			taskList = taskService.createTaskQuery().taskCandidateGroup("management").list();
			for(Task task : taskList){
				 System.out.println("Following task is available for management group: " + task.getName());
				 taskService.claim(task.getId(), "gonzo");
			}
			//查看gonzo是否能够获取改任务
			taskList = taskService.createTaskQuery().taskAssignee("gonzo").list();
			System.out.println("gonzo当前任务数：" + taskList.size());
			for(Task task : taskList){
				System.out.println("the task name is :" + task.getName());
				taskService.complete(task.getId());
			}
			System.out.println("gonzo当前任务数：" +  taskService.createTaskQuery().taskAssignee("gonzo").count());
			
			*//******************第三个任务结束******************************//*
			
			 HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					 
                     .processInstanceId(procId).singleResult();
			 System.out.println("流程结束时间：" + historicProcessInstance.getEndTime());
 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
