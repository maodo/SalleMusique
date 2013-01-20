package metier;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import donnees.Tarif;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabReservation;
import fabriques.FabSalle;

public class ServiceReservation {
	
	private static FabReservation fabReservation;
	private static ServiceReservation servReservation;
	
	private ServiceReservation(){
		fabReservation = FabReservation.getInstance();
	}
	
	public static ServiceReservation getInstance(){
		if(servReservation == null)
			return new ServiceReservation();
		else
			return servReservation;
	}
	
	public Reservation rechercherUneReservation(int idReservation) throws SQLException{
		return fabReservation.rechercherReservation(idReservation);
	}
	
	public Reservation reserverSalleAutomatiquement(
			Client leClient, Date laDate, int duree, 
			Tarif leTarif, TypeSalle leTypeSalle, 
			TrancheHoraire laTranche) throws SQLException {//ne dois pas ecreaser les reservations non reservé
		Reservation laReservation = null;
		
		//1 on charge les reservation de ce jour là : 
		List<Reservation> lesReservations = fabReservation.rechercherReservationDunJour(laDate);
		List<Salle> sallesDispo = FabSalle.getInstance().listerSalle();
		
		int creneauConseq = 0;
		int heureDebut = 9;
		if(lesReservations.isEmpty()){//aucune reservation ce jour là
			int leMontant = 0;
			java.util.Date today = new java.util.Date();
			java.sql.Date dateReserv = laDate, dateConfirm = new Date(today.getTime()), dateCommande = new Date(today.getTime());
			Salle laSalle = sallesDispo.get(1);
			laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
		}else{//Il y a d'autres reservations ce jour: on va les parser
			//On va construire l'emploi du temps d'aujourd'hui: 
			boolean[] tableDuJour = new boolean[14];
			for(int i=0;i<tableDuJour.length;++i){
				tableDuJour[i] = true;
			}
			for(Reservation r : lesReservations){
				int debutR = r.getHeureDebut();
				int dureeR = r.getDuree();
				for(int j=debutR-9;j<(debutR+dureeR)-9;++j){
					tableDuJour[j] = false;
				}
			}
			//On parcours la table du jour pour chercher un creneau qui correspond aux critères : 
			for(int i=0;i<14;++i){
				if(tableDuJour[i] == true){//Si ce creneau est dispo : 
					++creneauConseq;
					if(creneauConseq == duree){//On a assez de creneau pour reserver
						int leMontant = 0;
						java.util.Date today = new java.util.Date();
						java.sql.Date dateReserv = laDate, dateConfirm = new Date(today.getTime()), dateCommande = new Date(today.getTime());
						Salle laSalle = sallesDispo.get(1);
						laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
					}
				}else{
					heureDebut = i+1;
				}
			}
		}
		return laReservation;
	}
	
	public void reserverSalleManuellement(Salle laSalle, Client leClient, Date laDate){
		
	}
	
	public void reservationHebdomadaire(Salle laSalle, Client leClient, Date leCreneau){
		
	}
	
	public void annulerReservation(Reservation laReservation){
		
	}
}
