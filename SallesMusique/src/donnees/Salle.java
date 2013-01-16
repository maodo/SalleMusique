package donnees;

public class Salle {
	private int idSalle;
	private TypeSalle leTypeSalle;
	
	public Salle(int id,TypeSalle leType){
		this.idSalle = id;
		this.leTypeSalle = leType;
	}
	
	public int getIdentifiant(){
		return this.idSalle;
	}
	
	public TypeSalle getTypeSalle(){
		return this.leTypeSalle;
	}
}
