import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabReservation;
import fabriques.FabSalle;
import fabriques.FabTranche;
import fabriques.FabTypeSalle;


public class TestJohnReserveAutomatiquementUneSalle {
	
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
	}
	
	@Test
	public void testJohnReserveUneSalleSansForfaitPourUneHeure() throws SQLException, Exception{
		int duree = 2;
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
		Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(2);//Grande salle
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
		
		this.laReservation = this.servReservation.reserverSalleAutomatiquement(leClient,
				dateReservation,duree,leTypeSalle,laTranche,false);
		
		if(this.laReservation != null){
			Reservation uneReservation = FabReservation.getInstance().rechercherReservation(this.laReservation.getIdentifiant());
			assertNotNull(this.laReservation.getIdentifiant());
			assertEquals(this.laReservation.getIdentifiant(),uneReservation.getIdentifiant());
		}else{
			fail("La salle n'a pas été reservée");
		}
	}
	
	@Test
	public void testJohnReserveUneSalleMaisAucunCreneauNestDispo() throws SQLException, Exception{
		//On va remplir toutes les salles d'un TypeSalle donnée pour un jour donné: plus aucune creneau ne sera dispo.
		//On va ensuite essayer de faire une reservation automatique pour ce jour et ce typeSalle, et on 
		//va vérifier que la derniere Reservation n'a pas eu lieu (comme il n'y a plus de creneaux dispo) 
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(1);//petite salle
		List<Salle> lesSalles = FabSalle.getInstance().listerSalleParType(leTypeSalle);
		for(int i=0;i<lesSalles.size();++i){
			GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
			java.sql.Date dateReservation = new Date(leCalendrier.getTimeInMillis());
			int duree = 15;
			TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
			
			this.servReservation.reserverSalleAutomatiquement(leClient,dateReservation,duree,leTypeSalle,laTranche,false);
		}
		
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,1,1);
		java.sql.Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		int duree = 1;
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
		Client leClient = FabClient.getInstance().rechercherClient(1);
		this.laReservation = this.servReservation.reserverSalleAutomatiquement(leClient,
				dateReservation,duree,leTypeSalle,laTranche,false);
		assertNull("Cette reservation ne doit pas pouvoir etre faites",this.laReservation);
	}
}
