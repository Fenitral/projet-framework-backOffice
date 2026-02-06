create database travel_agency;
\c travel_agency;

CREATE SCHEMA local;

create TABLE local.hotel (
    hotel_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    addresse TEXT NOT NULL,
    phone VARCHAR(15)
);


CREATE TABLE local.reservation_temp (
    reservation_id SERIAL PRIMARY KEY,
    client VARCHAR(50) NOT NULL,
    nb_people INT NOT NULL,
    dateheure TIMESTAMP NOT NULL,
    hotel_id INT REFERENCES local.hotel(hotel_id)
);

-- Données de test
INSERT INTO local.hotel (name, addresse, phone) VALUES
('Grand Hotel', '123 Avenue de Paris, Antananarivo', '034 00 000 01'),
('Hotel Carlton', '456 Rue République, Antananarivo', '034 00 000 02'),
('Le Pavillon', '789 Boulevard Ratsimandrava, Antsirabe', '034 00 000 03');

INSERT INTO local.reservation_temp (client, nb_people, dateheure, hotel_id) VALUES
('Jean Dupont', 2, '2026-02-10 14:00:00', 1),
('Marie Martin', 4, '2026-02-15 10:00:00', 2),
('Pierre Rakoto', 1, '2026-02-20 18:30:00', 3);
