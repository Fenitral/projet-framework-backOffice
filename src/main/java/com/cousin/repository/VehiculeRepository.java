package com.cousin.repository;

import com.cousin.model.Vehicule;
import com.cousin.util.DbConnection;
import java.sql.SQLException;
import java.util.List;

public class VehiculeRepository {

    public void insert(Vehicule vehicule) throws SQLException {
        if (vehicule == null) {
            throw new SQLException("Vehicule invalide");
        }
    }

    public void update(Vehicule vehicule) throws SQLException {
        if (vehicule == null) {
            throw new SQLException("Vehicule invalide");
        }
    }

    public void deleteById(Integer idVehicule) throws SQLException {
        if (idVehicule == null) {
            throw new SQLException("idVehicule obligatoire");
        }
    }

    public Vehicule findById(Integer idVehicule) throws SQLException {
        if (idVehicule == null) {
            throw new SQLException("idVehicule obligatoire");
        }
        return null;
    }

    public List<Vehicule> findAll() throws SQLException {
        return List.of();
    }
}
