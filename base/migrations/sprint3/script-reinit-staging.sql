CREATE SCHEMA IF NOT EXISTS staging;

CREATE TABLE IF NOT EXISTS staging.Hotel(
   Id_Hotel SERIAL,
   nom VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Hotel)
);
alter table staging.hotel add column aeroport VARCHAR(50); 

CREATE TABLE staging.client (
    client_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS staging.reservation(
   Id_reservation SERIAL,
   DateHeureArrive TIMESTAMP,
   idClient VARCHAR(50),
   nbPassager INT,
   Id_Hotel INT NOT NULL,
   PRIMARY KEY(Id_reservation),
   FOREIGN KEY(Id_Hotel) REFERENCES staging.Hotel(Id_Hotel)
);
alter table staging.reservation add column client_id INT references staging.client;

select * from dev.vehicule;
\d dev.vehicule;
CREATE TABLE IF NOT EXISTS staging.Vehicule (
    Id_Vehicule SERIAL,
    Reference VARCHAR(50) NOT NULL,
    nbPlace INT,
    TypeVehicule VARCHAR(50),
    PRIMARY KEY(Id_Vehicule)
);

CREATE TABLE IF NOT EXISTS staging.token_expiration(
   id SERIAL,
   token VARCHAR(255) NOT NULL UNIQUE,
   expiration TIMESTAMP NOT NULL
);

-- sp3

CREATE TABLE staging.regroupement (
    regroupement_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

create TABLE staging.assignation (
    assignation_id SERIAL PRIMARY KEY,
    client_id INT REFERENCES staging.client(client_id),
    regroupement_id INT REFERENCES staging.regroupement(regroupement_id),
    vehicule_id INT REFERENCES staging.Vehicule(Id_Vehicule),
    assigned_date TIMESTAMP NOT NULL
);
ALTER TABLE staging.assignation ADD COLUMN reservation_id INTEGER;


create table staging.unite ( 
    unite_id SERIAL PRIMARY KEY, 
    nom_unite VARCHAR(50) NOT NULL 
);

create table staging.parametre ( 
    parametre_id SERIAL PRIMARY KEY, 
    nom_param VARCHAR(50) NOT NULL,
    valeur int, 
     unite_id INT REFERENCES staging.unite(unite_id) 
);


CREATE TABLE staging.distance (
    distance_id SERIAL PRIMARY KEY, 
    idHotelFrom INT REFERENCES staging.Hotel(Id_Hotel),
    idHotelTo INT REFERENCES staging.Hotel(Id_Hotel),
    valeur INT
);

-- verify the tables
 \dt staging.*  
\d staging.Hotel;...
select * from staging.Hotel;
select * from staging.client;