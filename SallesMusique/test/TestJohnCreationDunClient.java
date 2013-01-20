import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;
import metier.ServiceClient;

import org.junit.Before;
import org.junit.Test;

import connecteur.Connexion;
import donnees.Client;
import donnees.Forfait;
import donnees.ForfaitClient;
import fabriques.FabClient;
import fabriques.FabForfait;
import fabriques.FabForfaitClient;


public class TestJohnCreationDunClient extends TestCase {
	private FabClient fabClient;
	private Client leClient;
	
	@Before
	public void setUp(){
		this.fabClient = FabClient.getInstance();
	}
	
	@Test
	public void testJohnCreeUnClient() throws SQLException{
		this.leClient = this.fabClient.creerClient("Dunord","Jean-Michel","0836656565");
		assertNotNull(this.leClient);
	}
	
	@Test
	public void testJohnCreeUnClientQuiExisteDeja() throws Exception{
		ServiceClient sc = new ServiceClient();
		this.leClient = sc.creerUnClient("DesNeiges", "AbominableHomme", "0836656565");
		assertNull(this.leClient);
	}
	
	@Test
	public void testJohnRechercheUnClient() throws SQLException{
		//Au chargement de la base, dans script.sql, un client est automatiquement créé: AbominableHomme DesNeiges, idClient = 1
		assertEquals("DesNeiges",this.fabClient.rechercherClient(1).getNom());
	}
	
	@Test
	public void testJohnListeLesClients() throws Exception{
		int nbClient = -1;
		List<Client> desClients;
		PreparedStatement query = Connexion.getInstance().prepareStatement("SELECT count(*) FROM Client");
		ResultSet rs = query.executeQuery();
		if(!rs.next())
			System.out.println("vide");
		else
			nbClient = rs.getInt(1);
		
		desClients = this.fabClient.listerClient();
		assertEquals(nbClient,desClients.size());
		assertNotNull(desClients);
	}
	
	@Test
	public void testVerfierClientPossedeForfait() throws SQLException, Exception{
		ServiceClient sc = new ServiceClient();
		Client leClientSansForfait = sc.creerUnClient("Gobble","Hantouane","0238411137");
		Client leClientAvecForfait = fabClient.rechercherClient(1);
		
		assertNotNull("Ce client dois posseder un forfait",sc.verfierClientPossedeForfait(leClientAvecForfait));
		assertNull("Ce client ne dois pas posseder de forfait",sc.verfierClientPossedeForfait(leClientSansForfait));
	}
	
	@Test
	public void verifierForfaitClientEstValide() throws Exception, SQLException{
		ServiceClient sc = new ServiceClient();
		Client leClient = sc.creerUnClient("Gobble","Hantouane","0238411137");
		GregorianCalendar gc = new GregorianCalendar(1,1,2012);
		ForfaitClient leForfaitClientPasValide = FabForfaitClient.getInstance().creerForfaitClient(1,leClient.getIdentifiant(), 12,new java.sql.Date(gc.getTimeInMillis()));
		ForfaitClient leForfaitClientValide = FabForfaitClient.getInstance().rechercherForfait(1, 1);
		
		assertFalse("Ce client ne dois pas avoir de forfait valide",sc.verifierForfaitClientEstValide(leForfaitClientPasValide));
		assertTrue("Ce client doit posseder un forfait valide",sc.verifierForfaitClientEstValide(leForfaitClientValide));
	}
	
	@Test
	public void testJohnVendUnForfait() throws SQLException, Exception{
		Client client = this.fabClient.rechercherClient(1);
		Forfait leForfait = FabForfait.getInstance().rechercherForfait(1);
		ServiceClient sc = new ServiceClient();
		Client clientValide = sc.creerUnClient("GobbleLeVendeur","Hantouane","0238411137");
		
		ForfaitClient leForfaitClientValide = sc.vendreUnForfait(clientValide, leForfait);
		ForfaitClient leForfaitClientPasValide = sc.vendreUnForfait(client,leForfait);
		assertNull("Ce client possèdais deja un forfait",leForfaitClientPasValide);
		assertNotNull("Le client possède bien son nouveau forfait",leForfaitClientValide);
	}
}
