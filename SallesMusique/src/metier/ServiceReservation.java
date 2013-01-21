package metier;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import connecteur.Connexion;
import donnees.Client;
import donnees.Reservation;
import donnees.Salle;
import donnees.Tarif;
import donnees.TrancheHoraire;
import donnees.TypeSalle;
import fabriques.FabReservation;
import fabriques.FabSalle;

public class ServiceReservation {
	
	private static FabReservation fabReservation;
	private static ServiceReservation servReservation;
	private PreparedStatement statementCalculePrixPlage;
	
	private ServiceReservation(){
		fabReservation = FabReservation.getInstance();
	}
	
	public static ServiceReservation getInstance(){
		if(servReservation == null)
			return new ServiceReservation();
		else
			return servReservation;
	}
	
	/**
	 * Recherche une reservation par son identifiant
	 * @param idReservation L'identifiant de la reservation voulue
	 * @return un objet Reservation instancié; null si n'existe pas en base
	 */
	public Reservation rechercherUneReservation(int idReservation) throws SQLException{
		return fabReservation.rechercherReservation(idReservation);
	}
	
	/**
	 * Recherche automatiquement un creneau pour une date, une duree, un type de salle et une tranche horaire (matin/midi/soir)
	 * @param leClient Le client désirant reserver une salle
	 * @param laDate La date de la reservation
	 * @param duree La duree de la reservation 
	 * @param leTarif 
	 * @param leTypeSalle
	 * @param laTranche
	 * @return La reservation sous la forme d'un objet instancié; null si la reservation n'a pas pu se faire
	 * @throws SQLException
	 */
	public Reservation reserverSalleAutomatiquement(Client leClient, Date laDate, int duree, 
			Tarif leTarif, TypeSalle leTypeSalle, TrancheHoraire laTranche) throws SQLException {
		
		Reservation laReservation = null;
		List<Salle> sallesDispo = FabSalle.getInstance().listerSalleParType(leTypeSalle);
		boolean trouve = false;
		for(Salle laSalle : sallesDispo){
			int heureDebut = 9;
			if(!trouve){
				//On charge les reservation de ce jour là : 
				List<Reservation> lesReservations = fabReservation.rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
				if(lesReservations.isEmpty()){
					int leMontant = this.calculerMontant(duree,leTarif,leTypeSalle,laTranche);
					java.util.Date today = new java.util.Date();
					java.sql.Date dateReserv = laDate;
					java.sql.Date dateConfirm = new Date(today.getTime());
					java.sql.Date dateCommande = new Date(today.getTime());
					dateConfirm = this.ajouterJour(dateConfirm, 7);
					laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
					trouve = true;
				}else{
					//On construire une table de boolean, avec une case par heure. True = la salle est libre à cette heure là, 
					//False: la salle n'est pas libre à cette heure là
					boolean[] tableDuJour = this.construireTableauDuJourPourReservAuto(laDate, laSalle);
					//On parcours la table du jour pour chercher un creneau qui correspond aux critères : 
					int creneauConseq = 0;
					for(int i=0;i<15;++i){
						if(tableDuJour[i] == true){
							++creneauConseq;
							if(creneauConseq == duree && trouve == false){//On a assez de place pour caller ce creneau. Donc on reservce :
								java.util.Date today = new java.util.Date();
								int leMontant = 0;
								java.sql.Date dateReserv = laDate;
								java.sql.Date dateConfirm = new Date(today.getTime());
								java.sql.Date dateCommande = new Date(today.getTime());
								dateConfirm = this.ajouterJour(dateConfirm, 7);
								laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
								trouve = true;
							}
						}else{
							heureDebut = i+1;
						}
					}
				}
			}
		}

		return laReservation;
	}

