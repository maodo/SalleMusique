package donnees;

public class Majoration {
	private int idMajoration;
	private int heure;
	private float pourcentage;
	
	public Majoration(int id,int heure,float pourcent){
		this.idMajoration = id;
		this.heure = heure;
		this.pourcentage = pourcent;
	}
	
	public int getIdentifiant(){
		return this.idMajoration;
	}
	
	public int getHeure(){
		return this.heure;
	}
	
	public float getPourcentage(){
		return this.pourcentage;
	}
}
