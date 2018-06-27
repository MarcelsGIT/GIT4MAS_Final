package de.htw.berin.camunda.gruppe08.main.taskoperations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.htw.berin.camunda.gruppe08.dao.Connector;
import de.htw.berin.camunda.gruppe08.domain.Sitter;
import de.htw.berin.camunda.gruppe08.main.taskoperations.service.Matching;
/**
 * @project:de.htw.berin.camunda.gruppe08
 * @author: Marcel
 * @created: 17.06.2018
 * @changed: 17.06.2018
 * @changeBy:
 * @description: Aufforderung zur Bewerbung wird an entsprechende Sitter gesendet. Sitterprozess "Bewerben" wird gestartet.
 * @comments:
 */
public class SendAufforderungZurBewerbung implements JavaDelegate {
	
	private static final Logger L = LoggerFactory.getLogger(SendAufforderungZurBewerbung.class);
	
	@Override
	public void execute(DelegateExecution de) throws Exception {
		// TODO Auto-generated method stub
		try {

			HashMap<String, Sitter> sitters = (HashMap<String, Sitter>) de.getVariable("MatchedSitters");
			this.messageMatchedSitters(de, sitters);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void messageMatchedSitters(DelegateExecution de, HashMap<String, Sitter> sitters) {
		for(Sitter sitter : sitters.values()) {
			this.assignTask(de, sitter);
		}
	}
	
	private void assignTask(DelegateExecution de, Sitter sitter) {
		ProcessInstance pi = this.createProcessInstance(de);
		TaskQuery query = de.getProcessEngineServices().getTaskService().createTaskQuery().processInstanceId(pi.getProcessInstanceId()).taskName("Bewerben");
		Task task = query.singleResult();
		de.getProcessEngineServices().getTaskService().claim(task.getId(), sitter.getId());
	}
	
	private ProcessInstance createProcessInstance(DelegateExecution de) {
		ProcessInstance pi = de.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage("Start_Sitter", de.getVariables());
		de.setVariable("Sitter_InstanceID", pi.getProcessInstanceId());
		return pi;
	}

}
