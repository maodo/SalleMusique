package fabriques;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;

import donnees.TypeSalle;


public class FabTypeSalle {
static FabTypeSalle  FabT; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabTypeSalle(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT libelle FROM TypeSalle WHERE idTypeSalle = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idTypeSalle ,libelle FROM TypeSalle;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static FabTypeSalle getInstance(){
		if(FabT == null){
			FabT = new FabTypeSalle();
		}
		return FabT;
	}
	
	public TypeSalle rechercherTypeSalle(int idTypeSalle) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idTypeSalle);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new TypeSalle(idTypeSalle,rs.getString(1));
		}
	}
	
	public List<TypeSalle> listerTypeSalle() throws SQLException{
		List<TypeSalle> lesTypesSalles = new ArrayList<TypeSalle >();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			TypeSalle  unTypeSalle = new TypeSalle (rs.getInt(1),rs.getString(2));
			lesTypesSalles.add(unTypeSalle);
		}
		
		return lesTypesSalles;
	}	
}
