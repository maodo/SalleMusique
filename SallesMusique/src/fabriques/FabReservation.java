package fabriques;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import connecteur.Connexion;
import donnees.Client;
import donnees.Reservation;
import donnees.Tarif;
import donnees.TrancheHoraire;
import donnees.TypeSalle;

public class FabReservation {
	private static FabReservation FabR; 
	
	private PreparedStatement statementCreer;
	private PreparedStatement statementNouvelId;
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	private FabReservation(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementCreer = laConnexion.prepareStatement("INSERT INTO RESERVATION VALUES(?,?,?,?,?,?,?,?);");
			this.statementNouvelId = laConnexion.prepareStatement("");
			this.statementRechercher = laConnexion.prepareStatement("");
			this.statementLister = laConnexion.prepareStatement("");
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
	
	public Reservation rechercherReservation(int idReservation){
		return null;
	}
	
	public Reservation creerReservation(int duree,Date dateReservation,
			Date dateCommande,Date dateConfirmation,
			Tarif leTarif,TypeSalle leTypeSalle,TrancheHoraire laTranche) throws SQLException{
		
		int leNouvelId = this.getNouvelId();
		this.statementCreer.clearParameters();
		this.statementCreer.setInt(1,leNouvelId);
		this.statementCreer.setInt(2,duree);
		this.statementCreer.setDate(3,dateReservation);
		this.statementCreer.setDate(4,dateCommande);
		this.statementCreer.setDate(3,dateConfirmation);
		this.statementCreer.setInt(3,leTarif.getIdentifiant());
		this.statementCreer.setInt(3,leTypeSalle.getIdentifiant());
		this.statementCreer.setInt(3,laTranche.getIdentifiant());
		this.statementCreer.execute();
		
		return this.rechercherReservation(leNouvelId);
	}
	
	public int getNouvelId() throws SQLException{
		ResultSet rs = this.statementNouvelId.executeQuery();
		if(!rs.next())
			return -1;
		else
			return rs.getInt(1)+1;
	}
	
	public List<Client> listerClient() throws SQLException{
		List<Client> lesClients = new ArrayList<Client>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			Client unClient = new Client(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5));
			lesClients.add(unClient);
		}
		
		return lesClients;
	}
}
