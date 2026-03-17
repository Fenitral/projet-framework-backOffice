package com.cousin.repository;

import com.cousin.model.Client;
import com.cousin.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    public void insert(Client client) throws SQLException {
        String sql = "INSERT INTO local.client(name, email, phone) VALUES (?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getPhone());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    client.setClientId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Client client) throws SQLException {
        String sql = "UPDATE local.client SET name = ?, email = ?, phone = ? WHERE client_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getPhone());
            statement.setInt(4, client.getClientId());
            statement.executeUpdate();
        }
    }

    public Client findById(Integer clientId) throws SQLException {
        String sql = "SELECT client_id, name, email, phone FROM local.client WHERE client_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setClientId(rs.getInt("client_id"));
                    client.setName(rs.getString("name"));
                    client.setEmail(rs.getString("email"));
                    client.setPhone(rs.getString("phone"));
                    return client;
                }
            }
        }
        return null;
    }

    public List<Client> findAll() throws SQLException {
        String sql = "SELECT client_id, name, email, phone FROM local.client ORDER BY client_id";
        List<Client> clients = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("client_id"));
                client.setName(rs.getString("name"));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));
                clients.add(client);
            }
        }
        return clients;
    }

    public void deleteById(Integer clientId) throws SQLException {
        String sql = "DELETE FROM local.client WHERE client_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            statement.executeUpdate();
        }
    }
}
