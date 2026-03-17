BEGIN;

ALTER TABLE local.planification
    ADD COLUMN IF NOT EXISTS parametre_id INT;

INSERT INTO local.parametre (nom_param, valeur, unite_id)
SELECT
    'temps_attente_groupement',
    30,
    (
        SELECT unite_id
        FROM local.unite
        WHERE nom_unite = 'minutes'
        LIMIT 1
    )
WHERE NOT EXISTS (
    SELECT 1
    FROM local.parametre
    WHERE nom_param = 'temps_attente_groupement'
);

UPDATE local.planification
SET parametre_id = (
    SELECT parametre_id
    FROM local.parametre
    WHERE nom_param = 'temps_attente_groupement'
    LIMIT 1
)
WHERE parametre_id IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_local_parametre_valeur_positive'
    ) THEN
        ALTER TABLE local.parametre
            ADD CONSTRAINT chk_local_parametre_valeur_positive
            CHECK (valeur > 0);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_local_parametre_nom
    ON local.parametre(nom_param);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_local_planification_parametre'
    ) THEN
        ALTER TABLE local.planification
            ADD CONSTRAINT fk_local_planification_parametre
            FOREIGN KEY (parametre_id)
            REFERENCES local.parametre(parametre_id);
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'local'
          AND table_name = 'planification'
          AND column_name = 'window_' || 'id'
    ) THEN
        EXECUTE 'ALTER TABLE local.planification DROP COLUMN IF EXISTS ' || 'window_' || 'id';
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'local'
          AND table_name = 'temps_attente_' || 'window'
    ) THEN
        EXECUTE 'DROP TABLE IF EXISTS local.' || 'temps_attente_' || 'window';
    END IF;
END $$;

COMMIT;