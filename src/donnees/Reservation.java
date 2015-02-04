package donnees;

import java.sql.Date;

public class Reservation {
	private int idReservation;
	private int duree;
	private int montant;
	private Date dateReservation;
	private Date dateCommande;
	private Date dateConfirmation;
	private Salle laSalle;
	private Client leClient;
	private int heureDebut;
	
	public Reservation(int id,int duree,int montant,Date res,Date com,Date conf,Salle uneSalle,Client unClient,int uneHeureDebut){
		this.idReservation = id;
		this.duree = duree;
		this.montant = montant;
		this.dateReservation = res;
		this.dateCommande = com;
		this.dateConfirmation = conf;
		this.laSalle = uneSalle;
		this.leClient = unClient;
		this.heureDebut = uneHeureDebut;
	}
	
	public int getMontantTotal(){
		return this.montant;
	}
	
	public int getIdentifiant(){
		return this.idReservation;
	}
	
	public int getDuree(){
		return this.duree;
	}
	
	public Date getDateReservation(){
		return this.dateReservation;
	}
	
	public Date getDateCommande(){
		return this.dateCommande;
	}
	
	public Date getDateConfirmation(){
		return this.dateConfirmation;
	}
	
	public Client getClient(){
		return this.leClient;
	}
	
	public Salle getSalle(){
		return this.laSalle;
	}
	
	public int getHeureDebut(){
		return this.heureDebut;
	}
	
}
