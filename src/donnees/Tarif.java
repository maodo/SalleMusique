package donnees;

public class Tarif {
	int prix;
	TypeSalle leTypeSalle;
	
	public Tarif(int prix,TypeSalle typeSalle){
		this.prix = prix;
		this.leTypeSalle = typeSalle;
	}
	
	
	public int getPrix(){
		return this.prix;
	}
	
	public TypeSalle getTypeSalle(){
		return this.leTypeSalle;
	}
}
