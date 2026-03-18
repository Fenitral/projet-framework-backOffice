BEGIN;

-- ==========================================================
-- SPRINT 6 - TESTS PRIORITE D'ASSIGNATION
-- Prerequis:
--   1) Executer local-reinit.sql
--   2) Executer local-trajet-seed.sql
-- Objectif:
--   Valider l'ordre de priorite vehicule:
--   1) Capacite la plus proche du besoin
--   2) Moins de trajets
--   3) Diesel prioritaire
--   4) Aleatoire si egalite totale
-- ==========================================================

-- Nettoyage des donnees de test existantes (idempotent)
DELETE FROM local.assignation
WHERE reservation_id IN (
    SELECT r.id_reservation
    FROM local.reservation r
    WHERE r.idclient LIKE 'TST-PRIO-%'
);

DELETE FROM local.reservation WHERE idclient LIKE 'TST-PRIO-%';

DELETE FROM local.trajet_execution
WHERE vehicule_id IN (
    SELECT id_vehicule
    FROM local.vehicule
    WHERE reference LIKE 'TST-PRIO-%'
);

DELETE FROM local.vehicule WHERE reference LIKE 'TST-PRIO-%';

-- ==========================================================
-- Vehicules dedies aux tests
-- ==========================================================
INSERT INTO local.vehicule (reference, nbplace, typevehicule, lieu_actuel, statut) VALUES
    -- Cas 1: capacite la plus proche
    ('TST-PRIO-C1-A', 8,  'E', 'AEROPORT', 'DISPONIBLE'),
    ('TST-PRIO-C1-B', 9,  'E', 'AEROPORT', 'DISPONIBLE'),
    ('TST-PRIO-C1-C', 10, 'E', 'AEROPORT', 'DISPONIBLE'),

    -- Cas 2: egalite capacite -> moins de trajets
    ('TST-PRIO-C2-A', 8,  'E', 'AEROPORT', 'DISPONIBLE'),
    ('TST-PRIO-C2-B', 8,  'E', 'AEROPORT', 'DISPONIBLE'),

    -- Cas 3: egalite capacite + trajets -> diesel
    ('TST-PRIO-C3-D', 9,  'D', 'AEROPORT', 'DISPONIBLE'),
    ('TST-PRIO-C3-E', 9,  'E', 'AEROPORT', 'DISPONIBLE'),

    -- Cas 4: egalite totale -> aleatoire
    ('TST-PRIO-C4-D1', 10, 'D', 'AEROPORT', 'DISPONIBLE'),
    ('TST-PRIO-C4-D2', 10, 'D', 'AEROPORT', 'DISPONIBLE');

-- ==========================================================
-- Historique trajet_execution (pour criteres de charge)
-- ==========================================================

-- CAS 1 (date: 2026-03-20)
-- Objectif: capacite la plus proche pour 8 passagers => TST-PRIO-C1-A (8 places)
-- Pas d'historique necessaire (ou equivalent).

-- CAS 2 (date: 2026-03-21)
-- Objectif: capacite egale (8 vs 8), choisir le moins charge:
--   C2-A = 1 trajet, C2-B = 3 trajets
INSERT INTO local.trajet_execution (vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers)
VALUES
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C2-A' LIMIT 1), DATE '2026-03-21',
     TIMESTAMP '2026-03-21 06:00:00', TIMESTAMP '2026-03-21 06:30:00', 12.00, 6),

    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C2-B' LIMIT 1), DATE '2026-03-21',
     TIMESTAMP '2026-03-21 05:00:00', TIMESTAMP '2026-03-21 05:25:00', 10.00, 4),
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C2-B' LIMIT 1), DATE '2026-03-21',
     TIMESTAMP '2026-03-21 05:30:00', TIMESTAMP '2026-03-21 06:00:00', 14.00, 7),
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C2-B' LIMIT 1), DATE '2026-03-21',
     TIMESTAMP '2026-03-21 06:10:00', TIMESTAMP '2026-03-21 06:40:00', 11.00, 5);

