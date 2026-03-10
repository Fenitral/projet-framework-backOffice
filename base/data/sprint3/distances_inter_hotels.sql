-- ===========================================
-- Script pour ajouter les distances entre hôtels
-- ===========================================

-- 1. D'abord, voir les IDs de tes hôtels :
-- SELECT Id_Hotel, nom FROM dev.Hotel ORDER BY nom;

-- 2. Voir les distances existantes :
-- SELECT * FROM dev.distance;

-- ===========================================
-- DISTANCES INTER-HÔTELS
-- Format : (idHotelFrom, idHotelTo, valeur en km)
-- ===========================================

-- Exemple avec les IDs que tu dois remplacer par les vrais :
-- Analamanga (ID=?) vers Betina (ID=?) : 3 km
-- Betina (ID=?) vers Novotel (ID=?) : 2 km  
-- Analamanga (ID=?) vers Novotel (ID=?) : 4 km

-- Remplace ces IDs par les vrais IDs de tes hôtels :
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 2, 3);  -- Analamanga -> Betina
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 3, 2);  -- Betina -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 4, 4);  -- Analamanga -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 5, 4);  -- Analamanga -> Novotel
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES (1, 6, 6);  -- Analamanga -> Novotel

-- Distances dans l'autre sens (si besoin, pour le retour)
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