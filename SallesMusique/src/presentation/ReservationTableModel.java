package presentation;

import java.sql.SQLException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import metier.ServiceReservation;
import donnees.Reservation;
import donnees.Salle;

public class ReservationTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] colonne;
	private Object[][] data;
	
	public ReservationTableModel(List<Reservation> desReservations) throws SQLException{
		super();
		this.colonne = new String[1];
		this.colonne[0] = "Salle "+desReservations.get(0).getSalle().getIdentifiant();
		this.data = new Object[15][1];
		this.initData(desReservations,desReservations.get(0).getDateReservation(),desReservations.get(0).getSalle());
	}
	
	public void initData(List<Reservation> desReservations, java.sql.Date laDate, Salle laSalle) throws SQLException{
		int[] lesCreneaux = ServiceReservation.getInstance().construireTableauDuJour(laDate, laSalle);
		for(int i = 0;i<15;++i){//Pour chaque creneau de la journée, on va creer une ligne : 
			if(lesCreneaux[i] == -1){//Ce creneau est vide
				data[i][0] = lesCreneaux[i];
			}else{
				data[i][0] = ServiceReservation.getInstance().rechercherUneReservation(lesCreneaux[i]);
			}
		}
	}
	
	public int getColumnCount() {
        return this.colonne.length;
    }

    public int getRowCount() {
        return this.data.length;
    }

    public String getColumnName(int col) {
        return this.colonne[col];
    }

    public Object getValueAt(int row, int col) {
    	if(this.data[row][col].equals(-1)){
    		return (Reservation)null;
    	}else{
    		return ((Reservation)(this.data[row][col]));
    	}
    }

    public Class getColumnClass(int c) {
    	return Object.class;
    }

    public boolean isCellEditable(int row, int col) {
    	return false;
    }
}
