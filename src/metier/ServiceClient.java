package metier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import connecteur.Connexion;
import donnees.Client;
import donnees.Forfait;
import donnees.ForfaitClient;
import donnees.Reservation;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabForfait;
import fabriques.FabForfaitClient;
import fabriques.FabTarifForfaitaire;

public class ServiceClient {
	private FabClient fabClient;
	private static ServiceClient servClient;
	private PreparedStatement statementVerifSiClientExiste;
	private PreparedStatement statementConfirmerReservation;
	private PreparedStatement statementUtiliserPointsfidelite;
	private PreparedStatement statementUtiliserForfaitClient;
	private PreparedStatement statementAjouterPointsClient;
	private Connection laConnexion;
	
	private ServiceClient() throws Exception{
		this.fabClient = FabClient.getInstance();
		this.laConnexion = Connexion.getInstance();
		
	}
	
	public static ServiceClient getInstance() throws Exception{
		if(servClient == null)
			return new ServiceClient();
		else
			return servClient;
	}
	
	/*
	 * cree un nouveau client en base
	 * retourne le client si il est nouveau
	 * retourne null si le client existe deja
	 */
	public Client creerUnClient(String nom, String prenom, String numTel) throws SQLException{
		Client leNouveauClient = null;
		this.statementVerifSiClientExiste = laConnexion.prepareStatement("SELECT idClient FROM Client where nomClient='"+nom+"' and PrenomClient='"+prenom+"' and numTel='"+numTel+"';");
		ResultSet rs = this.statementVerifSiClientExiste.executeQuery();
		if(!rs.next()){
			//ce client n'existe pas deja, on le cree
			try {
				leNouveauClient = this.fabClient.creerClient(nom, prenom, numTel);
			} catch (SQLException e) {
				System.out.println("erreur lors de la creation du nouveau client");
				e.printStackTrace();
			}
			return leNouveauClient;
		}else
		{
			//ce client existe deja
			System.out.println("Ce client existe deja");
			//return fabClient.getInstance().rechercherClient(rs.getInt(1));
			//ou
			return null;
		}
	}
	
	/*
	 * le client confirme sa reservation
	 * il a choisi sur l'ihm s'il souhaite utiliser ses points et / ou son forfait pour regler 
	 * tout ou partie de la reservaton
	 */
	public void confirmerUneServation(Client client, Reservation laReservation) throws Exception
	{
		//param a recup de l'ihm
		boolean utilisePoints = true;
		boolean utiliseForfait = true;
		
		int Hdeb = laReservation.getHeureDebut();
		int duree = laReservation.getDuree();
		System.out.println("[confirmerUneServation]heure debut :  : " +Hdeb );
		System.out.println("[confirmerUneServation]duree :  : " +duree );
		if(utilisePoints)
		{
			System.out.println("[confirmerUneServation]le client utilise ses points" );
			int heuresPossibles = this.utiliserPointsFidelite(client, duree);
			System.out.println("[confirmerUneServation]les points lui permettent davoir "+heuresPossibles+" heures" );
			//on met a jour lheure debut et la duree restantes
			Hdeb = Hdeb+heuresPossibles;
			duree = duree - heuresPossibles;
			if(duree <0){duree =0;}//au cas ou le client utilise 150 point pour une seule heure
			System.out.println("[confirmerUneServation]nouvelle duree "+duree );
		}
		if(utiliseForfait)
		{
		 ForfaitClient FC = this.verfierClientPossedeForfait(client,laReservation.getSalle().getTypeSalle());
			 if(FC != null)
			 {
				 System.out.println("[confirmerUneServation]le client a un forfait" );
				int heuresDisponibles2 = this.utiliserForfait(FC, duree);

				//on met a jour lheure debut et la duree
				Hdeb = Hdeb+heuresDisponibles2;
				duree = duree-heuresDisponibles2;
				System.out.println("[confirmerUneServation]reste "+duree+" heures a payer");
			 }
		}
		//le client a maintenant eventuellement utilisé ses points et/ou forfait
		//il nous reste lheure de debut et la duree de ce qu'il reste a payer
		//on calcule le montant de ses heures restantes
		ServiceReservation sr= ServiceReservation.getInstance();
		
		//calcul du montant (en prenant en compte les eventuelles heures payees avec pts fidelites et/ou forfait 
		double montant = sr.calculerMontantReservation(laReservation, duree);
		System.out.println("[confirmerUneServation]montant de la reservation : "+montant +" euros");
		
		//recuperation de la date de commande
		java.sql.Date date = laReservation.getDateCommande();
		
		
		//mise a jour de la date de confirmation
		//TODO : elle est settée a la date de commande
		this.statementConfirmerReservation = laConnexion.prepareStatement("update reservation set  dateconfirmation ='"+date+"'  where idreservation ="+laReservation.getIdentifiant()+"");
		try
		{
		this.statementConfirmerReservation .executeUpdate();
		}
		catch (Exception e)
		{
			System.out.println("Erreur lors de la confirmation de reservation");
		}
		
	}
	
	
	
	
	
