package de.htw.berin.camunda.gruppe08.main;

import java.util.HashMap;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.engine.task.TaskQuery;

import de.htw.berin.camunda.gruppe08.dao.data.CsvReader;
import de.htw.berin.camunda.gruppe08.domain.Kunde;
import de.htw.berin.camunda.gruppe08.domain.Sitter;

public class DataGenerator {
	
	private static DataGenerator dg;
	private final String email = "camundaGruppe08@gmail.com";
	private DataGenerator() {
		
	}
	
	public static DataGenerator getInstance() {
		if(dg == null) {
			dg = new DataGenerator();
		}
		return dg;
	}
	
	public void createData(ProcessEngine engine) {
		IdentityService is = engine.getIdentityService();
		AuthorizationService as = engine.getAuthorizationService();
		FilterService fs = engine.getFilterService();
		TaskService ts = engine.getTaskService();
		
		User admin = this.createAdmin(is);
		User employee = this.createEmployee(is);
		HashMap<String, Sitter> sitters = null;
		try{sitters = CsvReader.getInstance().readSitterFile();}catch(Exception e) {e.printStackTrace();}
		Kunde customer = this.createKunde(is);
		
		Group ag = this.createAdminGroup(is, as);
		Group eg = this.createEmployeeGroup(is);
		Group sg = this.createSitterGroup(is);
		Group cg = this.createCustomerGroup(is);
		
		is.createMembership(admin.getId(), ag.getId());
		is.createMembership(admin.getId(), eg.getId());
		is.createMembership(admin.getId(), sg.getId());
		is.createMembership(admin.getId(), cg.getId());
		
		is.createMembership(employee.getId(), eg.getId());
		
		this.createMembershipForSitters(sitters, sg, is);
		
		is.createMembership(customer.getId(), cg.getId());
		
		this.authEmployee(as);
		this.authSitter(as);
		this.authCustomer(as);
		
		this.filterVisibility(fs, ts);
		
	}
	
	private User createAdmin(IdentityService is) {
		User admin = is.newUser("admin");
		admin.setFirstName("Kaan");
		admin.setLastName("Oezgiray");
		admin.setPassword("adminpw");
		admin.setEmail(this.email);
		is.saveUser(admin);
		
		return admin;
	}
	
	private Kunde createKunde(IdentityService is) {
		  Kunde rqu = (Kunde)is.newUser("chunsa");
		  rqu.setFirstName("Huyen");
		  rqu.setLastName("Dao");
		  rqu.setPassword("customerpw");
		  rqu.setEmail(this.email);
		  is.saveUser(rqu);
		  
		  return rqu;
	}
	
	private User createEmployee(IdentityService is) {
		User mpl = is.newUser("employee");
		mpl.setFirstName("Marcel");
		mpl.setLastName("Decker");
		mpl.setPassword("employeepw");
		mpl.setEmail(this.email);
		is.saveUser(mpl);
		
		return mpl;
	}
	
	private Group createAdminGroup(IdentityService is, AuthorizationService as) {
		Group ag = null;
		if(is.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
			ag = is.newGroup(Groups.CAMUNDA_ADMIN);
		}else {
			ag = is.newGroup("admins");
		}
			ag.setName("Administratoren");
			ag.setType("ALL_WORKFLOWS");
			is.saveGroup(ag);
		this.authAdmins(as);
		return ag;
	}
	
	private Group createEmployeeGroup(IdentityService is) {
		Group ag = is.newGroup("employees");
		ag.setName("Mitarbeiter");
		ag.setType("WORKFLOW");
		is.saveGroup(ag);
		
		return ag;
	}
	
	private Group createSitterGroup(IdentityService is) {
		Group ag = is.newGroup("sitters");
		ag.setName("Sitter");
		ag.setType("WORKFLOW");
		is.saveGroup(ag);
		
		return ag;
	}
	
	private Group createCustomerGroup(IdentityService is) {
		Group ag = is.newGroup("customers");
		ag.setName("Kunden");
		ag.setType("WORKFLOW");
		is.saveGroup(ag);
		
		return ag;
	}
	
	private void authAdmins(AuthorizationService as) {
		for (Resource resource : Resources.values()) {
	        if(as.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(Authorization.ANY).count() == 0) {
	          AuthorizationEntity userAdminAuth = new AuthorizationEntity(Authorization.AUTH_TYPE_GRANT);
	          userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
	          userAdminAuth.setResource(resource);
	          userAdminAuth.setResourceId(Authorization.ANY);
	          userAdminAuth.addPermission(Permissions.ALL);
	          as.saveAuthorization(userAdminAuth);
	        }
	}
	}
	
	private void createMembershipForSitters(HashMap<String, Sitter> sitters, Group sg, IdentityService is) {
		for(Sitter sitter : sitters.values()) {
			is.createMembership(sitter.getId(), sg.getId());
		}
	}
	
	
	private void authCustomer(AuthorizationService as) {
		Authorization auth = as.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		Authorization auth2 = as.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		auth.setGroupId("customers");
		auth2.setGroupId("customers");
		auth.addPermission(Permissions.ACCESS);
		auth2.addPermission(Permissions.ACCESS);
		auth.setResourceId("tasklist");
		auth2.setResourceId("tasklist");
		auth.setResource(Resources.APPLICATION);
		auth2.setResource(Resources.APPLICATION);
		auth.setResourceId("customer");
		auth2.setResourceId("start");
		as.saveAuthorization(auth);
		as.saveAuthorization(auth2);
	}
	
	private void authEmployee(AuthorizationService as) {
		Authorization auth = as.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		auth.setGroupId("employees");
		auth.addPermission(Permissions.ACCESS);
		auth.setResourceId("tasklist");
		auth.setResource(Resources.APPLICATION);
		as.saveAuthorization(auth);
	}
	
	private void authSitter(AuthorizationService as) {
		Authorization auth = as.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		auth.setGroupId("sitters");
		auth.addPermission(Permissions.ACCESS);
		auth.setResourceId("tasklist");
		auth.setResource(Resources.APPLICATION);
		as.saveAuthorization(auth);
	}
	
	private void filterVisibility(FilterService fs, TaskService ts) {
		TaskQuery query = ts.createTaskQuery().taskAssigneeExpression("${currentUser()}");
		Filter filter = fs.newTaskFilter().setName("Übersicht").setQuery(query).setOwner("admin");
		fs.saveFilter(filter);
	}
	

}
