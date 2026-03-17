BEGIN;

INSERT INTO local.hotel (nom, aeroport, is_aeroport) VALUES
    ('Aeroport principal', 'IVATO', TRUE),
    ('Colbert', NULL, FALSE),
    ('Novotel', NULL, FALSE),
    ('Ibis', NULL, FALSE),
    ('Lokanga', NULL, FALSE),
    ('Radisson Blu', NULL, FALSE);

INSERT INTO local.vehicule (reference, nbplace, typevehicule, lieu_actuel, statut) VALUES
    ('VH-001', 4, 'D', 'AEROPORT', 'DISPONIBLE'),
    ('VH-002', 5, 'E', 'AEROPORT', 'DISPONIBLE'),
    ('VH-003', 2, 'D', 'AEROPORT', 'MAINTENANCE'),
    ('VH-004', 7, 'E', 'AEROPORT', 'DISPONIBLE');

INSERT INTO local.client (name, email, phone) VALUES
    ('Tina', 'tina@gmail.com', '0342568678'),
    ('Fenitra', 'fenitra@gmail.com', '0342568679'),
    ('Ange', 'ange@gmail.com', '0342568680');

INSERT INTO local.reservation (dateheurearrive, idclient, nbpassager, id_hotel, client_id) VALUES
    ('2026-03-16 09:00:00', 'CLI-001', 3, 2, 1),
    ('2026-03-16 09:15:00', 'CLI-002', 5, 3, 2),
    ('2026-03-16 10:00:00', 'CLI-003', 2, 4, 3);

INSERT INTO local.unite (nom_unite) VALUES
    ('km/h'),
    ('minutes'),
    ('km');

INSERT INTO local.parametre (nom_param, valeur, unite_id) VALUES
    ('vitesse_moyenne', 50, 1),
    ('temps_attente_hotel', 10, 2);

-- AEROPORT is represented by idhotelfrom = NULL to match current repository logic.
INSERT INTO local.distance (idhotelfrom, idhotelto, valeur) VALUES
    (NULL, 2, 12),
    (NULL, 3, 8),
    (NULL, 4, 15),
    (NULL, 5, 20),
    (NULL, 6, 8),
    (2, 3, 3),
    (2, 4, 2),
    (2, 5, 4),
    (2, 6, 6),
    (3, 4, 3),
    (3, 5, 2),
    (3, 6, 6),
    (4, 5, 1),
    (4, 6, 6),
    (5, 6, 6);

INSERT INTO local.token_expiration (token, expiration) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', NOW() + INTERVAL '24 hours'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', NOW() + INTERVAL '48 hours');

-- Fenêtres de temps d'attente (groupage de réservations par 30 min)
INSERT INTO local.temps_attente_window (departure_date, window_start, window_end, minutes_attente) VALUES
    ('2026-03-16', '09:00:00', '09:30:00', 30),
    ('2026-03-16', '09:30:00', '10:00:00', 30),
    ('2026-03-16', '10:00:00', '10:30:00', 30);

-- Planifications complètes (résultats de regroupement et assignation)
INSERT INTO local.planification (departure_date, window_id, heure_depart, heure_retour_aeroport, description) VALUES
    ('2026-03-16', 1, '2026-03-16 09:30:00', '2026-03-16 11:45:00', 'Groupe 1: 2 réservations, 1 véhicule'),
    ('2026-03-16', 2, '2026-03-16 10:30:00', '2026-03-16 12:00:00', 'Groupe 2: 1 réservation, 1 véhicule');

-- Trajets véhicules associés aux planifications
INSERT INTO local.trajet_vehicule (planification_id, vehicule_id, distance_totale, heure_depart_aeroport, heure_retour_aeroport, ordre_trajet) VALUES
    (1, 1, 42, '2026-03-16 09:30:00', '2026-03-16 11:45:00', 1),
    (2, 2, 8, '2026-03-16 10:30:00', '2026-03-16 12:00:00', 1);

-- Assignations détaillées avec ordre de visite
INSERT INTO local.assignation_detaillee (trajet_id, reservation_id, ordre_visite, hotel_visite) VALUES
    (1, 1, 1, 2),
    (1, 2, 2, 3),
    (2, 3, 1, 4);

COMMIT;


update local.distance set idhotelfrom=null where distance_id=2;