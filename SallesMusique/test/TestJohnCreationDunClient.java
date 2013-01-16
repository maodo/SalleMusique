import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import connecteur.Connexion;

import donnees.Client;
import fabriques.FabClient;


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
	
	public void testJohnRechercheUnClient() throws SQLException{
		//Au chargement de la base, dans script.sql, un client est automatiquement créé: AbominableHomme DesNeiges, idClient = 1
		assertEquals("DesNeiges",this.fabClient.rechercherClient(1).getNom());
	}
	
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
}
