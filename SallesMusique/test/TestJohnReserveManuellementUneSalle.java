import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Date;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import metier.ServiceReservation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import fabriques.FabClient;
import fabriques.FabReservation;
import fabriques.FabSalle;


public class TestJohnReserveManuellementUneSalle {
	
	private Client leClient;
	private Reservation laReservation;
	private ServiceReservation servReservation;

	@Before
	public void setUp() throws SQLException{
		this.leClient = FabClient.getInstance().rechercherClient(1);
		this.servReservation = ServiceReservation.getInstance();
		this.laReservation = null;
		
		List<Reservation> lesReservation = FabReservation.getInstance().listerReservation();
		System.out.println("Before: ");
		for(Reservation r : lesReservation){
			System.out.println(r.getIdentifiant()+"(id) "+r.getSalle().getIdentifiant()+"(idSalle) "+r.getHeureDebut()+"(heureDeb) "+r.getDuree()+"(duree) "+r.getDateReservation()+"(dateR) "+r.getDateCommande()+"(dateCom) "+r.getDateConfirmation()+"(dateConf) "+r.getClient().getIdentifiant()+"(idClient)");
		}
	}
	
	@After
	public void listerReservation() throws SQLException, Exception{
		List<Reservation> lesReservation = FabReservation.getInstance().listerReservation();
		System.out.println("After : ");
		for(Reservation r : lesReservation){
			System.out.println(r.getIdentifiant()+"(id) "+r.getSalle().getIdentifiant()+"(idSalle) "+r.getHeureDebut()+"(heureDeb) "+r.getDuree()+"(duree) "+r.getDateReservation()+"(dateR) "+r.getDateCommande()+"(dateCom) "+r.getDateConfirmation()+"(dateConf) "+r.getClient().getIdentifiant()+"(idClient)");
		}
	}

	@Test
	public void testJohnReserveUneSalleManuellement() throws SQLException, Exception{
		Salle laSalle = FabSalle.getInstance().rechercherSalle(1);
		GregorianCalendar gc = new GregorianCalendar(2013,2,2);
		java.sql.Date laDate = new Date(gc.getTimeInMillis());
		int duree = 2;
		int heureDebut = 9;
		
		//On supprime toutes les reservations pour ce jour/cette salle, pour être sur qu'on pourra reserver
		List<Reservation> lesReservationsExistantes = FabReservation.getInstance().rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		for(Reservation r : lesReservationsExistantes){
			this.servReservation.annulerReservation(r);
		}
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);
		assertNotNull("Cette reservation ne doit pas être null",this.laReservation);
	}
	
	@Test
	public void testJohnReserveUneSalleManuellementSurUnCreneauConfirme() throws SQLException, Exception{
		//On va creer un creneau (et le confirmer) à une heure donnée pour une salle donnée, puis on va essayer de 
		//faire une reservation manuelle pour cette meme daet, meme heure, meme salle. Cette reservation manuelle doit échouer
		
		//1 : on créé une reservation : 
		Salle laSalle = FabSalle.getInstance().rechercherSalle(5);
		GregorianCalendar gc = new GregorianCalendar(2014,2,2);
		java.sql.Date laDate = new Date(gc.getTimeInMillis());
		int duree = 2;
		int heureDebut = 10;
		//On supprime toutes les reservations pour ce jour/cette salle, pour être sur qu'on pourra reserver
		List<Reservation> lesReservationsExistantes = FabReservation.getInstance().rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		for(Reservation r : lesReservationsExistantes){
			this.servReservation.annulerReservation(r);
		}
		Reservation uneReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//On a créé une reservation de 10h à 12h
		this.servReservation.confirmerUneServation(leClient,uneReservation);
		
		//2 : on reserve manuellement un creneau à cette meme heure :
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 10 à 12
		assertNull("Cette reservation ne doit pas pouvoir avoir lieu",this.laReservation);
		heureDebut -= 1;
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 9 à 11
		assertNull("Cette reservation ne doit pas pouvoir avoir lieu",this.laReservation);
		heureDebut += 2;
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 11 à 13
		assertNull("Cette reservation ne doit pas pouvoir avoir lieu",this.laReservation);
	}
	
	@Test
	public void testJohnReserveUneSalleManuellementSurUnCreneauNonConfirme() throws SQLException, Exception{
		//On va creer un creneau (et le confirmer) à une heure donnée pour une salle donnée, puis on va essayer de 
		//faire une reservation manuelle pour cette meme daet, meme heure, meme salle. Cette reservation manuelle doit échouer
		
		//1 : on créé une reservation : 
		Salle laSalle = FabSalle.getInstance().rechercherSalle(5);
		GregorianCalendar gc = new GregorianCalendar(2014,2,2);
		java.sql.Date laDate = new Date(gc.getTimeInMillis());
		int duree = 2;
		int heureDebut = 10;
		//On supprime toutes les reservations pour ce jour/cette salle, pour être sur qu'on pourra reserver
		List<Reservation> lesReservationsExistantes = FabReservation.getInstance().rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		for(Reservation r : lesReservationsExistantes){
			this.servReservation.annulerReservation(r);
		}
		Reservation uneReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//On a créé une reservation de 10h à 12h
		System.out.println(this.servReservation.reservationEstConfirmee(uneReservation));
		System.out.println(uneReservation.getIdentifiant());
		
		//2 : on reserve manuellement un creneau à cette meme heure :
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 10 à 12
		assertNotNull("Cette reservation doit pas pouvoir avoir lieu",this.laReservation);
		//3 : on reserve manuellement un creneau qui chevauche un creneau existant, sans forcement qu'il commence à la meme heure :
		heureDebut -= 1;
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 9 à 11
		assertNotNull("Cette reservation doit pas pouvoir avoir lieu",this.laReservation);
		heureDebut += 2;
		this.laReservation = this.servReservation.reserverSalleManuellement(laSalle, leClient, laDate, heureDebut, duree);//De 11 à 13
		assertNotNull("Cette reservation doit pas pouvoir avoir lieu",this.laReservation);
	}

}
