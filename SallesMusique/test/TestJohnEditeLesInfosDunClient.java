import java.sql.Date;
import java.util.GregorianCalendar;

import metier.ServiceClient;
import metier.ServiceReservation;

import org.junit.Before;
import org.junit.Test;

import donnees.Client;
import donnees.Forfait;
import donnees.Reservation;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabForfait;
import fabriques.FabForfaitClient;
import fabriques.FabSalle;
import fabriques.FabTranche;
import fabriques.FabTypeSalle;


public class TestJohnEditeLesInfosDunClient {

	private FabForfaitClient fabForfaitClient;
	private FabClient fabClient;
	private ServiceReservation servReservation;
	private ServiceClient servClient;
	
	@Before
	public void setUp() throws Exception{
		this.fabClient = FabClient.getInstance();
		this.fabForfaitClient = FabForfaitClient.getInstance();
		this.servReservation = ServiceReservation.getInstance();
		this.servClient = ServiceClient.getInstance();
	}

	void testJohnCreeUnClient(){
		
	}
	
	@Test
	public void testJhonConfirmeUneReservationEtUtiliseSesPointsEtSonForfait() throws Exception{
		int duree = 1;
		Client client = fabClient.rechercherClient(1);
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
		Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(1);//petite salle
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
		
		this.fabForfaitClient.supprimerForfaitClient(1, 1);//suppression de ses forfaits	
		Forfait f = FabForfait.getInstance().rechercherForfait(1);//achat d'un forfait de 12h pour petites salles
		this.fabForfaitClient.creerForfaitClient(1, 1, 1, dateReservation, 1, 50);//creation d'1 forfait petites salles a 50 euros, dans lequel on met 1h de credit

		//creation d'une reservation de 3 heures qui commence la matin pour une petite salle
		Reservation laReservation = this.servReservation.reserverSalleAutomatiquement(client, dateReservation, duree, 
				FabSalle.getInstance().rechercherSalle(1).getTypeSalle(), laTranche,false);
		System.out.println(laReservation.getHeureDebut());
		this.servClient.confirmerUneServation(client, laReservation);
	}
	
	public void testJohnVendUnForfait(){
		
	}

}
