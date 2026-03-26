-- Ajout du champ heure_disponibilite à la table vehicule
ALTER TABLE local.vehicule ADD COLUMN heure_disponibilite TIME NOT NULL DEFAULT '00:00:00';