	/**
	 * Permet de reserver manuellement une salle, sans faire de recherche automatique de creneaux
	 * @param laSalle La salle pour laquelle on veut reserver
	 * @param leClient Le client qui veut reserver
	 * @param laDate La date de la reservation 
	 * @param duree La duree de la reservation
	 * @return La reservation sous la forme d'un objet instancié si tout c'est bien passé; null sinon
	 */
	public Reservation reserverSalleManuellement(Salle laSalle, Client leClient, Date laDate, int heureDebut, int duree) throws SQLException{
		Reservation laReservation = null;
		//On regarde s'il y a déjà une reservation pour cette salle ce jour
		List<Reservation> lesReservations = fabReservation.rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		if(lesReservations.isEmpty()){
			//Aucune reservation ce jour là: on peut creer la reservation
			int leMontant = 0;
			java.util.Date today = new java.util.Date();
			java.sql.Date dateReserv = laDate;
			java.sql.Date dateConfirm = new Date(today.getTime());
			java.sql.Date dateCommande = new Date(today.getTime());
			dateConfirm = this.ajouterJour(dateConfirm, 7);
			laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
		}else{
			//Il y a des reservations ce jour là: on les parcours
			int[] tableDuJour = this.construireTableauDuJourPourReservManuel(laDate, laSalle);
			int creneauConseq = 0;
			int indiceHeureFin = heureDebut-9+duree;
			//On va regarder dans le tableau de boolean les indices correspondant aux creneaux qu'on souhaite reserver
			//Si toutes les cases qu'on parcour sont à true, on peut reserver
			//Sinon, on ne reserve pas
			int idAncienCreneau = -1;
			for(int i=heureDebut-9;i<indiceHeureFin;++i){
				if(tableDuJour[i] != -2){//Si le creneau n'est pas indisponible
					if(tableDuJour[i] != -1){
						idAncienCreneau = tableDuJour[i];
					}
					++creneauConseq;
					if(creneauConseq == duree){//On a assez de place pour caller ce creneau. Donc on reservce :
						if(tableDuJour[i] != -1){//On empiete sur une reservation non confirmée : on la supprime avant d'insérer la notre
							Reservation ancienneReservation = fabReservation.rechercherReservation(idAncienCreneau);
							this.annulerReservation(ancienneReservation);
						}
						java.util.Date today = new java.util.Date();
						int leMontant = 0;
						java.sql.Date dateReserv = laDate;
						java.sql.Date dateConfirm = new Date(today.getTime());
						java.sql.Date dateCommande = new Date(today.getTime());
						dateConfirm = this.ajouterJour(dateConfirm, 7);
						laReservation = fabReservation.creerReservation(duree, leMontant, dateReserv, dateCommande, dateConfirm, laSalle, leClient, heureDebut);
					}
				}else{
					heureDebut = i+1;
				}
			}
		}
		return laReservation;
	}
	
	public void reservationHebdomadaire(Salle laSalle, Client leClient, Date leCreneau){
		
	}
	
	/**
	 * Permet d'annuler (à savoir supprimer de la base) la reservation passée en paramètres
	 * @param laReservation La reservation a annuler
	 * @return True si la confirmation a bien été supprimée, false sinon
	 */
	public boolean annulerReservation(Reservation laReservation) throws SQLException{
		return fabReservation.supprimerReservation(laReservation);
	}
	
	private int calculerMontant(int duree, Tarif leTarif, TypeSalle leTypeSalle, TrancheHoraire laTranche) {
		return 0;
	}
	
	public double calculerMontantReservation(Reservation r, int duree) throws SQLException, Exception{
		double montant =0.00;
		//verification si la reservation commence apres 20h
		boolean majoration = false;
		if(r.getHeureDebut() >= 20){
			majoration = true;
		}
		//recuperation du type salle
		TypeSalle letypeSalle = r.getSalle().getTypeSalle();
		//calcule du nb de plages de 1 et 2 heures
		int nbplagesDeuxHeures = duree/2;
		int nbplagesUneHeure = duree%2;
		System.out.println("[calculerMontantReservation] nbplagesDeuxHeures : " +nbplagesDeuxHeures);
		System.out.println("[calculerMontantReservation] nbplagesUneHeure : " +nbplagesUneHeure);
		//on calcule le prix pour une plage de deux heures pour ce type de salle
		int prix = 0;
		if(nbplagesDeuxHeures >0){
			System.out.println("[calculerMontantReservation] recuperation prix pour plage 2 et idtypesalle : " +letypeSalle.getIdentifiant());

			//System.out.println("[calculerMontantReservation]requete : "+requete+"");
			this.statementCalculePrixPlage = Connexion.getInstance().prepareStatement("SELECT prix from tarifponctuel where idplage=2 and idtypesalle="+letypeSalle.getIdentifiant()+";");
			ResultSet rs = this.statementCalculePrixPlage.executeQuery();
			if(rs.next()){
				prix = rs.getInt(1);
				montant = montant + (nbplagesDeuxHeures*prix);
				System.out.println("[calculerMontantReservation]prix par plagesDeuxHeures : " +prix);
				System.out.println("[calculerMontantReservation]montant total: " +montant);	
			}
		}
		//de meme pour l'eventuelle plage d'une heure
		if(nbplagesUneHeure >0){
			this.statementCalculePrixPlage = Connexion.getInstance().prepareStatement("SELECT prix from tarifponctuel where idplage="+1+" and idtypesalle="+letypeSalle.getIdentifiant()+";");
			ResultSet rs = this.statementCalculePrixPlage.executeQuery();
			if(rs.next())
			{
			prix = rs.getInt(1);
			montant = montant + (nbplagesUneHeure*prix);
			System.out.println("[calculerMontantReservation]prix par plages Une Heure : " +prix);
			System.out.println("[calculerMontantReservation]montant total  : " +montant);
			}
		}
		//ajout de l'eventuellemajoration si le debut de la reservation >= 20 heures
		if(majoration){
			montant = montant * 1.02;
		}
		return montant;
	}
	
