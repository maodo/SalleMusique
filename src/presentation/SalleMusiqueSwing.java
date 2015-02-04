package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableModel;

import connecteur.Connexion;

import metier.ServiceClient;
import metier.ServiceReservation;
import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabClient;
import fabriques.FabSalle;
import fabriques.FabTranche;
import fabriques.FabTypeSalle;

public class SalleMusiqueSwing extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private List<JPanel> tabReservations;
	private static ServiceReservation servReservation = ServiceReservation.getInstance();
	
	private JButton btnCreerRes;
	private JButton btnAnnulerRes;
	private JButton btnVoirRes;
	private JPanel pBouton;
	private com.toedter.calendar.JCalendar datePicker;
	private JPanel pTableau;

	public SalleMusiqueSwing(){
		super();
		
		try {
			this.initInterfaceSwing(new Date(Calendar.getInstance().getTime().getTime()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initInterfaceSwing(Date laDate) throws SQLException{
		
		this.setTitle("Planning de reservations des salles");
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		
		JPanel pDatePicker = new JPanel(new BorderLayout());
		datePicker = new com.toedter.calendar.JCalendar(); 
		pDatePicker.add(new JLabel("<html><h3> Date : </h3></html>"),BorderLayout.NORTH);
		pDatePicker.add(datePicker,BorderLayout.CENTER);
		btnCreerRes = new JButton("Creer reservation");
		btnAnnulerRes = new JButton("Annuler reservation");
		btnVoirRes = new JButton("Voir reservation");
		btnCreerRes.addActionListener(this);
		btnVoirRes.addActionListener(this);
		btnCreerRes.addActionListener(this);
		JPanel pBouton = new JPanel(new BorderLayout());
		pBouton.add(btnCreerRes,BorderLayout.NORTH);
		pBouton.add(btnVoirRes,BorderLayout.CENTER);
		pBouton.add(btnAnnulerRes,BorderLayout.SOUTH);
		pDatePicker.add(pBouton,BorderLayout.SOUTH);
		
		JPanel pClient = new JPanel(new BorderLayout());
		pClient.add(new JLabel("<html><h3> Client : </h3></html>"),BorderLayout.NORTH);
		JList lesClients = new JList(FabClient.getInstance().listerClient().toArray());
		lesClients.setCellRenderer(new ClientListRenderer());
		JScrollPane jspClients = new JScrollPane(lesClients);
		pClient.add(jspClients,BorderLayout.CENTER);
		JPanel pBoutonClient = new JPanel(new BorderLayout());
		JButton btnCreerClient = new JButton("Creer client");
		JButton btnVendreForfait = new JButton("Vendre forfait");
		pBoutonClient.add(btnCreerClient,BorderLayout.NORTH);
		pBoutonClient.add(btnVendreForfait,BorderLayout.SOUTH);
		pClient.add(pBoutonClient,BorderLayout.SOUTH);
		
		JPanel pMenu = new JPanel(new BorderLayout());
		pMenu.add(pDatePicker,BorderLayout.NORTH);
		pMenu.add(pClient,BorderLayout.SOUTH);
		this.add(pMenu,BorderLayout.WEST);
		
		pTableau = initTableau(laDate);
		this.add(pTableau,BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.repaint();
	}

	private JPanel initTableau(Date laDate) throws SQLException{
		List<Reservation> lesReservations = servReservation.listerLesReservationsDunJour(laDate);
		this.tabReservations = this.construireTableau(lesReservations,laDate);
		
		JPanel pTableau = new JPanel(new BorderLayout());
		pTableau.add(new JLabel("<html><h3>"+laDate.toString()+"</h3></html>",JLabel.CENTER),BorderLayout.NORTH);
		JPanel pContent = new JPanel();
		for(JPanel p : this.tabReservations){
			pContent.add(p);
		}
		pTableau.add(pContent,BorderLayout.CENTER);
		return pTableau;
	}

	private List<JPanel> construireTableau(List<Reservation> lesReservations, Date laDate) throws SQLException{

		List<JPanel> listeOccupationSalles = new ArrayList<JPanel>();
		List<TypeSalle> mesTypeSalles = FabTypeSalle.getInstance().listerTypeSalle(); 
		
		for(TypeSalle leTypeSalle : mesTypeSalles){
			List<Salle> mesSalles = FabSalle.getInstance().listerSalleParType(leTypeSalle);
			
			for(Salle laSalle : mesSalles){
				JPanel panelDeLaSalle = new JPanel(new BorderLayout());
				JTable resPourUneSalleUnJour = construireTableauUneJourUneSalle(laSalle,servReservation.listerLesReservationsDunJourEtDuneSalle(laDate,laSalle)) ;
				resPourUneSalleUnJour.setBorder(BorderFactory.createEmptyBorder());
				resPourUneSalleUnJour.setRowHeight(30);
				resPourUneSalleUnJour.setRowMargin(0);
				
				JLabel labelSalle = new JLabel("<html><h5>Salle "+laSalle.getIdentifiant()+"</h5></html>");
				labelSalle.setOpaque(true);
				labelSalle.setBackground(this.getColorLabelSalle(laSalle));
				labelSalle.setHorizontalAlignment(JLabel.CENTER);
				labelSalle.setBorder(BorderFactory.createLineBorder(Color.gray));
				
				panelDeLaSalle.add(labelSalle,BorderLayout.NORTH);
				panelDeLaSalle.add(resPourUneSalleUnJour,BorderLayout.CENTER);
				listeOccupationSalles.add(panelDeLaSalle);
			}
		}
		return listeOccupationSalles;
	}

	private JTable construireTableauUneJourUneSalle(Salle laSalle, List<Reservation> lesReservations) throws SQLException{
		JTable laTable;
		if(lesReservations.size() > 0){
			TableModel mTable = new ReservationTableModel(lesReservations);
			laTable = new JTable(mTable);
		}else{
			laTable = this.construireTableauVideUnJourUneSalle(laSalle);
		}
		laTable.setDefaultRenderer(Object.class, new ReservationTableRenderer());
		return laTable;
	}
	
	public Color getColorLabelSalle(Salle laSalle){
		Color color = Color.white;
		switch(laSalle.getTypeSalle().getIdentifiant()){
		case 1: 
			color = new Color(224,205,79);
			break;
		case 2:
			color = new Color(224,79,130);
			break;
		case 3:
			color = new Color(161,224,79);
			break;
		}
		return color;
	}
	
	public JTable construireTableauVideUnJourUneSalle(Salle laSalle){
		String colonne[] = {"Salle "+laSalle.getIdentifiant()};
		Object data[][] = new Object[15][1];
		for(int i = 0;i<15;++i){
			data[i][0] = null;
		}
		return new JTable(data,colonne);
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource().equals(btnVoirRes)){
			java.sql.Date laDate = new java.sql.Date(datePicker.getDate().getTime());
			this.remove(pTableau);
			try {
				pTableau = this.initTableau(laDate);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			this.add(pTableau,BorderLayout.CENTER);
			this.repaint();
			this.pack();
			this.setVisible(true);
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception{
		
		int duree = 2;
		GregorianCalendar leCalendrier = new GregorianCalendar(2013,0,22);
		Date dateReservation = new Date(leCalendrier.getTimeInMillis());
		TypeSalle leTypeSalle = FabTypeSalle.getInstance().rechercherTypeSalle(1);//Grande salle
		TrancheHoraire laTranche = FabTranche.getInstance().rechercherTranche(1);//le matin
		Client leClient = FabClient.getInstance().rechercherClient(3);
		
		//Reservation r = servReservation.reserverSalleAutomatiquement(leClient,dateReservation,duree,leTypeSalle,laTranche,false);
		servReservation.afficherReservation();
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {}
	    catch (ClassNotFoundException e) {
	    	e.printStackTrace();
	    }
	    catch (InstantiationException e) {
	    	e.printStackTrace();
	    }
	    catch (IllegalAccessException e) {
	    	e.printStackTrace();
	    }	
		SalleMusiqueSwing s = new SalleMusiqueSwing();
		s.setVisible(true);
	}
}
