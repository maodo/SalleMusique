package donnees;

public class TrancheHoraire {
	private int idTranche;
	private String libelle;
	
	public TrancheHoraire(int id,String lib){
		this.idTranche = id;
		this.libelle = lib;
	}
	
	public int getIdentifiant(){
		return this.idTranche;
	}
	
	public String getLibelle(){
		return this.libelle;
	}
}