	/**
	 * Permet de construire un tableau de boolean representant la disponibilité d'une salle pour une journée donnée.
	 * Le tableau contient 15 cases (une par creneau de 1h); la case est à true si la creneau est dispo, false sisnon
	 * @param laDate La date pour laquelle on veut voir les reservations
	 * @param laSalle La salle pour laquelle on veut voir les reservations
	 * @return Le tableau de boolean décrit ci dessus
	 */
	private boolean[] construireTableauDuJourPourReservAuto(Date laDate, Salle laSalle) throws SQLException{
		List<Reservation> lesReservations = fabReservation.rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		boolean[] tableDuJour = new boolean[15];//15 car la location loue pour 14h de la journée, 9h->0h
		for(int i=0;i<tableDuJour.length;++i){
			tableDuJour[i] = true;
		}
		for(Reservation r : lesReservations){
			int debutR = r.getHeureDebut();
			int dureeR = r.getDuree();
			int indiceHeureFin = (debutR+dureeR)-9;
			for(int j=debutR-9;j<indiceHeureFin;++j){
				tableDuJour[j] = false;
			}
		}
		return tableDuJour;
	}
	
	/**
	 * Permet de construire un tableau d'entier representant la disponibilité d'une salle pour une journée donnée.
	 * Le tableau contient 15 cases (une par creneau de 1h); la case est à -1 si la creneau est dispo, -2 si pas dispo,
	 * ou l'id de la reservation si le creneau est utilisé par une reservation non confirmée
	 * A la difference de l'autres méthodes, celle ci considère un créneau dispo pour les reservations non confirmées
	 * @param laDate La date pour laquelle on veut voir les reservations
	 * @param laSalle La salle pour laquelle on veut voir les reservations
	 * @return Le tableau de boolean décrit ci dessus
	 */
	private int[] construireTableauDuJourPourReservManuel(Date laDate, Salle laSalle) throws SQLException{
		List<Reservation> lesReservations = fabReservation.rechercherReservationDunJourEtDuneSalle(laDate, laSalle);
		int[] tableDuJour = new int[15];//15 car la location loue pour 14h de la journée, 9h->0h
		for(int i=0;i<tableDuJour.length;++i){
			tableDuJour[i] = -1;
		}
		for(Reservation r : lesReservations){
			int debutR = r.getHeureDebut();
			int dureeR = r.getDuree();
			int indiceHeureFin = (debutR+dureeR)-9;
			for(int j=debutR-9;j<indiceHeureFin;++j){
				if(this.reservationEstConfirmee(r)){//On ne considère ce creneau comme occupé que si la reservation a été confirmée
					tableDuJour[j] = -2;
				}else{
					tableDuJour[j] = r.getIdentifiant();
				}
			}
		}
		return tableDuJour;
	}
	
	
	/**
	 * Permet de confirmer une reservation. La dateConfirmation devient alors la dateCommande
	 * @param client
	 * @param laReservation
	 * @return True si la confirmation s'est bien passée, false sinon
	 */
	public boolean confirmerUneServation(Client client, Reservation laReservation) throws SQLException, Exception{
		
		java.sql.Date date = laReservation.getDateCommande();
		PreparedStatement statementConfirmerReservation;
		statementConfirmerReservation = Connexion.getInstance().prepareStatement("UPDATE Reservation SET dateConfirmation = ? WHERE idReservation = ?");
		statementConfirmerReservation.clearParameters();
		statementConfirmerReservation.setDate(1,date);
		statementConfirmerReservation.setInt(2,laReservation.getIdentifiant());
		
		return statementConfirmerReservation.execute();
	}
	
	/**
	 * Permet de savoir si une reservation a oui ou non été confirmée
	 * @param r La reservation
	 * @return Vrai si la reservation est confirmée; Faux sinon
	 */
	public boolean reservationEstConfirmee(Reservation r) {
		return r.getDateConfirmation().equals(r.getDateCommande());
	}
	
	public java.sql.Date ajouterJour(java.sql.Date date, int nbJour) {
		java.sql.Date sqlDate;
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(date.getTime());
		cal.add(GregorianCalendar.DATE, nbJour);
	    sqlDate = new java.sql.Date(cal.getTime().getTime());
		return sqlDate;
	}
}
