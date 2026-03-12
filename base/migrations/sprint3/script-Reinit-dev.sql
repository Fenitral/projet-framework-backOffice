create database if not exists reservation_voiture;
\c reservation_voiture;

CREATE SCHEMA IF NOT EXISTS dev;

-- lister les schemas
\dn


CREATE TABLE IF NOT EXISTS dev.Hotel(
   Id_Hotel SERIAL,
   nom VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Hotel)
);
alter table dev.hotel add column aeroport VARCHAR(50); 

CREATE TABLE IF NOT EXISTS dev.reservation(

   Id_reservation SERIAL,
   DateHeureArrive TIMESTAMP,
   idClient VARCHAR(50),
   nbPassager INT,
   Id_Hotel INT NOT NULL,
   PRIMARY KEY(Id_reservation),
   FOREIGN KEY(Id_Hotel) REFERENCES dev.Hotel(Id_Hotel)
);
alter table dev.reservation add column client_id INT references dev.client;

CREATE TABLE IF NOT EXISTS dev.Vehicule (
    Id_Vehicule SERIAL,
   PRIMARY KEY(id)
    Reference VARCHAR(50) NOT NULL,
    nbPlace INT,
    TypeVehicule VARCHAR(50),
    PRIMARY KEY(Id_Vehicule)
);

CREATE TABLE IF NOT EXISTS dev.token_expiration(
   id SERIAL,
   token VARCHAR(255) NOT NULL UNIQUE,
   expiration TIMESTAMP NOT NULL,
);

-- sp3

CREATE TABLE dev.client (
    client_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15)
);

CREATE TABLE dev.regroupement (
    regroupement_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

create TABLE dev.assignation (
    assignation_id SERIAL PRIMARY KEY,
    client_id INT REFERENCES dev.client(client_id),
    regroupement_id INT REFERENCES dev.regroupement(regroupement_id),
    vehicule_id INT REFERENCES dev.Vehicule(Id_Vehicule),
    assigned_date TIMESTAMP NOT NULL
);
ALTER TABLE dev.assignation ADD COLUMN reservation_id INTEGER;


create table dev.unite ( 
    unite_id SERIAL PRIMARY KEY, 
    nom_unite VARCHAR(5unite0) NOT NULL 
);

create table dev.parametre ( 
    parametre_id SERIAL PRIMARY KEY, 
    nom_param VARCHAR(50) NOT NULL,
    valeur int, 
     unite_id INT REFERENCES dev.unite(unite_id) 
);


CREATE TABLE dev.distance (
    distance_id SERIAL PRIMARY KEY, 
    idHotelFrom INT REFERENCES dev.Hotel(Id_Hotel),
    idHotelTo INT REFERENCES dev.Hotel(Id_Hotel),
    valeur INT
);
