-- Reset script for Sprint 6: Clear trajet_execution and related assignations by date
-- Usage: Change the date variable and execute to clean test data

-- Parameters to edit
SET @date_to_reset = '2026-03-17'::DATE;

-- Display what will be deleted
SELECT 
    (SELECT COUNT(*) FROM local.trajet_execution WHERE date_service = @date_to_reset) as nb_trajets_to_delete,
    (SELECT COUNT(*) FROM local.assignation WHERE DATE(assigned_date) = @date_to_reset) as nb_assignations_to_delete,
    (SELECT COUNT(*) FROM local.reservation WHERE DATE(DateHeureArrive) = @date_to_reset AND statut = 'ASSIGNED') as nb_reservations_to_reassign;

-- Begin transaction
BEGIN;

-- Delete assignations for the date
DELETE FROM local.assignation 
WHERE DATE(assigned_date) = @date_to_reset;

-- Update reservation status back to PENDING for that date
UPDATE local.reservation 
SET statut = 'PENDING'
WHERE DATE(DateHeureArrive) = @date_to_reset AND statut = 'ASSIGNED';

-- Delete trajets for the date
DELETE FROM local.trajet_execution 
WHERE date_service = @date_to_reset;

-- Confirmation message
SELECT 'Reset completed for date: ' || @date_to_reset as message;

COMMIT;
