package fabriques;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;
import donnees.Client;
import donnees.Reservation;
import donnees.Salle;

public class FabReservation {
	private static FabReservation FabR; 
	
	private PreparedStatement statementCreer;
	private PreparedStatement statementNouvelId;
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private PreparedStatement statementResDunJourEtDuneSalle;
	private PreparedStatement statementResDunJour;
	private PreparedStatement statementSuppr;
	private Connection laConnexion;
	
	private FabReservation(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementCreer = laConnexion.prepareStatement("INSERT INTO RESERVATION VALUES(?,?,?,?,?,?,?,?,?);");
			this.statementNouvelId = laConnexion.prepareStatement("SELECT MAX(idReservation) FROM Reservation;");
			this.statementRechercher = laConnexion.prepareStatement("SELECT * FROM Reservation WHERE idReservation = ?");
			this.statementLister = laConnexion.prepareStatement("SELECT * FROM Reservation;");
			this.statementResDunJourEtDuneSalle = laConnexion.prepareStatement("SELECT * FROM Reservation WHERE dateReservation = ? AND idSalle = ?;");
			this.statementResDunJour = laConnexion.prepareStatement("SELECT * FROM Reservation WHERE dateReservation = ?;");
			this.statementSuppr = laConnexion.prepareStatement("DELETE FROM Reservation WHERE idReservation = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabReservation getInstance(){
		if(FabR == null){
			FabR = new FabReservation();
		}
		return FabR;
	}
	
	public Reservation rechercherReservation(int idReservation) throws SQLException{
		Reservation laReservation = null;
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idReservation);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(rs.next()){
			Salle uneSalle = FabSalle.getInstance().rechercherSalle(rs.getInt(7));
			Client unClient = FabClient.getInstance().rechercherClient(rs.getInt(8));
			laReservation = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getDate(4),rs.getDate(5),rs.getDate(6),uneSalle,unClient,rs.getInt(9));
		}
		return laReservation;
	}
	
	public Reservation creerReservation(int duree,int montant,Date dateReserv,Date dateCommmande,
			Date dateConfirm,Salle uneSalle,Client unClient,int uneHeureDebut) throws SQLException{
		int leNouvelId = this.getNouvelId();
		this.statementCreer.clearParameters();
		this.statementCreer.setInt(1,leNouvelId);
		this.statementCreer.setInt(2,duree);
		this.statementCreer.setInt(3,montant);
		this.statementCreer.setDate(4,dateReserv);
		this.statementCreer.setDate(5,dateCommmande);
		this.statementCreer.setDate(6,dateConfirm);
		this.statementCreer.setInt(7,uneSalle.getIdentifiant());
		this.statementCreer.setInt(8,unClient.getIdentifiant());
		this.statementCreer.setInt(9,uneHeureDebut);
		this.statementCreer.execute();
		
		return new Reservation(leNouvelId, duree, montant, dateReserv, dateCommmande, dateConfirm, uneSalle, unClient, uneHeureDebut);
	}
	
	public int getNouvelId() throws SQLException{
		ResultSet rs = this.statementNouvelId.executeQuery();
		if(!rs.next())
			return 1;
		else
			return rs.getInt(1)+1;
	}
	
	public List<Reservation> listerReservation() throws SQLException{
		List<Reservation> lesReservation = new ArrayList<Reservation>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			Salle laSalle = FabSalle.getInstance().rechercherSalle(rs.getInt(7));
			Client leClient = FabClient.getInstance().rechercherClient(rs.getInt(8));
			Reservation unReservation = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getDate(4),rs.getDate(5),rs.getDate(6),laSalle,leClient,rs.getInt(9));
			lesReservation.add(unReservation);
		}
		
		return lesReservation;
	}

	public List<Reservation> listerReservationDunJourEtDuneSalle(
			java.sql.Date laDate, Salle laSalle) throws SQLException{
		List<Reservation> desReservations = new ArrayList<Reservation>();
		this.statementResDunJourEtDuneSalle.clearParameters();
		this.statementResDunJourEtDuneSalle.setDate(1,laDate);
		this.statementResDunJourEtDuneSalle.setInt(2,laSalle.getIdentifiant());
		ResultSet rs = this.statementResDunJourEtDuneSalle.executeQuery();
		while(rs.next()){
			Salle uneSalle = FabSalle.getInstance().rechercherSalle(rs.getInt(7));
			Client unClient = FabClient.getInstance().rechercherClient(rs.getInt(8));
			
			Reservation uneReservation = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getDate(4),rs.getDate(5),rs.getDate(6),uneSalle,unClient,rs.getInt(9));
			desReservations.add(uneReservation);
		}
		return desReservations;
	}
	
	public boolean supprimerReservation(Reservation laReservation) throws SQLException{
		this.statementSuppr.clearParameters();
		this.statementSuppr.setInt(1,laReservation.getIdentifiant());
		return this.statementSuppr.execute();
	}

	public List<Reservation> listerReservationDunJour(java.sql.Date laDate) throws SQLException{
		List<Reservation> desReservations = new ArrayList<Reservation>();
		this.statementResDunJour.clearParameters();
		this.statementResDunJour.setDate(1,laDate);
		ResultSet rs = this.statementResDunJour.executeQuery();
		while(rs.next()){
			Salle uneSalle = FabSalle.getInstance().rechercherSalle(rs.getInt(7));
			Client unClient = FabClient.getInstance().rechercherClient(rs.getInt(8));
			
			Reservation uneReservation = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getDate(4),rs.getDate(5),rs.getDate(6),uneSalle,unClient,rs.getInt(9));
			desReservations.add(uneReservation);
		}
		return desReservations;
	}
}
