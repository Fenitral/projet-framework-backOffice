BEGIN;

CREATE SCHEMA IF NOT EXISTS local;

CREATE TABLE IF NOT EXISTS local.hotel (
    id_hotel SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    aeroport VARCHAR(50),
    is_aeroport BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS local.client (
    client_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS local.vehicule (
    id_vehicule SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    nbplace INT,
    typevehicule VARCHAR(50),
    lieu_actuel VARCHAR(100),
    statut VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
);

CREATE TABLE IF NOT EXISTS local.token_expiration (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiration TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS local.regroupement (
    regroupement_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS local.unite (
    unite_id SERIAL PRIMARY KEY,
    nom_unite VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS local.parametre (
    parametre_id SERIAL PRIMARY KEY,
    nom_param VARCHAR(50) NOT NULL,
    valeur INT,
    unite_id INT REFERENCES local.unite(unite_id)
);

CREATE TABLE IF NOT EXISTS local.reservation (
    id_reservation SERIAL PRIMARY KEY,
    dateheurearrive TIMESTAMP,
    idclient VARCHAR(50),
    nbpassager INT,
    id_hotel INT NOT NULL REFERENCES local.hotel(id_hotel),
    client_id INT REFERENCES local.client(client_id)
);

CREATE TABLE IF NOT EXISTS local.assignation (
    assignation_id SERIAL PRIMARY KEY,
    reservation_id INT REFERENCES local.reservation(id_reservation),
    client_id INT REFERENCES local.client(client_id),
    regroupement_id INT REFERENCES local.regroupement(regroupement_id),
    vehicule_id INT REFERENCES local.vehicule(id_vehicule),
    assigned_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS local.distance (
    distance_id SERIAL PRIMARY KEY,
    idhotelfrom INT REFERENCES local.hotel(id_hotel),
    idhotelto INT REFERENCES local.hotel(id_hotel),
    valeur INT
);

-- Fenêtres de temps d'attente pour regrouper les clients
-- CREATE TABLE IF NOT EXISTS local.temps_attente_window (
--     window_id SERIAL PRIMARY KEY,
--     departure_date DATE NOT NULL,
--     window_start TIME NOT NULL,
--     window_end TIME NOT NULL,
--     minutes_attente INT NOT NULL DEFAULT 30
-- );

-- Planifications complètes (résultats d'assignation)
CREATE TABLE IF NOT EXISTS local.planification (
    planification_id SERIAL PRIMARY KEY,
    departure_date DATE NOT NULL,
    window_id INT REFERENCES local.temps_attente_window(window_id),
    heure_depart TIMESTAMP NOT NULL,
    heure_retour_aeroport TIMESTAMP NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Trajets véhicules dans une planification
CREATE TABLE IF NOT EXISTS local.trajet_vehicule (
    trajet_id SERIAL PRIMARY KEY,
    planification_id INT NOT NULL REFERENCES local.planification(planification_id),
    vehicule_id INT NOT NULL REFERENCES local.vehicule(id_vehicule),
    distance_totale INT,
    heure_depart_aeroport TIMESTAMP NOT NULL,
    heure_retour_aeroport TIMESTAMP NOT NULL,
    ordre_trajet INT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Assignations détaillées avec trajet
CREATE TABLE IF NOT EXISTS local.assignation_detaillee (
    assignation_detaillee_id SERIAL PRIMARY KEY,
    trajet_id INT REFERENCES local.trajet_vehicule(trajet_id),
    reservation_id INT REFERENCES local.reservation(id_reservation),
    ordre_visite INT,
    hotel_visite INT REFERENCES local.hotel(id_hotel)
);

COMMIT;
