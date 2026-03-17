BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_vehicule_statut'
    ) THEN
        ALTER TABLE local.vehicule
            ADD CONSTRAINT chk_local_vehicule_statut
            CHECK (statut IN ('DISPONIBLE', 'ASSIGNE', 'MAINTENANCE'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_distance_non_negative'
    ) THEN
        ALTER TABLE local.distance
            ADD CONSTRAINT chk_local_distance_non_negative
            CHECK (valeur >= 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_distance_different_hotels'
    ) THEN
        ALTER TABLE local.distance
            ADD CONSTRAINT chk_local_distance_different_hotels
            CHECK (idhotelfrom IS NULL OR idhotelto IS NULL OR idhotelfrom != idhotelto);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_trajet_distance_non_negative'
    ) THEN
        ALTER TABLE local.trajet_vehicule
            ADD CONSTRAINT chk_local_trajet_distance_non_negative
            CHECK (distance_totale >= 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_trajet_heure'
    ) THEN
        ALTER TABLE local.trajet_vehicule
            ADD CONSTRAINT chk_local_trajet_heure
            CHECK (heure_depart_aeroport < heure_retour_aeroport);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_temps_attente_window'
    ) THEN
        ALTER TABLE local.temps_attente_window
            ADD CONSTRAINT chk_local_temps_attente_window
            CHECK (window_start < window_end AND minutes_attente > 0);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_local_distance_route
    ON local.distance (COALESCE(idhotelfrom, 0), COALESCE(idhotelto, 0));

CREATE UNIQUE INDEX IF NOT EXISTS uq_local_assignation_reservation
    ON local.assignation (reservation_id)
    WHERE reservation_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_local_reservation_date
    ON local.reservation(dateheurearrive);

CREATE INDEX IF NOT EXISTS idx_local_trajet_planification
    ON local.trajet_vehicule(planification_id);

CREATE INDEX IF NOT EXISTS idx_local_assignation_detail_trajet
    ON local.assignation_detaillee(trajet_id);

COMMIT;
