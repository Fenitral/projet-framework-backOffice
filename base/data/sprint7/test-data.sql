-- Données de test pour RESERVATION (sprint 7)
-- INSERT INTO local.RESERVATION (idReservation, dateHeureArrive, idClient, nbPassager, statut, nbr_passagers_assignes, statut_reservation, hotel)
-- VALUES
--   (1, '2026-03-20 10:00:00', 'C001', 8, 'PENDING', 0, 'NON_AFFECTEE', 101),
--   (2, '2026-03-20 10:30:00', 'C002', 4, 'PENDING', 0, 'NON_AFFECTEE', 102),
--   (3, '2026-03-20 11:00:00', 'C003', 6, 'PENDING', 0, 'NON_AFFECTEE', 103);

-- -- Données de test pour VEHICULE (sprint 7)
-- INSERT INTO local.VEHICULE (idVehicule, reference, nbPlace, typeVehicule, statut, lieuActuel)
-- VALUES
--   (1, 'V001', 6, 'Diesel', 'DISPONIBLE', 'Aeroport'),
--   (2, 'V002', 4, 'Essence', 'DISPONIBLE', 'Aeroport'),
--   (3, 'V003', 8, 'Diesel', 'DISPONIBLE', 'Aeroport');

-- Données de test pour AFFECTATION_PASSAGER (sprint 7)
INSERT INTO local.AFFECTATION_PASSAGER (reservation_id, vehicule_id, nb_passagers_assignes, position_visite, heure_arrivee_prevue, heure_depart_prevue)
VALUES
  (1, 1, 6, 1, '10:15:00', '10:00:00'),
  (1, 2, 2, 2, '10:40:00', '10:30:00'),
  (2, 3, 4, 1, '11:00:00', '10:45:00');