-- CAS 3 (date: 2026-03-22)
-- Objectif: capacite egale + charge egale, diesel prioritaire
--   C3-D = 2 trajets, C3-E = 2 trajets
INSERT INTO local.trajet_execution (vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers)
VALUES
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C3-D' LIMIT 1), DATE '2026-03-22',
     TIMESTAMP '2026-03-22 06:00:00', TIMESTAMP '2026-03-22 06:20:00', 9.00, 3),
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C3-D' LIMIT 1), DATE '2026-03-22',
     TIMESTAMP '2026-03-22 06:30:00', TIMESTAMP '2026-03-22 06:55:00', 13.00, 5),

    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C3-E' LIMIT 1), DATE '2026-03-22',
     TIMESTAMP '2026-03-22 06:05:00', TIMESTAMP '2026-03-22 06:25:00', 8.00, 3),
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C3-E' LIMIT 1), DATE '2026-03-22',
     TIMESTAMP '2026-03-22 06:35:00', TIMESTAMP '2026-03-22 07:00:00', 12.00, 5);

-- CAS 4 (date: 2026-03-23)
-- Objectif: egalite totale -> resultat aleatoire entre C4-D1 et C4-D2
--   C4-D1 = 1 trajet, C4-D2 = 1 trajet, meme type Diesel, meme capacite
INSERT INTO local.trajet_execution (vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers)
VALUES
    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C4-D1' LIMIT 1), DATE '2026-03-23',
     TIMESTAMP '2026-03-23 06:00:00', TIMESTAMP '2026-03-23 06:20:00', 9.00, 4),

    ((SELECT id_vehicule FROM local.vehicule WHERE reference = 'TST-PRIO-C4-D2' LIMIT 1), DATE '2026-03-23',
     TIMESTAMP '2026-03-23 06:10:00', TIMESTAMP '2026-03-23 06:30:00', 9.00, 4);

-- ==========================================================
-- Reservations de test (1 reservation par date/cas)
-- ==========================================================
-- Note: on utilise un client existant du seed (tina@gmail.com)

INSERT INTO local.reservation (dateheurearrive, idclient, nbpassager, id_hotel, client_id, statut) VALUES
    -- CAS 1: besoin 8 passagers -> C1-A attendu (capacite la plus proche)
    (TIMESTAMP '2026-03-20 08:00:00', 'TST-PRIO-C1', 8,
     (SELECT id_hotel FROM local.hotel WHERE nom = 'Colbert' LIMIT 1),
     (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
     'PENDING'),

    -- CAS 2: besoin 8 passagers -> C2-A attendu (moins de trajets)
    (TIMESTAMP '2026-03-21 08:00:00', 'TST-PRIO-C2', 8,
     (SELECT id_hotel FROM local.hotel WHERE nom = 'Novotel' LIMIT 1),
     (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
     'PENDING'),

    -- CAS 3: besoin 9 passagers -> C3-D attendu (diesel)
    (TIMESTAMP '2026-03-22 08:00:00', 'TST-PRIO-C3', 9,
     (SELECT id_hotel FROM local.hotel WHERE nom = 'Ibis' LIMIT 1),
     (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
     'PENDING'),

    -- CAS 4: besoin 10 passagers -> C4-D1 ou C4-D2 (aleatoire)
    (TIMESTAMP '2026-03-23 08:00:00', 'TST-PRIO-C4', 10,
     (SELECT id_hotel FROM local.hotel WHERE nom = 'Lokanga' LIMIT 1),
     (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
     'PENDING');

COMMIT;

-- ==========================================================
-- MODE D'UTILISATION
-- ==========================================================
-- 1) Executer ce script.
-- 2) Lancer la planification pour chaque date:
--    2026-03-20, 2026-03-21, 2026-03-22, 2026-03-23
-- 3) Verifier le vehicule assigne avec la requete ci-dessous.

-- Requete de verification (apres execution de planifier):
-- SELECT
--     r.idclient AS cas,
--     DATE(r.dateheurearrive) AS date_test,
--     v.reference AS vehicule_choisi,
--     v.nbplace,
--     v.typevehicule
-- FROM local.assignation a
-- JOIN local.reservation r ON r.id_reservation = a.reservation_id
-- JOIN local.vehicule v ON v.id_vehicule = a.vehicule_id
-- WHERE r.idclient LIKE 'TST-PRIO-%'
-- ORDER BY DATE(r.dateheurearrive), r.id_reservation;

-- Attendus:
-- - TST-PRIO-C1 => TST-PRIO-C1-A
-- - TST-PRIO-C2 => TST-PRIO-C2-A
-- - TST-PRIO-C3 => TST-PRIO-C3-D
-- - TST-PRIO-C4 => TST-PRIO-C4-D1 OU TST-PRIO-C4-D2 (doit pouvoir varier)
