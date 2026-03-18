-- Migration Sprint 7: Ajout séparation passagers et affectation

-- 1. Modifier la table RESERVATION
ALTER TABLE local.RESERVATION
ADD COLUMN nbr_passagers_assignes INT DEFAULT 0,
ADD COLUMN statut_reservation VARCHAR(30) DEFAULT 'NON_AFFECTEE';

-- 2. Créer la table AFFECTATION_PASSAGER
CREATE TABLE local.AFFECTATION_PASSAGER (
    id BIGSERIAL PRIMARY KEY,
    reservation_id INTEGER NOT NULL,
    vehicule_id INTEGER NOT NULL,
    nb_passagers_assignes INT NOT NULL,
    position_visite INT,
    heure_arrivee_prevue TIME,
    heure_depart_prevue TIME,
    FOREIGN KEY (reservation_id) REFERENCES local.reservation(id_reservation),
    FOREIGN KEY (vehicule_id) REFERENCES local.vehicule(id_vehicule)
);
