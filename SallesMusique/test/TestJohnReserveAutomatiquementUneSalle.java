import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import metier.ServiceReservation;

import org.junit.Before;
import org.junit.Test;

import donnees.Client;
import donnees.Reservation;
import donnees.TarifForfaitaire;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabReservation;


public class TestJohnReserveAutomatiquementUneSalle {
	
	private Client leClient;
	private Reservation laReservation;

	@Before
	public void setUp() throws SQLException{
		this.leClient = FabClient.getInstance().rechercherClient(1);
	}
	
	@Test
	public void testJohnReserveUneSalle() {
		int duree = 4;
		Calendar dateReservation = new GregorianCalendar(); 
		Calendar dateCommande = new GregorianCalendar();
		Calendar dateConfirmation = new GregorianCalendar();
		TarifForfaitaire leTarif = FabTarif.getInstance().rechercherTarif(1);
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(1);
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);
		
		this.laReservation = FabReservation.getInstance().creerReservation(duree,dateReservation,dateCommande,dateConfirmation,leTarif,leTypeSalle,laTranche);
		ServiceReservation.getInstance().reserverSalle(this.leClient,this.laReservation);
		
		assertEquals(this.laReservation.getIdentifiant(), FabReservation.getInstance().rechercherReservation(leNouvelIdReservation));
	}

}
