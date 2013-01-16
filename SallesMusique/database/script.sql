CREATE TABLE TypeSalle (
	idTypeSalle Integer,
	libelle varchar(25)
);
ALTER TABLE TypeSalle ADD constraint typeSalle_pkey PRIMARY KEY(idTypeSalle);

CREATE TABLE Salle (
	idSalle Integer,
	typeSalle Integer
);
ALTER TABLE Salle ADD constraint salle_pkey PRIMARY KEY(idSalle);
ALTER TABLE Salle ADD constraint salle_fkey_typeSalle FOREIGN KEY(typeSalle) REFERENCES TypeSalle(idTypeSalle);
 
CREATE TABLE Forfait (
	idForfait Integer,
	nomForfait varchar(25),
	validite Integer 
);
ALTER TABLE Forfait ADD constraint forfait_pkey PRIMARY KEY(idForfait);

CREATE TABLE TarifForfait (
	idTypeSalle Integer,
	idForfait Integer,
	prix Integer
);
ALTER TABLE TarifForfait ADD constraint tarifForfait_pkey PRIMARY KEY(idTypeSalle,idForfait);
ALTER TABLE TarifForfait ADD constraint tarifForfait_fkey_typeSalle FOREIGN KEY(idTypeSalle) REFERENCES TypeSalle(idTypeSalle);
ALTER TABLE TarifForfait ADD constraint tarifForfait_fkey_forfait FOREIGN KEY(idForfait) REFERENCES Forfait(idForfait);

CREATE TABLE Client (
	idClient Integer,
	nomClient varchar(50),
	prenomClient varchar(50),
	nbPointsFid Integer,
	numTel varchar(10)
);
ALTER TABLE Client ADD constraint client_pkey PRIMARY KEY(idClient);

CREATE TABLE Plage (
	idPlage Integer,
	duree Integer
);
ALTER TABLE Plage ADD constraint plage_fkey PRIMARY KEY(idPlage);

CREATE TABLE TarifPonctuel (
	idPlage Integer,
	idTypeSalle Integer,
	prix Integer
);
ALTER TABLE TarifPonctuel ADD constraint tarifPonctuel_pkey PRIMARY KEY(idPlage,idTypeSalle);
ALTER TABLE TarifPonctuel ADD constraint tafifPonctuel_fkey_plage FOREIGN KEY(idPlage) REFERENCES Plage(idPlage); 
ALTER TABLE TarifPonctuel ADD constraint tafifPonctuel_fkey_typeSalle FOREIGN KEY(idTypeSalle) REFERENCES TypeSalle(idTypeSalle);

CREATE TABLE TrancheHoraire (
	idTranche Integer,
	libelle varchar(25)
);
ALTER TABLE TrancheHoraire ADD constraint trancheHoraire_pkey PRIMARY KEY(idTranche);

CREATE TABLE Reservation (
	idReservation Integer,
	duree Integer,
	montant Integer,
	dateReservation Date,
	dateCommande Date,
	dateConfirmation Date,
	idTypeSalle Integer,
	idClient Integer,
	idTranche Integer
);
ALTER TABLE Reservation ADD constraint reservation_pkey PRIMARY KEY(idReservation);
ALTER TABLE Reservation ADD constraint reservation_fkey_typeSalle FOREIGN KEY(idTypeSalle) REFERENCES TypeSalle(idTypeSalle);
ALTER TABLE Reservation ADD constraint reservation_fkey_client FOREIGN KEY(idClient) REFERENCES Client(idClient);
ALTER TABLE Reservation ADD constraint reservation_fkey_tranche FOREIGN KEY(idTranche) REFERENCES TrancheHoraire(idTranche);

CREATE TABLE Majoration (
	idMajoration Integer,
	heure Integer,
	pourcentage float
);
ALTER TABLE Majoration ADD constraint majoration_pkey PRIMARY KEY(idMajoration);

INSERT INTO Client VALUES(1,'DesNeiges','AbominableHomme',0,'0836656565');
shutdown();
