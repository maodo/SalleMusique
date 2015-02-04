package donnees;

import java.sql.Date;

public class ForfaitClient {

	private int idForfait;
	private int idClient;
	private int credit;
	private Date dateAchat;
	private int idTypeSalle;
	private int prix;
	
	public ForfaitClient(int idf, int idc, int cred, Date dateA, int idtypesalle, int prix){
		this.idForfait = idf;
		this.idClient =idc;
		this.credit =cred;
		this.dateAchat = dateA;
		this.idTypeSalle= idtypesalle;
		this.prix = prix;
	}
	 
	public int getIdForfait() {
		return idForfait;
	}
	
	public int getIdClient() {
		return idClient;
	}
	
	public int getCredit() {
		return credit;
	}
	
	public void setCredit(int credit) {
		this.credit = credit;
	}
	
	public Date getDateAchat() {
		return dateAchat;
	}
	
	public int getIdTypeSalle(){
		return this.idTypeSalle;
	}
	
	public int getprix(){
		return this.prix;
	}
}