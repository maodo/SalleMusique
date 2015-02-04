package fabriques;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connecteur.Connexion;

import donnees.Client;

public class FabClient {
	static FabClient FabC; 
	
	private PreparedStatement statementCreer;
	private PreparedStatement statementNouvelId;
	private PreparedStatement statementRechercher;
	private PreparedStatement statementLister;
	private Connection laConnexion;
	
	public FabClient(){
		this.initRequetes();
	}
	
	private void initRequetes(){
		try {
			this.laConnexion = Connexion.getInstance();
			this.statementCreer = laConnexion.prepareStatement("INSERT INTO CLIENT(idClient,nomClient,prenomClient,nbPointsFid,numTel) VALUES(?,?,?,0,?);");
			this.statementNouvelId = laConnexion.prepareStatement("SELECT MAX(idClient) FROM CLIENT;");
			this.statementRechercher = laConnexion.prepareStatement("SELECT idClient,nomClient,PrenomClient,nbPointsFid,numTel FROM Client WHERE idClient = ?;");
			this.statementLister = laConnexion.prepareStatement("SELECT idClient,nomClient,PrenomClient,nbPointsFid,numTel FROM Client;");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static FabClient getInstance(){
		if(FabC == null){
			FabC = new FabClient();
		}
		return FabC;
	}
	
	public int getNouvelId() throws SQLException{
		ResultSet rs = this.statementNouvelId.executeQuery();
		if(!rs.next())
			return -1;
		else
			return rs.getInt(1)+1;
	}
	
	public Client creerClient(String nom, String prenom, String tel) throws SQLException{
		int leNouvelIdClient = this.getNouvelId();
		this.statementCreer.clearParameters();
		this.statementCreer.setInt(1,leNouvelIdClient);
		this.statementCreer.setString(2,nom);
		this.statementCreer.setString(3,prenom);
		this.statementCreer.setString(4, tel);
		this.statementCreer.execute();
		
		return this.rechercherClient(leNouvelIdClient);
	}
	
	public Client rechercherClient(int idClient) throws SQLException{
		this.statementRechercher.clearParameters();
		this.statementRechercher.setInt(1,idClient);
		ResultSet rs = this.statementRechercher.executeQuery();
		if(!rs.next()){
			return null;
		}else{
			return new Client(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5));
		}
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
	
	public static void main(String[] args) throws Exception{		
		List<Client> desClients = FabClient.getInstance().listerClient();
		for(Client c : desClients){
			System.out.println(c.getIdentifiant()+" "+c.getNom()+" "+c.getPrenom());
		}
	
		Connexion.getInstance().close();
	}
}
