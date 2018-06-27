package de.htw.berin.camunda.gruppe08.main.taskoperations;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.htw.berin.camunda.gruppe08.dao.Connector;
import de.htw.berin.camunda.gruppe08.domain.Kunde;

public class ServiceAnfrageLöschen implements JavaDelegate {

	@Override
	public void execute(DelegateExecution de) throws Exception {
		// TODO Auto-generated method stub
		Kunde customer = (Kunde) de.getVariable("Anfragender");
		Connector.getInstance().deleteRequest(customer.getId());
		de.getProcessEngineServices().getRuntimeService().createSignalEvent("Signal_3milh5b").send();
	}

}
