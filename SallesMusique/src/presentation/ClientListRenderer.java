package presentation;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import donnees.Client;

public class ClientListRenderer extends JLabel implements ListCellRenderer{
	  public ClientListRenderer() {}

	  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.setText(((Client)value).getPrenom()+" "+((Client)value).getNom());
	    if (isSelected) {
	    	setBackground(list.getSelectionBackground());
		    setForeground(list.getSelectionForeground());
		} else {
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
		}
	    return this;
	  }
}