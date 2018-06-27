package de.htw.berin.camunda.gruppe08.main.taskoperations.startprocess;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.htw.berin.camunda.gruppe08.dao.data.CsvReader;
import de.htw.berin.camunda.gruppe08.domain.Kunde;
import de.htw.berin.camunda.gruppe08.domain.Sitter;

public class SendAnfrageformularAusgefuellt implements JavaDelegate{

	@Override
	public void execute(DelegateExecution de) throws Exception {
		// TODO Auto-generated method stub

		User customer = de.getProcessEngineServices().getIdentityService()
				.createUserQuery().userId
				(
						de.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId()
				)
				.singleResult();
		//Kunde als Referenz zum User erstellen
		Kunde kunde = new Kunde();
		kunde.setId(customer.getId());
		kunde.setFirstName(customer.getFirstName());
		kunde.setLastName(customer.getLastName());
		kunde.setEmail(customer.getEmail());
		kunde.setStreet((String)de.getVariable("Strasse"));
		kunde.setPlz((String)de.getVariable("PLZ"));
		kunde.setCity((String)de.getVariable("Ort"));
		//Kundenanfrage erstellen
		kunde.createAnfrage((String)de.getVariable("Haustier"), (String)de.getVariable("Haltungsart"), 
							(String)de.getVariable("Futter"), (boolean)de.getVariable("andereTiere"), 
							this.castToLocalDate((Date)de.getVariable("startDatum")), 
							this.castToLocalDate((Date)de.getVariable("endDatum")));

		//Variablen in Prozessinstanz einfügen
		de.setVariable("Anfragender", kunde);
		de.setVariable("SitterMap", this.createSitter());
		de.setVariable("Start_InstanceID", de.getProcessInstanceId());
		//Prozessinstanz für Main_Prozess starten
		ProcessInstance main = de.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage("start_main2", de.getVariables());
		de.setVariable("Main_InstanceID", main.getProcessInstanceId());
	}
	
	private HashMap<String, Sitter> createSitter(){
		HashMap<String, Sitter> sitters = null;
		try {
		 sitters = CsvReader.getInstance().readSitterFile();
		}catch(Exception e) {
			
		}
		return sitters;
	}
	
	private LocalDate castToLocalDate(Date date) {
		LocalDate local = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return local;
	}

}
