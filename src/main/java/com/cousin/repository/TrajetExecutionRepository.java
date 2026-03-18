package com.cousin.repository;

import com.cousin.model.TrajetExecution;
import com.cousin.util.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrajetExecutionRepository {

    public int insert(TrajetExecution trajet) throws SQLException {
        String sql = "INSERT INTO local.trajet_execution (vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, trajet.getVehiculeId());
            statement.setDate(2, java.sql.Date.valueOf(trajet.getDateService()));
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(trajet.getHeureDepart()));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(trajet.getHeureRetour()));
            statement.setBigDecimal(5, trajet.getDistanceTotale());
            statement.setInt(6, trajet.getNombrePassagers());
            
            statement.executeUpdate();
            
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean existsByVehicleDateAndHours(Integer vehiculeId,
                                               LocalDate dateService,
                                               LocalDateTime heureDepart,
                                               LocalDateTime heureRetour) throws SQLException {
        String sql = "SELECT 1 FROM local.trajet_execution " +
                "WHERE vehicule_id = ? AND date_service = ? AND heure_depart = ? AND heure_retour = ? " +
                "LIMIT 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, vehiculeId);
            statement.setDate(2, java.sql.Date.valueOf(dateService));
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(heureDepart));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(heureRetour));

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Integer findTrajetIdByDateAndHours(LocalDate dateService,
                                              LocalDateTime heureDepart,
                                              LocalDateTime heureRetour) throws SQLException {
        String sql = "SELECT trajet_id FROM local.trajet_execution " +
                "WHERE date_service = ? AND heure_depart = ? AND heure_retour = ? " +
                "ORDER BY created_at DESC, trajet_id DESC LIMIT 1";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, java.sql.Date.valueOf(dateService));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(heureDepart));
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(heureRetour));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("trajet_id");
                }
            }
        }
        return null;
    }

    public void updateVehicleByTrajetId(Integer trajetId, Integer vehiculeId) throws SQLException {
        String sql = "UPDATE local.trajet_execution SET vehicule_id = ? WHERE trajet_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, vehiculeId);
            statement.setInt(2, trajetId);
            statement.executeUpdate();
        }
    }

    public int countTrajetsByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM local.trajet_execution WHERE vehicule_id = ? AND date_service = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vehiculeId);
            statement.setDate(2, java.sql.Date.valueOf(dateService));
            
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }
        return 0;
    }

    public List<TrajetExecution> findByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        String sql = "SELECT trajet_id, vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers " +
                     "FROM local.trajet_execution WHERE vehicule_id = ? AND date_service = ? ORDER BY trajet_id";
        List<TrajetExecution> trajets = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vehiculeId);
            statement.setDate(2, java.sql.Date.valueOf(dateService));
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    TrajetExecution t = new TrajetExecution();
                    t.setTrajetId(rs.getInt("trajet_id"));
                    t.setVehiculeId(rs.getInt("vehicule_id"));
                    t.setDateService(rs.getDate("date_service").toLocalDate());
                    java.sql.Timestamp ts1 = rs.getTimestamp("heure_depart");
                    if (ts1 != null) {
                        t.setHeureDepart(ts1.toLocalDateTime());
                    }
                    java.sql.Timestamp ts2 = rs.getTimestamp("heure_retour");
                    if (ts2 != null) {
                        t.setHeureRetour(ts2.toLocalDateTime());
                    }
                    t.setDistanceTotale(rs.getBigDecimal("distance_totale"));
                    t.setNombrePassagers(rs.getInt("nombre_passagers"));
                    trajets.add(t);
                }
            }
        }
        return trajets;
    }

    public List<TrajetExecution> findByDate(LocalDate dateService) throws SQLException {
        String sql = "SELECT trajet_id, vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers " +
                     "FROM local.trajet_execution WHERE date_service = ? ORDER BY trajet_id";
        List<TrajetExecution> trajets = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, java.sql.Date.valueOf(dateService));
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    TrajetExecution t = new TrajetExecution();
                    t.setTrajetId(rs.getInt("trajet_id"));
                    t.setVehiculeId(rs.getInt("vehicule_id"));
                    t.setDateService(rs.getDate("date_service").toLocalDate());
                    java.sql.Timestamp ts1 = rs.getTimestamp("heure_depart");
                    if (ts1 != null) {
                        t.setHeureDepart(ts1.toLocalDateTime());
                    }
                    java.sql.Timestamp ts2 = rs.getTimestamp("heure_retour");
                    if (ts2 != null) {
                        t.setHeureRetour(ts2.toLocalDateTime());
                    }
                    t.setDistanceTotale(rs.getBigDecimal("distance_totale"));
                    t.setNombrePassagers(rs.getInt("nombre_passagers"));
                    trajets.add(t);
                }
            }
        }
        return trajets;
    }

    public int deleteByDate(LocalDate dateService) throws SQLException {
        String sql = "DELETE FROM local.trajet_execution WHERE date_service = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, java.sql.Date.valueOf(dateService));
            return statement.executeUpdate();
        }
    }

    public TrajetExecution findById(Integer trajetId) throws SQLException {
        String sql = "SELECT trajet_id, vehicule_id, date_service, heure_depart, heure_retour, distance_totale, nombre_passagers " +
                     "FROM local.trajet_execution WHERE trajet_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, trajetId);
            
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    TrajetExecution t = new TrajetExecution();
                    t.setTrajetId(rs.getInt("trajet_id"));
                    t.setVehiculeId(rs.getInt("vehicule_id"));
                    t.setDateService(rs.getDate("date_service").toLocalDate());
                    java.sql.Timestamp ts1 = rs.getTimestamp("heure_depart");
                    if (ts1 != null) {
                        t.setHeureDepart(ts1.toLocalDateTime());
                    }
                    java.sql.Timestamp ts2 = rs.getTimestamp("heure_retour");
                    if (ts2 != null) {
                        t.setHeureRetour(ts2.toLocalDateTime());
                    }
                    t.setDistanceTotale(rs.getBigDecimal("distance_totale"));
                    t.setNombrePassagers(rs.getInt("nombre_passagers"));
                    return t;
                }
            }
        }
        return null;
    }

    public void deleteById(Integer trajetId) throws SQLException {
        String sql = "DELETE FROM local.trajet_execution WHERE trajet_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, trajetId);
            statement.executeUpdate();
        }
    }

    public int deleteByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        String sql = "DELETE FROM local.trajet_execution WHERE vehicule_id = ? AND date_service = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vehiculeId);
            statement.setDate(2, java.sql.Date.valueOf(dateService));
            return statement.executeUpdate();
        }
    }
}
