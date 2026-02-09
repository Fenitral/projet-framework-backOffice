create database travel_agency;
\c travel_agency;

CREATE SCHEMA local;

create TABLE local.hotel (
    hotel_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    addresse TEXT NOT NULL,
    phone VARCHAR(15)
);


CREATE TABLE local.reservation_temp (
    reservation_id SERIAL PRIMARY KEY,
    client VARCHAR(50) NOT NULL,
    nb_people INT NOT NULL,
    dateheure TIMESTAMP NOT NULL,
    hotel_id INT REFERENCES local.hotel(hotel_id)
);

-- Données de test
delete from local.reservation_temp;
delete from local.hotel;

-- Insertion des hôtels
INSERT INTO local.hotel (name, addresse, phone) VALUES
('Colbert', 'Avenue de l''Indépendance, Antananarivo', '034 01 234 56'),
('Novotel', 'Route de l''Aéroport, Ivato', '034 02 345 67'),
('Ibis', 'Rue Rainandriamampandry, Antananarivo', '034 03 456 78'),
('Lokanga', 'Avenue du 26 Juin, Antananarivo', '034 04 567 89'); 

INSERT INTO local.reservation_temp (client, nb_people, dateheure, hotel_id) VALUES
('Jean Dupont', 2, '2026-02-10 14:00:00', 1);

-- ('Marie Martin', 4, '2026-02-15 10:00:00', 2),
-- ('Pierre Rakoto', 1, '2026-02-20 18:30:00', 3);
