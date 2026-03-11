-- Insertion des unités
INSERT INTO dev.unite (nom_unite) VALUES 
    ('km/h'),
    ('minutes'),
    ('km');

-- Insertion des paramètres
INSERT INTO dev.parametre (nom_param, valeur, unite_id) VALUES 
    ('vitesse_moyenne', 50, 1),      -- 50 km/h
    ('temps_attente_hotel', 10, 2);  -- 10 minutes par hôtel

INSERT INTO dev.distance (idhotelfrom, idhotelto, valeur) VALUES
(NULL, 1, 12),   -- Aéroport -> Colbert (12 km)
(NULL, 2, 8),    -- Aéroport -> Novotel (8 km)
(NULL, 3, 15),   -- Aéroport -> Ibis (15 km)
(NULL, 4, 20);   -- Aéroport -> Lokanga (20 km)

INSERT INTO dev.client (name,email,phone) VALUES
('Tina','tina@gmail.com','0342568678');
