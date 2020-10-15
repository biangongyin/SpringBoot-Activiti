package com.cbs.service;

import java.util.Map;
import org.activiti.engine.runtime.ProcessInstance;

import com.cbs.po.PurchaseApply;

public interface PurchaseService {
	public ProcessInstance startWorkflow(PurchaseApply apply,String userid,Map<String,Object> variables);
	PurchaseApply getPurchase(int id);
	void updatePurchase(PurchaseApply a);
}
