package fabriques;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;
import donnees.TarifPonctuel;

public class FabTarifPonctuel {
	
	static FabTarifPonctuel FabT; 
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	
	public FabTarifPonctuel(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT prix FROM TarifPonctuel WHERE idPlage= ? and idTypeSalle =?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idPlage,idTypeSalle,prix  FROM TarifPonctuel;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabTarifPonctuel getInstance(){
		if(FabT == null){
			FabT = new FabTarifPonctuel();
		}
		return FabT;
	}
	
	public TarifPonctuel rechercherTarifPonctuel(int idPlage ,int idTypeSalle) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idPlage);
		this.statementRechercher.setInt(2,idTypeSalle);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new TarifPonctuel(rs.getInt(1), FabTypeSalle.getInstance().rechercherTypeSalle(idTypeSalle), FabPlage.getInstance().rechercherPlage(idPlage));
		}
	}
	
	public List<TarifPonctuel> listerTarifPonctuel() throws SQLException{
		List<TarifPonctuel> lesTarifsPonctuels = new ArrayList<TarifPonctuel>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			TarifPonctuel unTarifPonctuel = new TarifPonctuel(rs.getInt(1), FabTypeSalle.getInstance().rechercherTypeSalle(rs.getInt(2)), FabPlage.getInstance().rechercherPlage(rs.getInt(1)));
			lesTarifsPonctuels.add(unTarifPonctuel);
		}
		
		return lesTarifsPonctuels;	
	}	
}
