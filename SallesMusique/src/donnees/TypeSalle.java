package donnees;

public class TypeSalle {
	private int idTypeSalle;
	private String libelle;
	
	public TypeSalle(int id,String lib){
		this.idTypeSalle = id;
		this.libelle = lib;
	}
	
	public int getIdentifiant(){
		return this.idTypeSalle;
	}
	
	public String getLibelle(){
		return this.libelle;
	}
}
