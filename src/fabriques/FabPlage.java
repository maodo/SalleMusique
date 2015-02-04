package fabriques;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;

import donnees.Plage;
public class FabPlage {
	
static FabPlage FabP; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabPlage(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT duree FROM Plage WHERE idPlage = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idPlage,duree FROM Plage;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabPlage getInstance(){
		if(FabP == null){
			FabP = new FabPlage();
		}
		return FabP;
	}
	
	public Plage rechercherPlage(int idPlage) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idPlage);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new Plage(idPlage,rs.getInt(1));
		}
	}
	
	public List<Plage> listerTranche() throws SQLException{
		List<Plage> lesPlages= new ArrayList<Plage>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			Plage unePlage = new Plage(rs.getInt(1),rs.getInt(2));
			lesPlages.add(unePlage);
		}
		
		return lesPlages;
	}
}
