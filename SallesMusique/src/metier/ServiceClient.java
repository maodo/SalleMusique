package metier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import connecteur.Connexion;

import donnees.Client;
import donnees.Forfait;
import donnees.ForfaitClient;
import donnees.Reservation;
import fabriques.FabClient;
import fabriques.FabForfait;
import fabriques.FabForfaitClient;
import fabriques.FabReservation;

public class ServiceClient {

	private FabClient fabClient;
	private FabReservation fabReservation;
	private PreparedStatement statementVerifSiClientExiste;
	private PreparedStatement statementConfirmerReservation;
	private PreparedStatement statementUtiliserPointsfidelite;
	private PreparedStatement statementUtiliserForfaitClient;
	private Connection laConnexion;
	
	public ServiceClient() throws Exception{
		this.fabClient = FabClient.getInstance();
		this.laConnexion = Connexion.getInstance();
		
	}
	
	public Client creerUnClient(String nom, String prenom, String numTel) throws SQLException{
		Client leNouveauClient = null;
		this.statementVerifSiClientExiste = laConnexion.prepareStatement("SELECT idClient FROM Client where nomClient='"+nom+"' and PrenomClient='"+prenom+"' and numTel='"+numTel+"';");
		ResultSet rs = this.statementVerifSiClientExiste.executeQuery();
		if(!rs.next()){
			//ce client n'existe pas deja, on le cree
			try {
				leNouveauClient = this.fabClient.creerClient(nom, prenom, numTel);
			} catch (SQLException e) {
				System.out.println("nouveau client cree");
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
	
	
	public void confirmerUneServation(Client client, Reservation laReservation) throws SQLException
	{
		//recuperation de la date d'aujourd'hui
		String format = "dd/MM/yy H:mm:ss"; 
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
		java.util.Date date = new java.util.Date();
		
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
	
	public ForfaitClient verfierClientPossedeForfait(Client client) throws SQLException
	{
		List<Forfait> lesForfaits = FabForfait.getInstance().listerForfait();
		//pour les 2 types de forfaits (12,24), on regarde si le client en a un et on le retourne
		for (Forfait f : lesForfaits)
		{
			ForfaitClient FC = FabForfaitClient.getInstance().rechercherForfait(f.getIdentifiant(), client.getIdentifiant());
			if (FC != null)
			{
				return FC;
			}
		}
		//si le client n'a pas de forfait, on retourne null
		return null;
	}
	
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
	
	//on specifie le client et le nb d'heures qu'il doit acheter, 
	//la methode effectue le calcul en fct de ses points de fidelite
	//et retourne, apres avoir enlevé les points fidelites, 
	//le nombre d'heures obtenues grace aux points
	public int utiliserPointsFidelite(Client client, int nbheures) throws SQLException
	{
		int nbPoints = client.getPointsFid();
		int heuresPossibles = (nbPoints/150)*2;
		
		//mettre a jour le nb de points du client en base
		int nbpointsdispos = nbPoints - ((heuresPossibles/2)*150);
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
	
	//debite le credit (en heures) du forfait d'un client
	//retourne le nombre d'heures que peut acheter ce forfait
	//apres avoir debite ce forfait
	public int utiliserForfait(ForfaitClient FC, int nbheuresVoulues) throws SQLException
	{
		int nbheuresDispo = FC.getCredit();
		int nveauNbHeuresDispo = 0;
		int nbHeuresUtilisables = 0;
		if(nbheuresDispo > nbheuresVoulues)
		{
			nbHeuresUtilisables = nbheuresVoulues;
			nveauNbHeuresDispo = nbheuresDispo - nbheuresVoulues;
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
			nbHeuresUtilisables = nbheuresDispo;
			nveauNbHeuresDispo = 0;
			//mise a jour du credit du forfaitclient => suppression de ce forfait
			this.statementUtiliserForfaitClient = laConnexion.prepareStatement("delete forfaitCleint where idForfait ="+FC.getIdForfait()+" and idclient ="+FC.getIdClient()+"");
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
	
	
	public void vendreUnForfait(Client client,Forfait leForfait) throws SQLException
	{
		
		boolean possedeUnForfait = false;
		//verif que le client ne possede pas deja un forfait
	  ForfaitClient fc = this.verfierClientPossedeForfait(client);
	  if(fc != null)
	  {
		  if(this.verifierForfaitClientEstValide(fc))
		  {
			  possedeUnForfait = true;
			  System.out.println("pas ok : Le client possede deja un forfait");
		  }
		  else
		  {
			  System.out.println("OK : Le client possedait deja un forfait mais non valide, il a donc ete supprime");
		  }
	  }
	  else
	  {
		  //ok le client ne possede pas deja de forfait, on le cree
		  //recuperation du credit
		  String str[]=leForfait.getNomForfait().split("h");
		  int credit =  Integer.parseInt(str[0]);
		  //creation de la date
		  String format = "dd/MM/yy H:mm:ss";
		  java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
		  java.util.Date dateAujourdhui = new java.util.Date(); 
		  System.out.println(dateAujourdhui);
		  java.sql.Date dateSql = new java.sql.Date(dateAujourdhui.getTime());
		  //creation du forfaitclient
		 FabForfaitClient.getInstance().creerForfaitClient(leForfait.getIdentifiant(), client.getIdentifiant(), credit, dateSql);
	  }
	  
	}
	
	
	public static void main(String[] args) throws Exception
	{	
	ServiceClient sc = new ServiceClient();
	ForfaitClient FC = FabForfaitClient.getInstance().rechercherForfait(1, 1);
	//sc.verifierForfaitClientEstValide(FC);
	
	Client c = FabClient.getInstance().rechercherClient(2);
	Forfait f = FabForfait.getInstance().rechercherForfait(FC.getIdForfait());
	sc.vendreUnForfait(c, f);
	}
	
}
