package com.cousin.repository;

import com.cousin.model.Reservation;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservationRepository {

    public void insert(Reservation reservation) throws SQLException {
        if (reservation == null) {
            throw new SQLException("Reservation invalide");
        }
    }

    public void update(Reservation reservation) throws SQLException {
        if (reservation == null) {
            throw new SQLException("Reservation invalide");
        }
    }

    public Reservation findById(Integer idReservation) throws SQLException {
        if (idReservation == null) {
            throw new SQLException("idReservation obligatoire");
        }
        return null;
    }

    public List<Reservation> findByDate(LocalDate date) throws SQLException {
        if (date == null) {
            throw new SQLException("date obligatoire");
        }
        return List.of();
    }

    public List<Reservation> findAll() throws SQLException {
        return List.of();
    }

    public void deleteById(Integer idReservation) throws SQLException {
        if (idReservation == null) {
            throw new SQLException("idReservation obligatoire");
        }
    }
}
