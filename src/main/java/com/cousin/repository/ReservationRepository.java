package com.cousin.repository;

import com.cousin.model.Hotel;
import com.cousin.model.Reservation;
import com.cousin.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public void insert(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO dev.reservation(DateHeureArrive, idClient, nbPassager, Id_Hotel) VALUES (?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (reservation.getDateHeureArrive() != null) {
                statement.setTimestamp(1, Timestamp.valueOf(reservation.getDateHeureArrive()));
            } else {
                statement.setTimestamp(1, null);
            }
            statement.setString(2, reservation.getIdClient());
            statement.setInt(3, reservation.getNbPassager());
            if (reservation.getHotel() == null) {
                throw new SQLException("Hotel is required for reservation");
            }
            statement.setInt(4, reservation.getHotel().getIdHotel());
            statement.executeUpdate();
        }
    }

    public List<Reservation> findAllWithHotel() throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, " +
                     "h.Id_Hotel, h.nom, h.aeroport " +
                     "FROM dev.reservation r " +
                     "JOIN dev.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "ORDER BY r.Id_reservation";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Hotel hotel = new Hotel();
                hotel.setIdHotel(resultSet.getInt("Id_Hotel"));
                hotel.setNom(resultSet.getString("nom"));
                hotel.setAeroport(resultSet.getString("aeroport"));

                Reservation reservation = new Reservation();
                reservation.setIdReservation(resultSet.getInt("Id_reservation"));
                Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
                if (ts != null) {
                    reservation.setDateHeureArrive(ts.toLocalDateTime());
                }
                reservation.setIdClient(resultSet.getString("idClient"));
                reservation.setNbPassager(resultSet.getInt("nbPassager"));
                reservation.setHotel(hotel);
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    /**
     * Récupère toutes les réservations pour une date donnée.
     */
    public List<Reservation> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, " +
                     "h.Id_Hotel, h.nom, h.aeroport " +
                     "FROM dev.reservation r " +
                     "JOIN dev.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "WHERE DATE(r.DateHeureArrive) = ? " +
                     "ORDER BY r.DateHeureArrive";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Hotel hotel = new Hotel();
                    hotel.setIdHotel(resultSet.getInt("Id_Hotel"));
                    hotel.setNom(resultSet.getString("nom"));
                    hotel.setAeroport(resultSet.getString("aeroport"));

                    Reservation reservation = new Reservation();
                    reservation.setIdReservation(resultSet.getInt("Id_reservation"));
                    Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
                    if (ts != null) {
                        reservation.setDateHeureArrive(ts.toLocalDateTime());
                    }
                    reservation.setIdClient(resultSet.getString("idClient"));
                    reservation.setNbPassager(resultSet.getInt("nbPassager"));
                    reservation.setHotel(hotel);
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }
}