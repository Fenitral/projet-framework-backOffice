BEGIN;

-- ==========================================================
-- SPRINT 6 - LOCAL SEED POUR TEST REGROUPEMENT
-- A executer apres local-reinit.sql (base vide)
-- ==========================================================

-- 1) Hotels (id_hotel = 1 est l'aeroport)
INSERT INTO local.hotel (nom, aeroport, is_aeroport) VALUES
	('Aeroport principal', 'IVATO', TRUE),
	('Colbert', NULL, FALSE),
	('Novotel', NULL, FALSE),
	('Ibis', NULL, FALSE),
	('Lokanga', NULL, FALSE),
	('Radisson Blu', NULL, FALSE);

-- 2) Vehicules
INSERT INTO local.vehicule (reference, nbplace, typevehicule, lieu_actuel, statut) VALUES
	('VH-001', 4, 'D', 'AEROPORT', 'DISPONIBLE'),
	('VH-002', 5, 'E', 'AEROPORT', 'DISPONIBLE'),
	('VH-003', 2, 'D', 'AEROPORT', 'DISPONIBLE'),
	('VH-004', 7, 'E', 'AEROPORT', 'DISPONIBLE');

-- 3) Clients
INSERT INTO local.client (name, email, phone) VALUES
	('Tina', 'tina@gmail.com', '0342568678'),
	('Fenitra', 'fenitra@gmail.com', '0342568679'),
	('Ange', 'ange@gmail.com', '0342568680');

-- 4) Unites + Parametres metier
INSERT INTO local.unite (nom_unite) VALUES
	('km/h'),
	('minutes'),
	('km');

INSERT INTO local.parametre (nom_param, valeur, unite_id) VALUES
	('vitesse_moyenne', 50, 1),
	('temps_attente_groupement', 30, 2);

-- 5) Distances
-- Convention aeroport: idhotelfrom ou idhotelto = NULL
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

-- 6) Token (optionnel)
INSERT INTO local.token_expiration (token, expiration) VALUES
	('550e8400-e29b-41d4-a716-446655440000', NOW() + INTERVAL '24 hours');

-- 7) Historique trajet_execution (charge initiale + disponibilite horaire)
-- Date de test: 2026-03-19
-- - VH-001: 1 trajet
-- - VH-002: 1 trajet, retour a 08:18 (candidat dans fenetre 08:00-08:30)
-- - VH-004: 2 trajets (plus charge)
-- INSERT INTO local.trajet_execution
-- 	(vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers)
-- VALUES
-- 	((SELECT id_vehicule FROM local.vehicule WHERE reference = 'VH-001'), DATE '2026-03-19',
-- 	 TIMESTAMP '2026-03-19 07:30:00', TIMESTAMP '2026-03-19 07:55:00', 12.0, 3),

-- 	((SELECT id_vehicule FROM local.vehicule WHERE reference = 'VH-002'), DATE '2026-03-19',
-- 	 TIMESTAMP '2026-03-19 07:50:00', TIMESTAMP '2026-03-19 08:18:00', 14.0, 4),

-- 	((SELECT id_vehicule FROM local.vehicule WHERE reference = 'VH-004'), DATE '2026-03-19',
-- 	 TIMESTAMP '2026-03-19 06:50:00', TIMESTAMP '2026-03-19 07:20:00', 18.0, 6),

-- 	((SELECT id_vehicule FROM local.vehicule WHERE reference = 'VH-004'), DATE '2026-03-19',
-- 	 TIMESTAMP '2026-03-19 07:25:00', TIMESTAMP '2026-03-19 07:45:00', 10.0, 2);

-- 8) Reservations de test groupement
-- Fenetre 1 attendue: [08:00 - 08:30]
-- R4 (8 passagers) non assignable (capacite max vehicule = 7)
INSERT INTO local.reservation (dateheurearrive, idclient, nbpassager, id_hotel, client_id, statut) VALUES
	(TIMESTAMP '2026-03-19 08:00:00', 'CLI-001', 4,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Colbert' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
	 'PENDING'),

	(TIMESTAMP '2026-03-19 08:07:00', 'CLI-002', 3,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Novotel' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'fenitra@gmail.com' LIMIT 1),
	 'PENDING'),

	(TIMESTAMP '2026-03-19 08:22:00', 'CLI-003', 5,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Ibis' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'ange@gmail.com' LIMIT 1),
	 'PENDING'),

	(TIMESTAMP '2026-03-19 08:25:00', 'CLI-004', 8,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Radisson Blu' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'tina@gmail.com' LIMIT 1),
	 'PENDING');

-- Fenetre 2 attendue: commence a 08:40
-- La reservation de 8 passagers doit etre reconsideree mais restera non affectee
INSERT INTO local.reservation (dateheurearrive, idclient, nbpassager, id_hotel, client_id, statut) VALUES
	(TIMESTAMP '2026-03-19 08:40:00', 'CLI-005', 2,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Lokanga' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'fenitra@gmail.com' LIMIT 1),
	 'PENDING'),

	(TIMESTAMP '2026-03-19 08:44:00', 'CLI-006', 6,
	 (SELECT id_hotel FROM local.hotel WHERE nom = 'Colbert' LIMIT 1),
	 (SELECT client_id FROM local.client WHERE email = 'ange@gmail.com' LIMIT 1),
	 'PENDING');

COMMIT;

