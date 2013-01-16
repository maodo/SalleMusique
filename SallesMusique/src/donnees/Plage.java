package donnees;

public class Plage {
	private int idPlage;
	private int duree;
	
	public Plage(int id,int duree){
		this.idPlage = id;
		this.duree = duree;
	}
	
	public int getIdentifiant(){
		return this.idPlage;
	}
	
	public int getDuree(){
		return this.duree;
	}
}
