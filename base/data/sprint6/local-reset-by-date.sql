-- Sprint 6 reset cible par date (schema local)
-- Modifier la date dans params.cible_date avant execution

BEGIN;

WITH params AS (
    SELECT DATE '2026-03-19' AS cible_date
)
-- 1) Supprimer les assignations faites ce jour
DELETE FROM local.assignation a
USING params p
WHERE DATE(a.assigned_date) = p.cible_date;

WITH params AS (
    SELECT DATE '2026-03-19' AS cible_date
)
-- 2) Remettre les reservations du jour en PENDING
UPDATE local.reservation r
SET statut = 'PENDING'
FROM params p
WHERE DATE(r.dateheurearrive) = p.cible_date
  AND r.statut IS NOT NULL
  AND UPPER(r.statut) = 'ASSIGNED';

WITH params AS (
    SELECT DATE '2026-03-19' AS cible_date
)
-- 3) Supprimer les trajets executes du jour
DELETE FROM local.trajet_execution t
USING params p
WHERE t.date_service = p.cible_date;

COMMIT;

-- Verification rapide (optionnel)
-- WITH params AS (SELECT DATE '2026-03-19' AS cible_date)
-- SELECT
--   (SELECT COUNT(*) FROM local.assignation a, params p WHERE DATE(a.assigned_date) = p.cible_date) AS assignations_restantes,
--   (SELECT COUNT(*) FROM local.trajet_execution t, params p WHERE t.date_service = p.cible_date) AS trajets_restants,
--   (SELECT COUNT(*) FROM local.reservation r, params p WHERE DATE(r.dateheurearrive) = p.cible_date AND UPPER(COALESCE(r.statut, 'PENDING')) = 'ASSIGNED') AS reservations_assigned_restantes;
