package fabriques;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connecteur.Connexion;
import donnees.ForfaitClient;

public class FabForfaitClient {
static FabForfaitClient FabFC; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementCreer;
	private PreparedStatement statementSupprimer;
	private Connection laConnexion;
	
	public FabForfaitClient(){
		this.initRequetes();
	}
	
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementCreer = laConnexion.prepareStatement("INSERT INTO ForfaitClient VALUES(?,?,?,?,?,?)");
			this.statementRechercher = laConnexion.prepareStatement("SELECT idForfait,idClient, credit,dateAchat, idTypeSalle, prix FROM ForfaitClient WHERE idForfait = ? AND idClient = ?;");
			this.statementSupprimer = laConnexion.prepareStatement("DELETE FRoM forfaitclient where idforfait=? and idclient=?");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static FabForfaitClient getInstance(){
		if(FabFC == null){
			FabFC = new FabForfaitClient();
		}
		return FabFC;
	}
	
	
	public ForfaitClient rechercherForfait(int idForfait, int idClient) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idForfait);
		this.statementRechercher.setInt(2,idClient);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new ForfaitClient(rs.getInt(1),rs.getInt(2),rs.getInt(3), rs.getDate(4), rs.getInt(5),rs.getInt(6));
		}
	}
	
	
	public ForfaitClient creerForfaitClient(int idForfait, int idClient, int credit, Date dateAchat, int idtypesalle, int prix) throws SQLException{
		
		this.statementCreer.clearParameters();
		this.statementCreer.setInt(1,idForfait);
		this.statementCreer.setInt(2,idClient);
		this.statementCreer.setInt(3,credit);
		this.statementCreer.setDate(4,dateAchat);
		this.statementCreer.setInt(5,idtypesalle);
		this.statementCreer.setInt(6,prix);
		this.statementCreer.execute();
		
		System.out.println("insertion forfaitclient ok pour client "+idClient);
		return FabForfaitClient.getInstance().rechercherForfait(idForfait, idClient);
		
	}
	
	public void supprimerForfaitClient(int idforfait, int idclient) throws SQLException
	{
		this.statementSupprimer.clearParameters();
		this.statementSupprimer.setInt(1,idforfait);
		this.statementSupprimer.setInt(2,idclient);
		this.statementSupprimer.execute();
		System.out.println("forfait supprime");
	}
	
	
}