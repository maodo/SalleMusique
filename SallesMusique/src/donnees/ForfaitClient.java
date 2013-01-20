package donnees;

import java.util.Date;

public class ForfaitClient {

	private int idForfait;
	private int idClient;
	private int credit;
	private Date dateAchat;
	
	public ForfaitClient(int idf, int idc, int cred, Date dateA)
	{
		this.idForfait = idf;
		this.idClient =idc;
		this.credit =cred;
		this.dateAchat = dateA;
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
}