	/*
	 * verifie pour un client et un type salle, si le client possede un forfait pour ce genre de salle
	 * retourne le forfait s'il existe, null sinon
	 */
	public ForfaitClient verfierClientPossedeForfait(Client client, TypeSalle letypeSalle) throws SQLException{
		List<Forfait> lesForfaits = FabForfait.getInstance().listerForfait();
		
		for (Forfait f : lesForfaits){
			ForfaitClient FC = FabForfaitClient.getInstance().rechercherForfait(f.getIdentifiant(), client.getIdentifiant());
			if (FC != null && FC.getIdTypeSalle() == letypeSalle.getIdentifiant()){
				System.out.println("le client possede deja un forfait pour ce type de salle");
				return FC;
			}
		}
		//si le client n'a pas de forfait pour ce type de salle, on retourne null
		return null;
	}
	 
	/*
	 * verifie si le forfait d'un client n'est pas perimé
	 * retourne vrai ou faux
	 */
	public boolean verifierForfaitClientEstValide(ForfaitClient FC) throws SQLException
	{
		
		Date dateAchatForfait = FC.getDateAchat();
		System.out.println("dateAchatForfait : "+dateAchatForfait);
		Date DateAujourdhui = new Date();
		System.out.println("DateAujourdhui : "+DateAujourdhui);
		//recuperation du type de forfait pour connaitre la validite
		int idforfait = FC.getIdForfait();
		int validite = FabForfait.getInstance().rechercherForfait(idforfait).getValidite();
		System.out.println(" validite : "+validite+" mois");
		
		Calendar c= Calendar.getInstance();
		c.setTime(dateAchatForfait);
		c.set(Calendar.MONTH, (c.get(Calendar.MONTH)+validite));  
		Date echeance = c.getTime();
		System.out.println("echeance : "+echeance);
		
		if (DateAujourdhui.before(echeance))
		{
			System.out.println("OK forfait valide");
			return true;
		}
		else
		{
			System.out.println("PAS OK forfait non valide");
		//TODO : supprimer le forfaitclient
		return false;
		}
	}
	

	/*
	 * pour un client donné, le nombre d'heures de la reservation, 
	 * retourne le nombre d'heures dispos grace aux points
	 * apres avoir retiré les points correspondants
	 */
	public int utiliserPointsFidelite(Client client, int nbheures) throws SQLException
	{
		//on specifie le client et le nb d'heures qu'il doit acheter, 
		//la methode effectue le calcul en fct de ses points de fidelite
		//et retourne, apres avoir enlev� les points fidelites, 
		//le nombre d'heures obtenues grace aux points
		int nbPoints = client.getPointsFid();
		int heuresPossibles = (nbPoints/150)*2;
		System.out.println("[ utiliserPointsFidelite]le client "+client.getPrenom()+" a "+nbPoints+" points" );
		System.out.println("[ utiliserPointsFidelite]les points permettent davoir "+heuresPossibles+" heure(s)" );
		//mettre a jour le nb de points du client en base
		int nbpointsdispos = nbPoints - ((heuresPossibles/2)*150);
		System.out.println("[ utiliserPointsFidelite]le client "+client.getPrenom()+" a maintenant "+nbpointsdispos+" points" );
		this.statementUtiliserPointsfidelite = laConnexion.prepareStatement("update Client set nbPointsFid="+nbpointsdispos+" where  idclient ="+client.getIdentifiant()+"");
		try
		{
		this.statementUtiliserPointsfidelite.executeUpdate();
		}
		catch (Exception e)
		{
			System.out.println("Erreur lors de l'utilisation des points de fidelite");
		}
		return heuresPossibles;
	}
	


