package fabriques;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;
import donnees.Salle;
import donnees.TypeSalle;


public class FabSalle {
static FabSalle  FabS; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabSalle(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT typeSalle FROM Salle WHERE idSalle = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT * FROM Salle;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static FabSalle getInstance(){
		if(FabS == null){
			FabS = new FabSalle();
		}
		return FabS;
	}
	
	public Salle rechercherSalle(int idSalle) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idSalle);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(rs.getInt(1));
			return new Salle(idSalle,leTypeSalle);
		}
	}
	
	public List<Salle> listerSalle() throws SQLException{
		List<Salle> lesSalles = new ArrayList<Salle>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(rs.getInt(2));
			Salle  uneSalle = new Salle (rs.getInt(1),leTypeSalle);
			lesSalles.add(uneSalle);
		}
		return lesSalles;
	}	
}
