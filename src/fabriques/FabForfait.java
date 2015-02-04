package fabriques;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;

import donnees.Forfait;

public class FabForfait {
static FabForfait FabF; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabForfait(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT nomForfait,validite FROM Forfait WHERE idForfait = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idForfait,nomForfait,validite FROM Forfait;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabForfait getInstance(){
		if(FabF == null){
			FabF = new FabForfait();
		}
		return FabF;
	}
	
	public Forfait rechercherForfait(int idForfait) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idForfait);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new Forfait(idForfait,rs.getString(1),rs.getInt(2));
		}
	}
	
	public List<Forfait> listerForfait() throws SQLException{
		List<Forfait> lesForfaits= new ArrayList<Forfait>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			Forfait unForfait = new Forfait(rs.getInt(1),rs.getString(2),rs.getInt(3));
			lesForfaits.add(unForfait);
		}
		
		return lesForfaits;
		
	}
}
