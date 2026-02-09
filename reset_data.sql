-- Script de réinitialisation des données
-- Utilisation: psql -U postgres -d travel_agency -f reset_data.sql

\c travel_agency;

-- Vider les tables (en respectant l'ordre des contraintes de clés étrangères)
TRUNCATE TABLE local.reservation_temp CASCADE;
TRUNCATE TABLE local.hotel CASCADE;

-- Réinitialiser les séquences
ALTER SEQUENCE local.hotel_hotel_id_seq RESTART WITH 1;
ALTER SEQUENCE local.reservation_temp_reservation_id_seq RESTART WITH 1;

-- Réinsérer les données de test pour hotel
INSERT INTO local.hotel (name, addresse, phone) VALUES
('Grand Hotel', '123 Avenue de Paris, Antananarivo', '034 00 000 01'),
('Hotel Carlton', '456 Rue République, Antananarivo', '034 00 000 02'),
('Le Pavillon', '789 Boulevard Ratsimandrava, Antsirabe', '034 00 000 03');

-- Réinsérer les données de test pour reservation_temp
INSERT INTO local.reservation_temp (client, nb_people, dateheure, hotel_id) VALUES
('Jean Dupont', 2, '2026-02-10 14:00:00', 1);

-- ('Marie Martin', 4, '2026-02-15 10:00:00', 2),
-- ('Pierre Rakoto', 1, '2026-02-20 18:30:00', 3);

-- Afficher le résultat
SELECT 'Données réinitialisées avec succès!' as status;
SELECT COUNT(*) as nb_hotels FROM local.hotel;
SELECT COUNT(*) as nb_reservations FROM local.reservation_temp;
