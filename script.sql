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
