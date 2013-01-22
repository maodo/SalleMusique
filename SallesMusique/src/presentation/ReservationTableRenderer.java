package presentation;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import metier.ServiceReservation;

import donnees.Reservation;

public class ReservationTableRenderer implements TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		
		 JTextField editor = new JTextField();
		 if(value == null){
			 editor.setText(" ");
			 editor.setBackground(Color.white);
			 editor.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.black));
			 if(row == 0){
				 editor.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.black));
			 }
			 if(row == table.getRowCount()-1){
				 editor.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.black));
			 }
		 }else{
			 if(row == 0){
				 editor.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.black));
			 }
			 if(row == table.getRowCount()-1){
				 editor.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.black));
			 }
			 Reservation r = (Reservation)value;
			 
			 if(ServiceReservation.getInstance().reservationEstConfirmee(r))
				 editor.setBackground(Color.green);
			 else
				 editor.setBackground(Color.cyan);
			 editor.setBorder(BorderFactory.createMatteBorder(0,1,0,1,Color.black));
			 editor.setHorizontalAlignment(JTextField.CENTER);
			 
			 if(this.estCreneauUnique(r,row)){
				 editor.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
				 editor.setText(r.getClient().getNom());
			 }else if(this.estPremierCreneau(r,row)){
				 editor.setBorder(BorderFactory.createMatteBorder(1,1,0,1,Color.black));
				 editor.setText(r.getClient().getNom());
			 }else if(this.estSecondCreneau(r,row)){
				 editor.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.black));
			 }
		 }
	
		return editor;
	}

	private boolean estSecondCreneau(Reservation r, int row) {
		return (r.getHeureDebut() == row-1+9);
	}

	private boolean estPremierCreneau(Reservation r, int row) {
		return (r.getHeureDebut() == row+9);
	}

	private boolean estCreneauUnique(Reservation r, int row) {
		return (r.getDuree()==1);
	}
}
