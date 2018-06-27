/**
 * 
 */
package de.htw.berin.camunda.gruppe08.main.taskoperations.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.camunda.bpm.engine.identity.User;

import de.htw.berin.camunda.gruppe08.domain.Kunde;
import de.htw.berin.camunda.gruppe08.domain.Sitter;

/**
 * @project:de.htw.berin.camunda.gruppe08
 * @author: Marcel
 * @created: 17.06.2018
 * @changed: 17.06.2018
 * @changeBy:
 * @description: Hier findet der Matchingprozess statt
 * @comments:
 */
public class Matching {

	private static Matching m;
	
	
	private Matching() {}
	
	public static Matching getInstance() {
		if(m == null) {
			m = new Matching();
		}return m;
	}
	
	public HashMap<String, Sitter> match(HashMap<String, Sitter> sitters, Kunde customer)throws NullPointerException{

		HashMap<String, Sitter>  matchedSitters = this.compareAndStore(sitters, customer);
		return matchedSitters;
	}
	
	private HashMap<String, Sitter> compareAndStore(HashMap<String, Sitter> sitters, Kunde customer)throws NullPointerException{
		HashMap<String, Sitter> matchedSitters = null;
		Integer comparisonResult = 0;
		for(Sitter sitter : sitters.values()) {
			comparisonResult += sitter.getCity().compareToIgnoreCase(customer.getCity());
			comparisonResult += sitter.getKeeping().compareToIgnoreCase(customer.getAnfrage().getKeeping());
			comparisonResult += sitter.getMorePets().compareTo(customer.getAnfrage().getMorePets());
			comparisonResult += sitter.getPetfood().compareToIgnoreCase(customer.getAnfrage().getPetfood());
			comparisonResult += this.comparePetValue(sitter.getPet(), customer.getAnfrage().getPet());
			if(comparisonResult == 0) {
				if(matchedSitters == null) {
					matchedSitters = new HashMap<String, Sitter>();
				}
				matchedSitters.put(sitter.getId(), sitter);
			}
		}
		return matchedSitters;
	}
	
	private Integer comparePetValue(String sitterPet, String customerPet)throws NullPointerException {
		Integer comparisonResult = 1; 
		String[] sitterPets = sitterPet.split(",");
		for(String pet : sitterPets) {
			if(pet.compareToIgnoreCase(customerPet) == 0) {
				comparisonResult = 0;
				break;
			}
		}
		return comparisonResult;
		
	}

}
