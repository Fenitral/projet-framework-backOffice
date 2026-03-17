package com.cousin.repository;

import com.cousin.model.Assignation;
import com.cousin.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignationRepository {

    public void insert(Assignation assignation) throws SQLException {
        String sql = "INSERT INTO local.assignation(reservation_id, client_id, regroupement_id, vehicule_id, assigned_date) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, assignation.getReservationId());
            statement.setInt(2, assignation.getClientId());
            
            if (assignation.getRegroupementId() > 0) {
                statement.setInt(3, assignation.getRegroupementId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            
            statement.setInt(4, assignation.getVehiculeId());
            statement.setTimestamp(5, Timestamp.valueOf(assignation.getAssignedDate()));
            
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    assignation.setAssignationId(rs.getInt(1));
                }
            }
        }
    }

    public List<Integer> findAssignedReservationIds() throws SQLException {
        String sql = "SELECT DISTINCT reservation_id FROM local.assignation WHERE reservation_id IS NOT NULL";
        List<Integer> ids = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("reservation_id"));
            }
        }
        return ids;
    }

    public List<Assignation> findByDate(java.time.LocalDate date) throws SQLException {
        String sql = "SELECT assignation_id, client_id, regroupement_id, vehicule_id, assigned_date " +
                     "FROM local.assignation WHERE DATE(assigned_date) = ? ORDER BY assigned_date";
        List<Assignation> assignations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Assignation a = new Assignation();
                    a.setAssignationId(rs.getInt("assignation_id"));
                    a.setClientId(rs.getInt("client_id"));
                    a.setRegroupementId(rs.getInt("regroupement_id"));
                    a.setVehiculeId(rs.getInt("vehicule_id"));
                    Timestamp ts = rs.getTimestamp("assigned_date");
                    if (ts != null) {
                        a.setAssignedDate(ts.toLocalDateTime());
                    }
                    assignations.add(a);
                }
            }
        }
        return assignations;
    }

    public void deleteByDate(java.time.LocalDate date) throws SQLException {
        String sql = "DELETE FROM local.assignation WHERE DATE(assigned_date) = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            statement.executeUpdate();
        }
    }
}
