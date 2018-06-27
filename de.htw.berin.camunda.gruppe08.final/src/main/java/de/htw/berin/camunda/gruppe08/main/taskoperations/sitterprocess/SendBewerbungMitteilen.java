package de.htw.berin.camunda.gruppe08.main.taskoperations.sitterprocess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;

import de.htw.berin.camunda.gruppe08.domain.Sitter;
import java.util.HashMap;


public class SendBewerbungMitteilen implements JavaDelegate {

	@Override
	public void execute(DelegateExecution de) throws Exception {
		// TODO Auto-generated method stub
		
		String userid = de.getProcessEngineServices().getTaskService().createTaskQuery()
				.processInstanceId(de.getProcessInstanceId()).taskId("Task_05es86r")
				.singleResult().getAssignee();
		
		HashMap<String, Sitter> sitters = (HashMap<String, Sitter>)de.getVariable("MatchedSitters");
		Sitter sitter = sitters.get(userid);
		
		sitter.setPricePerDay((Long)de.getVariable("preisProTag"));
		
		de.getProcessEngineServices().getRuntimeService().createMessageCorrelation("Bewerbung_Entgegennehmen2").setVariable("Bewerbung", sitter).processInstanceId((String)de.getVariable("Main_InstanceID")).correlate();
				
	}

}
