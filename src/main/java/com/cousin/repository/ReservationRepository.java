package com.cousin.repository;

import com.cousin.model.Client;
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
        String sql = "INSERT INTO local.reservation(DateHeureArrive, idClient, nbPassager, Id_Hotel, client_id) VALUES (?, ?, ?, ?, ?)";

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
            if (reservation.getClientId() != null) {
                statement.setInt(5, reservation.getClientId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }
            statement.executeUpdate();
        }
    }

    public List<Reservation> findAllWithHotel() throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
                     "ORDER BY r.Id_reservation";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Hotel hotel = new Hotel();
                hotel.setIdHotel(resultSet.getInt("Id_Hotel"));
                hotel.setNom(resultSet.getString("nom"));

                Reservation reservation = new Reservation();
                reservation.setIdReservation(resultSet.getInt("Id_reservation"));
                Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
                if (ts != null) {
                    reservation.setDateHeureArrive(ts.toLocalDateTime());
                }
                reservation.setIdClient(resultSet.getString("idClient"));
                reservation.setNbPassager(resultSet.getInt("nbPassager"));
                reservation.setHotel(hotel);
                
                int clientId = resultSet.getInt("client_id");
                if (!resultSet.wasNull()) {
                    reservation.setClientId(clientId);
                    Client client = new Client();
                    client.setClientId(resultSet.getInt("c_client_id"));
                    client.setName(resultSet.getString("c_name"));
                    client.setEmail(resultSet.getString("c_email"));
                    client.setPhone(resultSet.getString("c_phone"));
                    reservation.setClient(client);
                }
                
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public List<Reservation> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
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

                    Reservation reservation = new Reservation();
                    reservation.setIdReservation(resultSet.getInt("Id_reservation"));
                    Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
                    if (ts != null) {
                        reservation.setDateHeureArrive(ts.toLocalDateTime());
                    }
                    reservation.setIdClient(resultSet.getString("idClient"));
                    reservation.setNbPassager(resultSet.getInt("nbPassager"));
                    reservation.setHotel(hotel);
                    
                    int clientId = resultSet.getInt("client_id");
                    if (!resultSet.wasNull()) {
                        reservation.setClientId(clientId);
                        Client client = new Client();
                        client.setClientId(resultSet.getInt("c_client_id"));
                        client.setName(resultSet.getString("c_name"));
                        client.setEmail(resultSet.getString("c_email"));
                        client.setPhone(resultSet.getString("c_phone"));
                        reservation.setClient(client);
                    }
                    
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

    public List<Reservation> findByDateRange(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
                     "WHERE DATE(r.DateHeureArrive) >= ? AND DATE(r.DateHeureArrive) <= ? " +
                     "ORDER BY r.DateHeureArrive DESC";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(dateDebut));
            statement.setDate(2, java.sql.Date.valueOf(dateFin));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Hotel hotel = new Hotel();
                    hotel.setIdHotel(resultSet.getInt("Id_Hotel"));
                    hotel.setNom(resultSet.getString("nom"));

                    Reservation reservation = new Reservation();
                    reservation.setIdReservation(resultSet.getInt("Id_reservation"));
                    Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
                    if (ts != null) {
                        reservation.setDateHeureArrive(ts.toLocalDateTime());
                    }
                    reservation.setIdClient(resultSet.getString("idClient"));
                    reservation.setNbPassager(resultSet.getInt("nbPassager"));
                    reservation.setHotel(hotel);
                    
                    int clientId = resultSet.getInt("client_id");
                    if (!resultSet.wasNull()) {
                        reservation.setClientId(clientId);
                        Client client = new Client();
                        client.setClientId(resultSet.getInt("c_client_id"));
                        client.setName(resultSet.getString("c_name"));
                        client.setEmail(resultSet.getString("c_email"));
                        client.setPhone(resultSet.getString("c_phone"));
                        reservation.setClient(client);
                    }
                    
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

    /**
     * SPRINT 8: Récupère SEULEMENT les réservations NON-AFFECTÉES
     * IMPORTANT: Toutes les dates - pas de filtrage!
     * Car une réservation non-assignée d'hier reste en attente jusqu'à assignation
     * Elle doit être traitée EN PRIORITÉ quand un véhicule se libère
     */
    public List<Reservation> findNonAssignedReservations() throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "r.nbrPassagersAssignes, r.statutReservation, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
                     "WHERE r.statutReservation = 'NON_AFFECTEE' " +
                     "ORDER BY r.DateHeureArrive ASC";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
        }
        return reservations;
    }

    /**
     * SPRINT 8: Récupère SEULEMENT les réservations PARTIELLEMENT-AFFECTÉES
     * IMPORTANT: Toutes les dates - pas de filtrage!
     * Ces réservations attendent les places manquantes jusqu'à assignation complète
     */
    public List<Reservation> findPartiallyAssignedReservations() throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "r.nbrPassagersAssignes, r.statutReservation, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
                     "WHERE r.statutReservation = 'PARTIELLEMENT_AFFECTEE' " +
                     "ORDER BY r.DateHeureArrive ASC";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
        }
        return reservations;
    }

    /**
     * SPRINT 8: Récupère réservations NON/PARTIELLEMENT-AFFECTÉES dans une fenêtre temps (pour 2e vague)
     * Utilisé APRÈS avoir assigné les anciennes non-assignées
     */
    public List<Reservation> findUnassignedInTimeWindow(LocalDate date, java.time.LocalDateTime windowStart, 
                                                       java.time.LocalDateTime windowEnd) throws SQLException {
        String sql = "SELECT r.Id_reservation, r.DateHeureArrive, r.idClient, r.nbPassager, r.client_id, " +
                     "r.nbrPassagersAssignes, r.statutReservation, " +
                     "h.Id_Hotel, h.nom, " +
                     "c.client_id AS c_client_id, c.name AS c_name, c.email AS c_email, c.phone AS c_phone " +
                     "FROM local.reservation r " +
                     "JOIN local.Hotel h ON r.Id_Hotel = h.Id_Hotel " +
                     "LEFT JOIN local.client c ON r.client_id = c.client_id " +
                     "WHERE DATE(r.DateHeureArrive) = ? " +
                     "AND r.DateHeureArrive >= ? AND r.DateHeureArrive <= ? " +
                     "AND (r.statutReservation = 'NON_AFFECTEE' OR r.statutReservation = 'PARTIELLEMENT_AFFECTEE') " +
                     "ORDER BY r.DateHeureArrive ASC";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(windowStart));
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(windowEnd));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
            }
        }
        return reservations;
    }

    /**
     * SPRINT 8: Met à jour le statut et le nombre de passagers assignés d'une réservation
     */
    public void updateReservationStatus(int idReservation, String newStatut, int nbAssignes) throws SQLException {
        String sql = "UPDATE local.reservation SET statutReservation = ?, nbrPassagersAssignes = ? WHERE Id_reservation = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatut);
            statement.setInt(2, nbAssignes);
            statement.setInt(3, idReservation);
            statement.executeUpdate();
        }
    }

    /**
     * Helper privé: mappe un ResultSet à un Reservation
     */
    private Reservation mapResultSetToReservation(ResultSet resultSet) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setIdHotel(resultSet.getInt("Id_Hotel"));
        hotel.setNom(resultSet.getString("nom"));

        Reservation reservation = new Reservation();
        reservation.setIdReservation(resultSet.getInt("Id_reservation"));
        
        Timestamp ts = resultSet.getTimestamp("DateHeureArrive");
        if (ts != null) {
            reservation.setDateHeureArrive(ts.toLocalDateTime());
        }
        
        reservation.setIdClient(resultSet.getString("idClient"));
        reservation.setNbPassager(resultSet.getInt("nbPassager"));
        reservation.setHotel(hotel);
        
        int nbAssignes = resultSet.getInt("nbrPassagersAssignes");
        reservation.setNbrPassagersAssignes(nbAssignes);
        
        String statut = resultSet.getString("statutReservation");
        reservation.setStatutReservation(statut);
        
        int clientId = resultSet.getInt("client_id");
        if (!resultSet.wasNull()) {
            reservation.setClientId(clientId);
            Client client = new Client();
            client.setClientId(resultSet.getInt("c_client_id"));
            client.setName(resultSet.getString("c_name"));
            client.setEmail(resultSet.getString("c_email"));
            client.setPhone(resultSet.getString("c_phone"));
            reservation.setClient(client);
        }
        
        return reservation;
    }
}
