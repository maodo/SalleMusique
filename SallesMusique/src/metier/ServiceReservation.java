package metier;

import java.util.Date;

import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import fabriques.FabReservation;

public class ServiceReservation {
	
	private static FabReservation fabReservation;
	private static ServiceReservation servReservation;
	
	private ServiceReservation(){
		fabReservation = FabReservation.getInstance();
	}
	
	public ServiceReservation getInstance(){
		if(servReservation == null)
			return new ServiceReservation();
		else
			return servReservation;
	}
	
	public Reservation rechercherUneReservation(int idReservation){
		return this.fabReservation.rechercherReservation(idReservation);
	}
	
	public void reserverSalleAutomatiquement(int idClient,int idReservation,Date laDate){
		//ne dois pas ecreaser les reservations non reservé
	}
	
	public void reserverSalleManuellement(Salle laSalle, Client leClient, Date laDate){
		
	}
	
	public void reservationHebdomadaire(Salle laSalle, Client leClient, Date leCreneau){
		
	}
	
	public void annulerReservation(Reservation laReservation){
		
	}
}
