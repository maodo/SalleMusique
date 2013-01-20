package fabriques;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;
import donnees.TarifForfaitaire;

public class FabTarifForfaitaire {
	static FabTarifForfaitaire FabTF; 
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabTarifForfaitaire(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT prix FROM TarifForfait WHERE idTypeSalle= ? and idForfait =?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idTypeSalle,idForfait,prix  FROM TarifForfait;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabTarifForfaitaire getInstance(){
		if(FabTF == null){
			FabTF = new FabTarifForfaitaire();
		}
		return FabTF;
	}
	
	public TarifForfaitaire rechercherTarifForfaitaire (int idPlage ,int idTypeSalle) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idPlage);
		this.statementRechercher.setInt(2,idTypeSalle);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new TarifForfaitaire(rs.getInt(3), FabTypeSalle.getInstance().rechercherTypeSalle(rs.getInt(1)), FabForfait.getInstance().rechercherForfait(rs.getInt(2)));
		}
	}
	
	public List<TarifForfaitaire > listerTarifForfaitaire () throws SQLException{
		List<TarifForfaitaire > lesTarifsForfaitaires  = new ArrayList<TarifForfaitaire >();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			TarifForfaitaire  unTarifForfaitaire = new TarifForfaitaire(rs.getInt(3), FabTypeSalle.getInstance().rechercherTypeSalle(rs.getInt(1)), FabForfait.getInstance().rechercherForfait(rs.getInt(2)));
			lesTarifsForfaitaires.add( unTarifForfaitaire);
		}
		
		return lesTarifsForfaitaires;
	}
}
