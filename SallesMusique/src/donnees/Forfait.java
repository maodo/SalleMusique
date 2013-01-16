package donnees;

public class Forfait {
	private int idForfait;
	private String nomForfait;
	private int validite;
	private int prix;
	
	public Forfait(int id,String nom,int valid){
		this.idForfait = id;
		this.nomForfait = nom;
		this.validite = valid;
	}
	
	public int getIdentifiant(){
		return this.idForfait;
	}
	
	public String getNomForfait(){
		return this.nomForfait;
	}
	
	public int getValidite(){
		return this.validite;
	}
	
	public int getPrix(){
		return this.prix;
	}
}
