package de.htw.berin.camunda.gruppe08.main.taskoperations;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.htw.berin.camunda.gruppe08.dao.Connector;
import de.htw.berin.camunda.gruppe08.domain.Kunde;
/**
 * @project:de.htw.berin.camunda.gruppe08
 * @author: Marcel
 * @created: 17.06.2018
 * @changed: 17.06.2018
 * @changeBy:
 * @description: Bewerbung wird an Kunde weitergeleitet. Kundenprozess "Bewerbung annehmen" startet
 * @comments:
 */
public class SendBewerbungWeiterleiten implements JavaDelegate {
	private static final Logger L = LoggerFactory.getLogger(SendBewerbungWeiterleiten.class);
	@Override
	public void execute(DelegateExecution de) throws Exception {
		// TODO Auto-generated method stub
		try {
			ProcessInstance instance = de.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage("start_customer", de.getVariables());
			Kunde kunde = (Kunde) de.getVariable("Anfragender");
			Task task = de.getProcessEngineServices().getTaskService().createTaskQuery().processInstanceId(instance.getProcessInstanceId()).taskId("Task_0eleuzi").singleResult();
			de.setVariable("Customer_InstanceID", instance.getProcessInstanceId());
			de.getProcessEngineServices().getTaskService().claim(task.getId(), kunde.getId());
					
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
