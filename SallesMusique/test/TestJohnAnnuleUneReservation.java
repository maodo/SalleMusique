import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import metier.ServiceReservation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import connecteur.Connexion;

import donnees.Client;
import donnees.Reservation;
import donnees.Tarif;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabReservation;
import fabriques.FabTarifPonctuel;
import fabriques.FabTranche;
import fabriques.FabTypeSalle;


public class TestJohnAnnuleUneReservation {
	
	private Client leClient;
	private Reservation laReservation;
	private ServiceReservation servReservation;

	@Before
	public void setUp() throws SQLException{
		this.leClient = FabClient.getInstance().rechercherClient(1);
		this.servReservation = ServiceReservation.getInstance();
		this.laReservation = null;
	}
	
	@After
	public void listerReservation() throws SQLException, Exception{
		List<Reservation> lesReservation = FabReservation.getInstance().listerReservation();
		for(Reservation r : lesReservation){
			System.out.println(r.getIdentifiant()+"(id) "+r.getSalle().getIdentifiant()+"(idSalle) "+r.getHeureDebut()+"(heureDeb) "+r.getDuree()+"(duree) "+r.getDateReservation()+"(dateR) "+r.getDateCommande()+"(dateCom) "+r.getDateConfirmation()+"(dateConf) "+r.getClient().getIdentifiant()+"(idClient)");
		}
		Connexion.getInstance().close();
	}

	@Test
	public void testJohnAnnuleUnReservationExistante() throws SQLException{
		int duree = 2;
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
		Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(2);//Grande salle
		Tarif leTarif = FabTarifPonctuel.getInstance().rechercherTarifPonctuel(duree,leTypeSalle.getIdentifiant());
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(2);//L'apres midi
		
		//Pour le test : on fait une reservation, et on verifie qu'elle existe (via la fabrique).
		//Puis on appel annulerReservation(), et on vérifie que cetet meme reservation n'existe plus
		this.laReservation = this.servReservation.reserverSalleAutomatiquement(leClient,
				dateReservation,duree,leTarif,leTypeSalle,laTranche);
		assertNotNull("La reservation ne doit pas etre null",this.laReservation);
		this.servReservation.annulerReservation(this.laReservation);
		this.laReservation = this.servReservation.rechercherUneReservation(this.laReservation.getIdentifiant());
		assertNull("La reservation doit etre null",this.laReservation);
	}
}
