package com.cousin.repository;

import com.cousin.model.Distance;
import com.cousin.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistanceRepository {

    public List<Distance> findAll() throws SQLException {
        String sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur FROM dev.distance ORDER BY distance_id";
        List<Distance> distances = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Distance d = new Distance();
                d.setDistanceId(rs.getInt("distance_id"));
                d.setIdHotelFrom(rs.getInt("idHotelFrom"));
                d.setIdHotelTo(rs.getInt("idHotelTo"));
                d.setValeur(rs.getInt("valeur"));
                distances.add(d);
            }
        }
        return distances;
    }

    public Distance findByHotels(int idHotelFrom, int idHotelTo) throws SQLException {
        // Gérer le cas où l'aéroport est représenté par 0 dans le code mais NULL dans la DB
        String sql;
        boolean fromIsAirport = (idHotelFrom == 0);
        boolean toIsAirport = (idHotelTo == 0);
        
        if (fromIsAirport && toIsAirport) {
            return null; // Pas de distance aéroport -> aéroport
        } else if (fromIsAirport) {
            sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                  "FROM dev.distance WHERE idHotelFrom IS NULL AND idHotelTo = ?";
        } else if (toIsAirport) {
            sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                  "FROM dev.distance WHERE idHotelFrom = ? AND idHotelTo IS NULL";
        } else {
            sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                  "FROM dev.distance WHERE idHotelFrom = ? AND idHotelTo = ?";
        }

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            if (fromIsAirport) {
                statement.setInt(1, idHotelTo);
            } else if (toIsAirport) {
                statement.setInt(1, idHotelFrom);
            } else {
                statement.setInt(1, idHotelFrom);
                statement.setInt(2, idHotelTo);
            }

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Distance d = new Distance();
                    d.setDistanceId(rs.getInt("distance_id"));
                    d.setIdHotelFrom(rs.getObject("idHotelFrom") != null ? rs.getInt("idHotelFrom") : 0);
                    d.setIdHotelTo(rs.getObject("idHotelTo") != null ? rs.getInt("idHotelTo") : 0);
                    d.setValeur(rs.getInt("valeur"));
                    return d;
                }
            }
        }
        return null;
    }

    /**
     * Trouve la distance entre deux points (bidirectionnel).
     * Cherche d'abord A->B, sinon B->A.
     */
    public int getDistanceValue(int idFrom, int idTo) throws SQLException {
        Distance d = findByHotels(idFrom, idTo);
        if (d != null) {
            return d.getValeur();
        }
        // Essayer l'inverse
        d = findByHotels(idTo, idFrom);
        if (d != null) {
            return d.getValeur();
        }
        return 0; // Distance inconnue
    }

    public void insert(Distance distance) throws SQLException {
        String sql = "INSERT INTO dev.distance(idHotelFrom, idHotelTo, valeur) VALUES (?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, distance.getIdHotelFrom());
            statement.setInt(2, distance.getIdHotelTo());
            statement.setInt(3, distance.getValeur());
            statement.executeUpdate();
        }
    }
}