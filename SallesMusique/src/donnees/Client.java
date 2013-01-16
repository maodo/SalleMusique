package donnees;

public class Client {

	private int idClient;
	private String nomClient;
	private String prenomClient;
	private int nbPointsFid;
	private String numTelephone;
	
	public Client(int id,String nom,String prenom,int nbPts,String tel){
		this.idClient = id;
		this.nomClient = nom;
		this.prenomClient = prenom;
		this.numTelephone = tel;
		this.nbPointsFid = nbPts;
	}
	
	public int getIdentifiant(){
		return this.idClient;
	}
	
	public String getNom(){
		return this.nomClient;
	}
	
	public String getPrenom(){
		return this.prenomClient;
	}
	
	public int getPointsFid(){
		return this.nbPointsFid;
	}
	
	public String getNumTelephone(){
		return this.numTelephone;
	}
}
