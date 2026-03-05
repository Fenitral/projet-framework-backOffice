package com.cousin.repository;

import com.cousin.model.Parametre;
import com.cousin.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParametreRepository {

    public List<Parametre> findAll() throws SQLException {
        String sql = "SELECT p.parametre_id, p.nom_param, p.valeur, p.unite_id, u.nom_unite " +
                     "FROM dev.parametre p " +
                     "LEFT JOIN dev.unite u ON p.unite_id = u.unite_id " +
                     "ORDER BY p.parametre_id";
        List<Parametre> parametres = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Parametre p = new Parametre();
                p.setParametreId(rs.getInt("parametre_id"));
                p.setNomParam(rs.getString("nom_param"));
                p.setValeur(rs.getInt("valeur"));
                p.setUniteId(rs.getInt("unite_id"));
                p.setNomUnite(rs.getString("nom_unite"));
                parametres.add(p);
            }
        }
        return parametres;
    }

    public Parametre findByNom(String nomParam) throws SQLException {
        String sql = "SELECT p.parametre_id, p.nom_param, p.valeur, p.unite_id, u.nom_unite " +
                     "FROM dev.parametre p " +
                     "LEFT JOIN dev.unite u ON p.unite_id = u.unite_id " +
                     "WHERE p.nom_param = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomParam);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Parametre p = new Parametre();
                    p.setParametreId(rs.getInt("parametre_id"));
                    p.setNomParam(rs.getString("nom_param"));
                    p.setValeur(rs.getInt("valeur"));
                    p.setUniteId(rs.getInt("unite_id"));
                    p.setNomUnite(rs.getString("nom_unite"));
                    return p;
                }
            }
        }
        return null;
    }

    public void update(Parametre parametre) throws SQLException {
        String sql = "UPDATE dev.parametre SET valeur = ?, unite_id = ? WHERE parametre_id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parametre.getValeur());
            statement.setInt(2, parametre.getUniteId());
            statement.setInt(3, parametre.getParametreId());
            statement.executeUpdate();
        }
    }

    public void insert(Parametre parametre) throws SQLException {
        String sql = "INSERT INTO dev.parametre(nom_param, valeur, unite_id) VALUES (?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, parametre.getNomParam());
            statement.setInt(2, parametre.getValeur());
            statement.setInt(3, parametre.getUniteId());
            statement.executeUpdate();
        }
    }
}