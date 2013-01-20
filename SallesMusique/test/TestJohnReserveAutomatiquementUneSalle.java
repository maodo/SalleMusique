import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import metier.ServiceReservation;

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


public class TestJohnReserveAutomatiquementUneSalle {
	
	private Client leClient;
	private Reservation laReservation;
	private ServiceReservation servReservation;

	@Before
	public void setUp() throws SQLException{
		this.leClient = FabClient.getInstance().rechercherClient(3);
		this.servReservation = ServiceReservation.getInstance();
	}
	
	@Test
	public void testJohnReserveUneSalleSansForfaitPourUneHeure() throws SQLException, Exception{
		int duree = 2;
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
		Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(1);//petite salle
		Tarif leTarif = FabTarifPonctuel.getInstance().rechercherTarifPonctuel(duree,leTypeSalle.getIdentifiant());
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
		
		this.laReservation = this.servReservation.reserverSalleAutomatiquement(leClient,
				dateReservation,duree,leTarif,leTypeSalle,laTranche);
		
		if(this.laReservation != null){
			Reservation uneReservation = FabReservation.getInstance().rechercherReservation(this.laReservation.getIdentifiant());
			assertNotNull(this.laReservation.getIdentifiant());
			assertEquals(this.laReservation.getIdentifiant(),uneReservation.getIdentifiant());
			System.out.println(uneReservation.getClient().getNom());
		}else{
			fail("La salle n'a pas été reservée");
		}
		Connexion.getInstance().close();
	}
	
	public void testJohnReserveUneSalleAvecUnForfaitValide() {
		
	}
}
