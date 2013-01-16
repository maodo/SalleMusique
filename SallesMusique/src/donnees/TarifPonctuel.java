package donnees;

public class TarifPonctuel extends Tarif{
	private Plage laPlageHoraire;
	
	public TarifPonctuel(int id,int prix,TypeSalle leTypeSalle,Plage unePlage){
		 super(id,prix,leTypeSalle);
		 this.laPlageHoraire = unePlage;
	}
	
	public Plage getPlageHoraire(){
		return this.laPlageHoraire;
	}
}
