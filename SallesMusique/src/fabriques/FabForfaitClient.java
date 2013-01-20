package fabriques;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import connecteur.Connexion;
import donnees.Client;
import donnees.Forfait;
import donnees.ForfaitClient;

public class FabForfaitClient {
static FabForfaitClient FabFC; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementCreer;
	private Connection laConnexion;
	
	public FabForfaitClient(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementCreer = laConnexion.prepareStatement("insert into forfaitclient values (?,?,?,?)");
			this.statementRechercher = laConnexion.prepareStatement("SELECT idForfait,idClient, credit,dateAchat FROM ForfaitClient WHERE idForfait = ? AND idClient = ?;");
		} catch (SQLException e) {
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
			return new ForfaitClient(rs.getInt(1),rs.getInt(2),rs.getInt(3), rs.getDate(4));
		}
	}
	
	
	public ForfaitClient creerForfaitClient(int idForfait, int idClient, int credit, Date dateAchat) throws SQLException{
		
		this.statementCreer.clearParameters();
		this.statementCreer.setInt(1,idForfait);
		this.statementCreer.setInt(2,idClient);
		this.statementCreer.setInt(3,credit);
		this.statementCreer.setDate(4,dateAchat);
		this.statementCreer.execute();
		
		System.out.println("insertion forfaitclient ok pour client "+idClient);
		return FabForfaitClient.getInstance().rechercherForfait(idForfait, idClient);
	}
	
	
	
}
