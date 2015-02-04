import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import metier.ServiceClient;
import metier.ServiceReservation;

import org.junit.Before;
import org.junit.Test;

import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabSalle;
import fabriques.FabTranche;
import fabriques.FabTypeSalle;

public class TestJohnFaitUneReservationHebdomadaire {

	private ServiceReservation servReservation;
	
	@Before
	public void setUp() throws SQLException{
		servReservation = ServiceReservation.getInstance();
		servReservation.annulerToutesLesReservation();
	}
	
	@Test
	public void testJohnFaitUneReservationHebomadaire() throws SQLException {
		
		Client leClient = FabClient.getInstance().rechercherClient(1);
		Calendar cal = Calendar.getInstance();
		int leJour = Calendar.MONDAY;
		int nbSemaines = 3;
		
		cal.set(Calendar.YEAR,2013);
		cal.set(Calendar.DAY_OF_MONTH,28);
		cal.set(Calendar.MONTH,0);
		java.sql.Date dateDebut = new java.sql.Date(cal.getTime().getTime());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(2);
		int duree = 2;
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(2);//l'après midi
		List<Reservation> lesRes = this.servReservation.reservationHebdomadaire(leClient, leJour, nbSemaines, dateDebut, leTypeSalle, duree, laTranche);
		assertEquals(lesRes.size(),nbSemaines);
	}

	@Test
	public void testJohnFaitUneReservationHebomadaireQuiEmpieteSurUneReservationNonConfirmee() throws SQLException{
		//On créé des reservations qui bloquent toutes les salles du type salle voulu, mais on ne va pas les confirmer :
		//Ainsi une de ses reservations devrai etre annulée
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(2);
		int duree = 5;
		List<Salle> lesSalles = FabSalle.getInstance().listerSalleParType(leTypeSalle);
		for(Salle salleGenante : lesSalles){
			
			Client clientGenant = FabClient.getInstance().rechercherClient(2);
			Date dateGenante = new Date(new GregorianCalendar(2013,2,4).getTime().getTime());
			
			int heureDebut = 9;
			this.servReservation.reserverSalleManuellement(salleGenante, clientGenant, dateGenante, heureDebut, duree);
		}
		
		Client leClient = FabClient.getInstance().rechercherClient(1);
		Calendar cal = Calendar.getInstance();
		int leJour = Calendar.MONDAY;
		int nbSemaines = 3;
		cal.set(Calendar.DAY_OF_MONTH,28);
		cal.set(Calendar.MONTH,1);
		cal.set(Calendar.YEAR,2013);
		java.sql.Date dateDebut = new java.sql.Date(cal.getTime().getTime());
		
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);
		
		List<Reservation> lesRes = this.servReservation.reservationHebdomadaire(leClient, leJour, nbSemaines, dateDebut, leTypeSalle, duree, laTranche);
		this.servReservation.afficherReservation();
		assertEquals(lesRes.size(),nbSemaines);
	}
	
	public void testJohnFaitUneReservationHebomadaireQuiEmpieteSurUneReservationConfirmee() throws SQLException, Exception{
		//On créé des reservations qui bloquent toutes les salles du type salle voulu et on va confirmer ces reserv
		//A la fin, une des seances de la reservation hebdo n'aura pas pu etre faites
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(2);
		int duree = 5;
		List<Salle> lesSalles = FabSalle.getInstance().listerSalleParType(leTypeSalle);
		for(Salle salleGenante : lesSalles){
			
			Client clientGenant = FabClient.getInstance().rechercherClient(2);
			Date dateGenante = new Date(new GregorianCalendar(2013,2,4).getTime().getTime());
			
			int heureDebut = 9;
			Reservation laRes = this.servReservation.reserverSalleManuellement(salleGenante, clientGenant, dateGenante, heureDebut, duree);
			ServiceClient.getInstance().confirmerUneServation(clientGenant, laRes);
		}
		
		Client leClient = FabClient.getInstance().rechercherClient(1);
		Calendar cal = Calendar.getInstance();
		int leJour = Calendar.MONDAY;
		int nbSemaines = 3;
		cal.set(Calendar.DAY_OF_MONTH,28);
		cal.set(Calendar.MONTH,1);
		cal.set(Calendar.YEAR,2013);
		java.sql.Date dateDebut = new java.sql.Date(cal.getTime().getTime());
		
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);
		
		List<Reservation> lesRes = this.servReservation.reservationHebdomadaire(leClient, leJour, nbSemaines, dateDebut, leTypeSalle, duree, laTranche);
		this.servReservation.afficherReservation();
		assertEquals(lesRes.size(),nbSemaines-1);
	}
}
