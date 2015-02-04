package donnees;

public class TarifForfaitaire extends Tarif{
	private Forfait leForfait;
	
	public TarifForfaitaire(int prix,TypeSalle leTypeSalle,Forfait unForfait){
		super(prix,leTypeSalle);
		this.leForfait = unForfait;
	}
	
	public Forfait getForfait(){
		return this.leForfait;
	}
}

