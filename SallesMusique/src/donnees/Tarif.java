package donnees;

public class Tarif {
	private int idTarif;
	int prix;
	TypeSalle leTypeSalle;
	
	public Tarif(int id,int prix,TypeSalle typeSalle){
		this.idTarif = id;
		this.prix = prix;
		this.leTypeSalle = typeSalle;
	}
	
	public int getIdentifiant(){
		return this.idTarif;
	}
	
	public int getPrix(){
		return this.prix;
	}
	
	public TypeSalle getTypeSalle(){
		return this.leTypeSalle;
	}
}
