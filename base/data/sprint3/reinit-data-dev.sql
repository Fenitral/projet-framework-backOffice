
TRUNCATE TABLE dev.distance RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.parametre RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.unite RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.assignation RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.regroupement RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.client RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.Vehicule RESTART IDENTITY CASCADE;
TRUNCATE TABLE dev.Hotel RESTART IDENTITY CASCADE;

INSERT INTO dev.Hotel (nom) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga'),
('Radisson Blu'),
('Analamanga');

-- Tokens de test pour le schéma dev
-- Ces tokens expirent dans 24h à partir de l'insertion
INSERT INTO dev.token_expiration(token, expiration)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', NOW() + INTERVAL '24 hours'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', NOW() + INTERVAL '48 hours');


INSERT INTO dev.Vehicule (Reference, nbPlace, TypeVehicule) VALUES
	('VH-001', 4, 'D'),
	('VH-002', 5, 'E'),
	('VH-003', 2, 'D'),
	('VH-004', 7, 'E');

-- Insertion des unités
INSERT INTO dev.unite (nom_unite) VALUES 
    ('km/h'),
    ('minutes'),
    ('km');

-- Insertion des paramètres
INSERT INTO dev.parametre (nom_param, valeur, unite_id) VALUES 
    ('vitesse_moyenne', 50, 1);      -- 50 km/h

INSERT INTO dev.distance (idhotelfrom, idhotelto, valeur) VALUES
(NULL, 1, 12),   -- Aéroport -> Colbert (12 km)
(NULL, 2, 8),    -- Aéroport -> Novotel (8 km)
(NULL, 3, 15),   -- Aéroport -> Ibis (15 km)
(NULL, 4, 20),   -- Aéroport -> Lokanga (20 km)
(NULL, 5, 8),   -- Aéroport -> Radisson Blu (8 km)
(NULL, 6, 8);   -- Aéroport -> Analamanga (8 km)

INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 2, 3);  -- Analamanga -> Betina
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 3, 2);  -- Betina -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 4, 4);  -- Analamanga -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 5, 4);  -- Analamanga -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 6, 6);  -- Analamanga -> Novotel

INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 3, 3);  -- Betina -> Analamanga
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 4, 2);  -- Novotel -> Betina
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 5, 4);  -- Novotel -> Analamanganto dev.distance (idHotelFrom, idHotelTo, valeur) values (1,3,10);
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (2, 6, 6);  -- Novotel -> Analamanganto dev.distance (idHotelFrom, idHotelTo, valeur) values (1,3,10);


INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 4, 3);  -- Betina -> Analamanga
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 5, 2);  -- Betina -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (3, 6, 6);  -- Betina -> Novotel

insert into dev.distance (idHotelFrom, idHotelTo, valeur) values (4, 5, 1);  -- Novotel -> Analamanga
insert into dev.distance (idHotelFrom, idHotelTo, valeur) values (4, 6, 6);  -- Novotel -> Analamanga   

insert into dev.distance (idHotelFrom, idHotelTo, valeur) values (5, 6, 6);  -- Novotel -> Analamanga

INSERT INTO dev.client (name,email,phone) VALUES('Tina','tina@gmail.com','0342568678');
INSERT INTO dev.client (name,email,phone) VALUES('Fenitra','fenitra@gmail.com','0342568678');
INSERT INTO dev.client (name,email,phone) VALUES('Ange','ange@gmail.com','0342568678');


----verification script
 \dt dev.assignation 
 

INSERT INTO dev.Hotel (nom) VALUES
('zahhotel');