--TypeSalle
CREATE TABLE TypeSalle (
	idTypeSalle Integer,
	libelle varchar(25)
);
ALTER TABLE TypeSalle ADD constraint typeSalle_pkey PRIMARY KEY(idTypeSalle);
INSERT INTO TypeSalle VALUES(1,'Petite salle');
INSERT INTO TypeSalle VALUES(2,'Grande salle');
INSERT INTO TypeSalle VALUES(3,'Salle d''enregistrement');


--Salle
CREATE TABLE Salle (
	idSalle Integer,
	typeSalle Integer
);
ALTER TABLE Salle ADD constraint salle_pkey PRIMARY KEY(idSalle);
ALTER TABLE Salle ADD constraint salle_fkey_typeSalle FOREIGN KEY(typeSalle) REFERENCES TypeSalle(idTypeSalle);
INSERT INTO Salle VALUES(1,1);
INSERT INTO Salle VALUES(2,1);
INSERT INTO Salle VALUES(3,2);
INSERT INTO Salle VALUES(4,2);
INSERT INTO Salle VALUES(5,3);
INSERT INTO Salle VALUES(6,3);

 
--Forfait
CREATE TABLE Forfait (
	idForfait Integer,
	nomForfait varchar(25),
	validite Integer 
);
ALTER TABLE Forfait ADD constraint forfait_pkey PRIMARY KEY(idForfait);
INSERT INTO Forfait VALUES(1,'12h',3);
INSERT INTO Forfait VALUES(2,'24h',6);


--Client
CREATE TABLE Client (
	idClient Integer,
	nomClient varchar(50),
	prenomClient varchar(50),
	nbPointsFid Integer,
	numTel varchar(10)
);
ALTER TABLE Client ADD constraint client_pkey PRIMARY KEY(idClient);
INSERT INTO Client VALUES(1,'DesNeiges','AbominableHomme',0,'0836656565');


--Plage
CREATE TABLE Plage (
	idPlage Integer,
	duree Integer
);
ALTER TABLE Plage ADD constraint plage_fkey PRIMARY KEY(idPlage);
INSERT INTO Plage VALUES(1,1);
INSERT INTO Plage VALUES(2,2);


--TrancheHoraire
CREATE TABLE TrancheHoraire (
	idTranche Integer,
	libelle varchar(5)
);
ALTER TABLE TrancheHoraire ADD constraint trancheHoraire_pkey PRIMARY KEY(idTranche);
INSERT INTO TrancheHoraire VALUES(1,'9/13');
INSERT INTO TrancheHoraire VALUES(2,'13/20');
INSERT INTO TrancheHoraire VALUES(3,'20/24');


--TarifForfait
CREATE TABLE TarifForfait (
	idTypeSalle Integer,
	idForfait Integer,
	prix Integer
);
ALTER TABLE TarifForfait ADD constraint tarifForfait_pkey PRIMARY KEY(idTypeSalle,idForfait);
ALTER TABLE TarifForfait ADD constraint tarifForfait_fkey_typeSalle FOREIGN KEY(idTypeSalle) REFERENCES TypeSalle(idTypeSalle);
ALTER TABLE TarifForfait ADD constraint tarifForfait_fkey_forfait FOREIGN KEY(idForfait) REFERENCES Forfait(idForfait);
INSERT INTO TarifForfait VALUES(1,1,50);
INSERT INTO TarifForfait VALUES(1,2,100);
INSERT INTO TarifForfait VALUES(2,1,80);
INSERT INTO TarifForfait VALUES(2,2,150);


--TarifPonctuel
CREATE TABLE TarifPonctuel (
	idPlage Integer,
	idTypeSalle Integer,
	prix Integer
);
ALTER TABLE TarifPonctuel ADD constraint tarifPonctuel_pkey PRIMARY KEY(idPlage,idTypeSalle);
ALTER TABLE TarifPonctuel ADD constraint tafifPonctuel_fkey_plage FOREIGN KEY(idPlage) REFERENCES Plage(idPlage); 
ALTER TABLE TarifPonctuel ADD constraint tafifPonctuel_fkey_typeSalle FOREIGN KEY(idTypeSalle) REFERENCES TypeSalle(idTypeSalle);
INSERT INTO TarifPonctuel VALUES(1,1,7);
INSERT INTO TarifPonctuel VALUES(1,2,10);
INSERT INTO TarifPonctuel VALUES(1,3,20);
INSERT INTO TarifPonctuel VALUES(2,1,10);
INSERT INTO TarifPonctuel VALUES(2,2,16);
INSERT INTO TarifPonctuel VALUES(2,3,30);


--Reservation
CREATE TABLE Reservation (
	idReservation Integer,
	duree Integer,
	montant Integer,
	dateReservation Date,
	dateCommande Date,
	dateConfirmation Date,
	idSalle Integer,
	idClient Integer,
	heureDebut Integer
);
ALTER TABLE Reservation ADD constraint reservation_pkey PRIMARY KEY(idReservation);
ALTER TABLE Reservation ADD constraint reservation_fkey_client FOREIGN KEY(idClient) REFERENCES Client(idClient);
ALTER TABLE Reservation ADD constraint reservation_fkey_salle FOREIGN KEY(idSalle) REFERENCES Salle(idSalle);


--Majoration
CREATE TABLE Majoration (
	idMajoration Integer,
	heure Integer,
	pourcentage float
);
ALTER TABLE Majoration ADD constraint majoration_pkey PRIMARY KEY(idMajoration);
INSERT INTO Majoration VALUES(1,20,1.20);


shutdown();