	/*
	 * utilise le forfait d'un client pour regler les heures de reservation
	 * utilise tout ou partie des heures dispo et renvoie le nombre d'heures possibles
	 */
	public int utiliserForfait(ForfaitClient FC, int nbheuresVoulues) throws SQLException
	{
		int nbheuresDispo = FC.getCredit();
		System.out.println("[utiliserForfait]credit du forfait : "+nbheuresDispo );
		System.out.println("[utiliserForfait]cnb d'heures voulues : "+nbheuresVoulues );
		int nveauNbHeuresDispo = 0;
		int nbHeuresUtilisables = 0;
		if(nbheuresDispo > nbheuresVoulues)
		{
			
			nbHeuresUtilisables = nbheuresVoulues;
			nveauNbHeuresDispo = nbheuresDispo - nbheuresVoulues;
			System.out.println("[utiliserForfait]il ya assez d'heures sur le forfait pour obtenir ttes les heures souhaitees");
			System.out.println("[utiliserForfait]nouveau credit forfait : "+nveauNbHeuresDispo );
			//debite le credit (en heures) du forfait d'un client
			//mise a jour du credit du forfaitclient
			this.statementUtiliserForfaitClient = laConnexion.prepareStatement("update ForfaitClient set  credit ='"+nveauNbHeuresDispo+"'  where idForfait ="+FC.getIdForfait()+" and idclient ="+FC.getIdClient()+"");
			try
			{
			this.statementUtiliserForfaitClient.executeUpdate();
			}
			catch (Exception e)
			{
				System.out.println("Erreur lors de l'utilistion du forfait");
			}
		}
		else if(nbheuresDispo <= nbheuresVoulues)
		{
			System.out.println("[utiliserForfait]il nya pas assez d'heures sur le forfait pour obtenir ttes les heures souhaitees : suppression du forfait");
			nbHeuresUtilisables = nbheuresDispo;
			System.out.println("[utiliserForfait]nombre d'heures utilisables : "+nbHeuresUtilisables);
			nveauNbHeuresDispo = 0;
			//mise a jour du credit du forfaitclient => suppression de ce forfait
			this.statementUtiliserForfaitClient = laConnexion.prepareStatement("delete from forfaitClient where idForfait ="+FC.getIdForfait()+" and idclient ="+FC.getIdClient()+"");
			try
			{
			this.statementUtiliserForfaitClient.executeUpdate();
			}
			catch (Exception e)
			{
				System.out.println("Erreur lors de l'utilistion du forfait");
			}
		}
		return nbHeuresUtilisables;
	}
	
	public void ajouterPointsClient(Client c,int points) throws SQLException
	{
		this.statementAjouterPointsClient = laConnexion.prepareStatement("update Client set nbpointsfid = nbpointsfid+"+points+" where idclient = "+c.getIdentifiant()+"");
		this.statementAjouterPointsClient.executeQuery();
	}
	
	
	/*
	 * methode de vente d'un forfait a un client, selon le type de salle
	 */
	public ForfaitClient vendreUnForfait(Client client,Forfait leForfait, TypeSalle letypeSalle) throws SQLException
	{
		
		System.out.println("[ vendreUnForfait]entree");
		ForfaitClient leForfaitClient = null;
		
		//verif que le client ne possede pas deja un forfait
		  ForfaitClient fc = this.verfierClientPossedeForfait(client, letypeSalle);
		  if(fc != null && this.verifierForfaitClientEstValide(fc))
		  {
			  if(this.verifierForfaitClientEstValide(fc))
			  {
				  
				  System.out.println("pas ok : Le client possede deja un forfait Pour ce type de salle");
			  }
			  else
			  {
				  System.out.println("OK : Le client possedait deja un forfait pour ce type de salle mais non valide, il a donc ete supprime");
			  }
		  }
		  else{
			  //ok le client ne possede pas deja de forfait, on le cree
			  //recuperation du credit
			  String str[]=leForfait.getNomForfait().split("h");
			  int credit =  Integer.parseInt(str[0]);
			  //creation de la date
			  java.util.Date dateAujourdhui = new java.util.Date(); 
			  System.out.println(dateAujourdhui);
			  java.sql.Date dateSql = new java.sql.Date(dateAujourdhui.getTime());
			  //recuperation du prix du forfait pour cette salle
			 int prix = FabTarifForfaitaire.getInstance().rechercherTarifForfaitaire(leForfait.getIdentifiant(), letypeSalle.getIdentifiant()).getPrix();
			  //creation du forfaitclient
			  leForfaitClient = FabForfaitClient.getInstance().creerForfaitClient(leForfait.getIdentifiant(), client.getIdentifiant(), credit, dateSql,letypeSalle.getIdentifiant(),prix);
		  }
		  return leForfaitClient;
	}
}