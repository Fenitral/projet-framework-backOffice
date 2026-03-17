-- V6.0: Add trajet_execution table and reservation status
-- Sprint 6: Groupement des réservations par fenêtres fixes

BEGIN;

-- Ajouter colonne statut à reservation (PENDING, ASSIGNED, CANCELLED)
ALTER TABLE IF EXISTS local.reservation ADD COLUMN IF NOT EXISTS statut VARCHAR(20) DEFAULT 'PENDING';

-- Créer table historique des trajets
CREATE TABLE IF NOT EXISTS local.trajet_execution (
    trajet_id SERIAL PRIMARY KEY,
    vehicule_id INT NOT NULL,
    date_service DATE NOT NULL,
    heure_depart TIMESTAMP,
    heure_retour TIMESTAMP,
    distance_totale DECIMAL(10, 2),
    nombre_passagers INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicule_id) REFERENCES local.vehicule(id_vehicule) ON DELETE CASCADE
);

-- Créer indices pour optimiser les requêtes
CREATE INDEX IF NOT EXISTS idx_reservation_datearrive ON local.reservation(DateHeureArrive);
CREATE INDEX IF NOT EXISTS idx_trajet_date_service ON local.trajet_execution(date_service);
CREATE INDEX IF NOT EXISTS idx_trajet_vehicule_id ON local.trajet_execution(vehicule_id);
CREATE INDEX IF NOT EXISTS idx_trajet_date_vehicule ON local.trajet_execution(date_service, vehicule_id);
CREATE INDEX IF NOT EXISTS idx_reservation_statut ON local.reservation(statut);

-- Ajouter colonne pour lier assignation à l'historique (optionnel, pour traçabilité)
ALTER TABLE IF EXISTS local.assignation ADD COLUMN IF NOT EXISTS trajet_id INT;
ALTER TABLE IF EXISTS local.assignation ADD CONSTRAINT fk_assignation_trajet 
    FOREIGN KEY (trajet_id) REFERENCES local.trajet_execution(trajet_id) ON DELETE SET NULL;

COMMIT;
