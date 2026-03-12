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
    
    public List<Client> findAll() throws SQLException {
        String sql = "SELECT client_id, name, email, phone FROM dev.client ORDER BY name";
        List<Client> clients = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Client client = new Client();
                client.setClientId(resultSet.getInt("client_id"));
                client.setName(resultSet.getString("name"));
                client.setEmail(resultSet.getString("email"));
                client.setPhone(resultSet.getString("phone"));
                clients.add(client);
            }
        }

        return clients;
    }

    public Client findById(int clientId) throws SQLException {
        String sql = "SELECT client_id, name, email, phone FROM dev.client WHERE client_id = ?";
        
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Client client = new Client();
                    client.setClientId(resultSet.getInt("client_id"));
                    client.setName(resultSet.getString("name"));
                    client.setEmail(resultSet.getString("email"));
                    client.setPhone(resultSet.getString("phone"));
                    return client;
                }
            }
        }

        return null;
    }
}
