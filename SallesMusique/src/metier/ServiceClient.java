package metier;

import java.sql.SQLException;

import donnees.Client;
import donnees.Forfait;
import donnees.Reservation;
import fabriques.FabClient;

public class ServiceClient {

	private FabClient fabClient;

	public ServiceClient(){
		this.fabClient = FabClient.getInstance();
	}
	
	public Client creerUnClient(String nom, String prenom, String numTel){
		Client leNouveauClient = null;
		//ici faire les verif savoir si un tel client existe deja 
		try {
			leNouveauClient = this.fabClient.creerClient(nom, prenom, numTel);
		} catch (SQLException e) {
			System.out.println("Erreur création client");
			e.printStackTrace();
		}
		return leNouveauClient;
	}
	
	public void confirmerUneServation(Reservation laReservation){
		
	}
	
	public void vendreUnForfait(Forfait leForfait){
		
	}
	
}
