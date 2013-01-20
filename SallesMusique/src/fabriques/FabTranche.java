package fabriques;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;

import donnees.TrancheHoraire;;
public class FabTranche {
	
	static FabTranche FabT; 
	
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabTranche (){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementRechercher = laConnexion.prepareStatement("SELECT libelle FROM TrancheHoraire WHERE idTranche = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idTranche,libelle FROM TrancheHoraire;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static FabTranche getInstance(){
		if(FabT == null){
			FabT = new FabTranche();
		}
		return FabT;
	}
	
	public TrancheHoraire rechercherTranche(int idTranche) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idTranche);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new TrancheHoraire(idTranche,rs.getString(1));
		}
	}
	
	public List<TrancheHoraire> listerTranche() throws SQLException{
		List<TrancheHoraire> lesTranchesHoraires= new ArrayList<TrancheHoraire>();
		ResultSet rs = this.statementLister.executeQuery();
		while(rs.next()){
			TrancheHoraire uneTranche = new TrancheHoraire(rs.getInt(1),rs.getString(2));
			lesTranchesHoraires.add(uneTranche);
		}
		
		return lesTranchesHoraires;
	}
	
	public static void main(String[] args) throws Exception{		
		List<TrancheHoraire> desTranches = FabTranche.getInstance().listerTranche();
		for(TrancheHoraire t : desTranches){
			System.out.println(t.getIdentifiant()+" "+t.getLibelle());
		}
	
		Connexion.getInstance().close();
	}

}
