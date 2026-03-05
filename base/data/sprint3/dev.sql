-- Insertion des unités
INSERT INTO dev.unite (nom_unite) VALUES 
    ('km/h'),
    ('minutes'),
    ('km');

-- Insertion des paramètres
INSERT INTO dev.parametre (nom_param, valeur, unite_id) VALUES 
    ('vitesse_moyenne', 50, 1),      -- 50 km/h
    ('temps_attente_hotel', 10, 2);  -- 10 minutes par hôtel

-- L'aéroport est représenté par l'ID 0 dans la table distance
-- Insertion des distances (exemples)
-- Aéroport (ID 0) vers les hôtels
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES 
    (0, 1, 15),   -- Aéroport -> Colbert: 15 km
    (0, 2, 25),   -- Aéroport -> Novotel: 25 km
    (0, 3, 10),   -- Aéroport -> Ibis: 10 km
    (0, 4, 30);   -- Aéroport -> Lokanga: 30 km

-- Distances entre hôtels
INSERT INTO dev.distance (idHotelFrom, idHotelTo, valeur) VALUES 
    (1, 2, 12),   -- Colbert -> Novotel
    (1, 3, 8),    -- Colbert -> Ibis
    (1, 4, 20),   -- Colbert -> Lokanga
    (2, 3, 18),   -- Novotel -> Ibis
    (2, 4, 15),   -- Novotel -> Lokanga
    (3, 4, 25);   -- Ibis -> Lokanga