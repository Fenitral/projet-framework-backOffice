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

-- Suppression et réinitialisation des compteurs ID
TRUNCATE TABLE local.reservation_temp RESTART IDENTITY CASCADE;
TRUNCATE TABLE local.hotel RESTART IDENTITY CASCADE;

delete from local.reservation_temp;
delete from local.hotel;

-- Insertion des hôtels
INSERT INTO local.hotel (name, addresse, phone) VALUES
('Colbert', 'Avenue de l''Indépendance, Antananarivo', '034 01 234 56'),
('Novotel', 'Route de l''Aéroport, Ivato', '034 02 345 67'),
('Ibis', 'Rue Rainandriamampandry, Antananarivo', '034 03 456 78'),
('Lokanga', 'Avenue du 26 Juin, Antananarivo', '034 04 567 89'); 

insert into local.reservation_temp (client, nb_people, dateheure, hotel_id) values
('1', 11, '2026-02-05 00:01:00', 3),
('2', 1, '2026-02-05 16:23:55', 3),
('3', 2, '2026-02-09 10:17:00', 1),
('4', 4, '2026-02-01 15:25:00', 2),
('5', 4, '2026-01-28 07:11:00', 1),
('6', 5, '2026-01-28 07:45:00', 1),
('7', 13, '2026-02-28 08:25:00', 2),
('8', 8, '2026-02-28 13:00:00', 2),
('9', 7, '2026-02-15 13:00:00', 1),
('10', 1, '2026-02-18 22:55:00', 4);
--------------------------------------
data-test sp7

update local.distance set valeur=90 where distance_id=1;
update local.distance set valeur=35 where distance_id=2;
update local.distance set valeur=60 where distance_id=3;

update local.parametre set valeur=30 where parametre_id=4;

update local.vehicule set nbplace=5 where id_vehicule=1;
update local.vehicule set nbplace=5 where id_vehicule=2;

insert into local.vehicule( reference ,nbplace ,typevehicule ,heure_disponibilite)values
('VH-001',5,'diesel','00:00:00'),
('VH-003',5,'essence','00:00:00'),
('VH-003',12,'diesel','00:00:00');
insert into local.vehicule( reference ,nbplace ,typevehicule ,heure_disponibilite)values
('VH-004',9,'diesel','00:00:00'),
('VH-005',12,'essence','13:00:00');

truncate local.vehicule restart identity cascade; 
---------------------------------------
data-test sp8

insert into local.vehicule( reference ,nbplace ,typevehicule ,heure_disponibilite)values
('VH-006',20,'essence','00:00:00');

