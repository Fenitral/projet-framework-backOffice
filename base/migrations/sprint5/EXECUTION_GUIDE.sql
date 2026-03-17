-- ============================================================================
-- SCRIPT D'EXÉCUTION SPRINT 5 - Ordre d'exécution des migrations
-- ============================================================================

-- Étape 1: Créer le schéma et les tables
-- $> psql -U postgres -d reservation_voiture -f V5.0__2026-03-16__init_local.sql

-- Étape 2: Ajouter les contraintes et index
-- $> psql -U postgres -d reservation_voiture -f V5.1__2026-03-16__constraints_local.sql

-- Étape 3: Aligner la planification avec parametre
-- $> psql -U postgres -d reservation_voiture -f V5.2__2026-03-17__replace_window_with_parametre.sql

-- Étape 4: Réinitialiser les données (optionnel, destructif)
-- $> psql -U postgres -d reservation_voiture -f local-reinit.sql

-- Étape 5: Insérer les données de test
-- $> psql -U postgres -d reservation_voiture -f local-seed.sql

-- ============================================================================
-- ORDRE DE STRUCTURE POUR LES DONNÉES
-- ============================================================================
-- 1. TABLES FONDAMENTALES (sans dépendances)
--    - hotel
--    - client
--    - vehicule
--    - unite
--    - token_expiration
--    - regroupement
--
-- 2. TABLES AVEC DÉPENDANCES
--    - parametre (dépend de unite)
--    - distance (dépend de hotel)
--    - reservation (dépend de hotel, client)
--    - planification (dépend de parametre)
--
-- 3. TABLES RÉSULTATS
--    - assignation (dépend de reservation, client, regroupement, vehicule)
--    - trajet_vehicule (dépend de planification, vehicule)
--    - assignation_detaillee (dépend de trajet_vehicule, reservation, hotel)

-- ============================================================================
-- EXEMPLE DE REQUÊTE POUR EXTRAIRE UNE PLANIFICATION COMPLÈTE
-- ============================================================================
/*
SELECT 
    p.planification_id,
    p.departure_date,
    p.heure_depart,
    p.heure_retour_aeroport,
    v.id_vehicule,
    v.reference,
    v.nbplace,
    v.typevehicule,
    tv.distance_totale,
    tv.heure_depart_aeroport,
    tv.heure_retour_aeroport,
    r.id_reservation,
    c.client_id,
    c.name,
    c.email,
    r.nbpassager,
    h.id_hotel,
    h.nom AS hotel_name,
    ad.ordre_visite
FROM local.planification p
    JOIN local.trajet_vehicule tv ON p.planification_id = tv.planification_id
    JOIN local.vehicule v ON tv.vehicule_id = v.id_vehicule
    JOIN local.assignation_detaillee ad ON tv.trajet_id = ad.trajet_id
    JOIN local.reservation r ON ad.reservation_id = r.id_reservation
    JOIN local.client c ON r.client_id = c.client_id
    JOIN local.hotel h ON ad.hotel_visite = h.id_hotel
WHERE p.departure_date = '2026-03-16'
ORDER BY p.planification_id, tv.trajet_id, ad.ordre_visite;
*/
