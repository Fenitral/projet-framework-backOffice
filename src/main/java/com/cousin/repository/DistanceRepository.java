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

    private String resolveSchema(Connection connection) throws SQLException {
        String schema = connection.getSchema();
        if (schema == null || schema.isBlank()) {
            return "public";
        }
        return schema;
    }

    public List<Distance> findAll() throws SQLException {
        List<Distance> distances = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT distance_id, idHotelFrom, idHotelTo, valeur FROM "
                             + resolveSchema(connection)
                             + ".distance ORDER BY distance_id");
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

        try (Connection connection = DbConnection.getConnection();
             ) {
            String schema = resolveSchema(connection);

            if (fromIsAirport && toIsAirport) {
                return null; // Pas de distance aéroport -> aéroport
            } else if (fromIsAirport) {
                // Accepte les 2 représentations de l'aéroport: NULL ou id de l'hôtel marqué is_aeroport.
                sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                      "FROM " + schema + ".distance " +
                      "WHERE (idHotelFrom IS NULL OR idHotelFrom = (" +
                      "SELECT id_hotel FROM " + schema + ".hotel WHERE is_aeroport = TRUE LIMIT 1" +
                      ")) AND idHotelTo = ?";
            } else if (toIsAirport) {
                // Accepte les 2 représentations de l'aéroport: NULL ou id de l'hôtel marqué is_aeroport.
                sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                      "FROM " + schema + ".distance " +
                      "WHERE idHotelFrom = ? AND (idHotelTo IS NULL OR idHotelTo = (" +
                      "SELECT id_hotel FROM " + schema + ".hotel WHERE is_aeroport = TRUE LIMIT 1" +
                      "))";
            } else {
                sql = "SELECT distance_id, idHotelFrom, idHotelTo, valeur " +
                      "FROM " + schema + ".distance WHERE idHotelFrom = ? AND idHotelTo = ?";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            
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
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO " + resolveSchema(connection) + ".distance(idHotelFrom, idHotelTo, valeur) VALUES (?, ?, ?)")) {
            statement.setInt(1, distance.getIdHotelFrom());
            statement.setInt(2, distance.getIdHotelTo());
            statement.setInt(3, distance.getValeur());
            statement.executeUpdate();
        }
    }
}
