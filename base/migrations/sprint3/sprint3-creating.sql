CREATE TABLE dev.client (
    client_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15)
);

alter table dev.reservation add column client_id INT references dev.client;

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


create table dev.unite ( 
    unite_id SERIAL PRIMARY KEY, 
    nom_unite VARCHAR(50) NOT NULL 
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

-- ito mbola ts tafiditra
alter table dev.hotel add column aeroport VARCHAR(50); 


ALTER TABLE dev.assignation ADD COLUMN reservation_id INTEGER;
