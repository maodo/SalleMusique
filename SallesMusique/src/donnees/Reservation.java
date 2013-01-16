package donnees;

import java.sql.Date;

public class Reservation {
	private int idReservation;
	private int duree;
	private Date dateReservation;
	private Date dateCommande;
	private Date dateConfirmation;
	private Tarif leTarif;
	private TypeSalle leTypeSalle;
	private TrancheHoraire laTranche;
	
	public Reservation(int id,int duree,Date res,Date com,Date conf,Tarif unTarif,TypeSalle unTypeSalle,TrancheHoraire uneTranche){
		this.idReservation = id;
		this.duree = duree;
		this.dateReservation = res;
		this.dateCommande = com;
		this.dateConfirmation = conf;
		this.leTarif = unTarif;
		this.leTypeSalle = unTypeSalle;
		this.laTranche = uneTranche;
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
	
	public Tarif getTarif(){
		return this.leTarif;
	}
	
	public TypeSalle getTypeSalle(){
		return this.leTypeSalle;
	}
	
	public TrancheHoraire getTrancheHoraire(){
		return this.laTranche;
	}
	
}
