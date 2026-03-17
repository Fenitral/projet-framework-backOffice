3245-3328-3368
TRUNCATE TABLE staging.distance RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.parametre RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.unite RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.assignation RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.reservation RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.regroupement RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.client RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.Vehicule RESTART IDENTITY CASCADE;
TRUNCATE TABLE staging.Hotel RESTART IDENTITY CASCADE;

INSERT INTO staging.Hotel (nom) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga'),
('Radisson Blu'),
('Analamanga');

-- Tokens de test pour le schéma dev
-- Ces tokens expirent dans 24h à partir de l'insertion
INSERT INTO staging.token_expiration(token, expiration)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', NOW() + INTERVAL '24 hours'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', NOW() + INTERVAL '48 hours');


INSERT INTO dev.Vehicule (Reference, nbPlace, TypeVehicule) VALUES
	('Vehicule1', 12, 'D'),
	('Vehicule2', 5, 'E'),
	('Vehicule3', 5, 'D'),
	('Vehicule5', 12, 'E');

INSERT INTO dev.client (name,email,phone) VALUES('Tina','tina@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Fenitra','fenitra@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Ange','ange@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('A','a@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('B','b@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('C','c@gmail.com','0342568678');

INSERT INTO dev.reservation (dateheurearrive, idclient, nbpassager, id_hotel, client_id)
VALUES
    ('2026-03-12 09:00:00', 'CLI-001', 7, 1, 1),
    ('2026-03-12 09:00:00', 'CLI-002', 11, 1, 2),
    ('2026-03-12 09:00:00', 'CLI-003', 3, 1, 7),
    ('2026-03-12 09:00:00', 'CLI-004', 1, 1, 4),
    ('2026-03-12 09:00:00', 'CLI-005', 2, 1, 5),
    ('2026-03-12 09:00:00', 'CLI-006', 20, 1, 6);


    insert into staging.reservation (client_id, hotel_id, date_reservation) values (1, 1, '2024-07-01');

-- Insertion des unités
INSERT INTO staging.unite (nom_unite) VALUES 
    ('km/h'),
    ('minutes'),
    ('km');

-- Insertion des paramètres
INSERT INTO staging.parametre (nom_param, valeur, unite_id) VALUES 
    ('vitesse_moyenne', 50, 1);      -- 50 km/h

INSERT INTO dev.distance (idhotelfrom, idhotelto, valeur) VALUES
(NULL, 1, 50); -- Aéroport -> Colbert (12 km)
(NULL, 2, 8),    -- Aéroport -> Novotel (8 km)
(NULL, 3, 15),   -- Aéroport -> Ibis (15 km)
(NULL, 4, 20);   -- Aéroport -> Lokanga (20 km)

INSERT INTO staging.distance (idhotelfrom, idhotelto, valeur) VALUES
(NULL, 5, 8),   -- Aéroport -> Radisson Blu (8 km)
(NULL, 6, 8);   -- Aéroport -> Analamanga (8 km)

INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 2, 3);  -- Analamanga -> Betina
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 3, 2);  -- Betina -> Novotel
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 4, 4);  -- Analamanga -> Novotel
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 5, 4);  -- Analamanga -> Novotel
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 6, 6);  -- Analamanga -> Novotel

INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 3, 3);  -- Betina -> Analamanga
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 4, 2);  -- Novotel -> Betina
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 5, 4);  -- Novotel -> Analamanganto staging.distance (idHotelFrom, idHotelTo, valeur) values (1,3,10);
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 6, 6);  -- Novotel -> Analamanganto staging.distance (idHotelFrom, idHotelTo, valeur) values (1,3,10);


INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 4, 3);  -- Betina -> Analamanga
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 5, 2);  -- Betina -> Novotel
INSERT INTO staging.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 6, 6);  -- Betina -> Novotel

insert into staging.distance (idHotelFrom, idHotelTo, valeur) values (4, 5, 1);  -- Novotel -> Analamanga
insert into staging.distance (idHotelFrom, idHotelTo, valeur) values (4, 6, 6);  -- Novotel -> Analamanga   

insert into staging.distance (idHotelFrom, idHotelTo, valeur) values (5, 6, 6);  -- Novotel -> Analamanga

INSERT INTO staging.client (name,email,phone) VALUES('Tina','tina@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Fenitra','fenitra@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Ange','ange@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Soa','soa@gmail.com','0342568678');
INSERT INTO staging.client (name,email,phone) VALUES('Meva','meva@gmail.com','0342568678');


----verification script
 \dt staging.assignation 

 select * from staging.reservation;
 