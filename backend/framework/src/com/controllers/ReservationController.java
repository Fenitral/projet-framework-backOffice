package com.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.annotations.Api;
import com.annotations.ControllerAnnotation;
import com.annotations.GetMapping;
import com.annotations.PostMapping;
import com.annotations.Param;
import com.models.Hotel;
import com.models.Reservation;
import com.utils.DatabaseConnection;

@ControllerAnnotation
public class ReservationController {

    @GetMapping("/api/reservations")
    @Api
    public List<Reservation> getAllReservations() throws Exception {
        List<Reservation> reservations = new ArrayList<>();
        
        String sql = "SELECT r.reservation_id, r.client, r.nb_people, r.dateheure, r.hotel_id, " +
                     "h.hotel_id as h_id, h.name, h.addresse, h.phone " +
                     "FROM local.reservation_temp r " +
                     "LEFT JOIN local.hotel h ON r.hotel_id = h.hotel_id " +
                     "ORDER BY r.dateheure DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setClient(rs.getString("client"));
                reservation.setNbPeople(rs.getInt("nb_people"));
                reservation.setDateheure(rs.getTimestamp("dateheure"));
                reservation.setHotelId(rs.getInt("hotel_id"));
                
                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("h_id"));
                hotel.setName(rs.getString("name"));
                hotel.setAddresse(rs.getString("addresse"));
                hotel.setPhone(rs.getString("phone"));
                reservation.setHotel(hotel);
                
                reservations.add(reservation);
            }
        }
        
        return reservations;
    }

    @GetMapping("/api/reservations/{id}")
    @Api
    public Reservation getReservationById(@Param("id") int id) throws Exception {
        Reservation reservation = null;
        
        String sql = "SELECT r.reservation_id, r.client, r.nb_people, r.dateheure, r.hotel_id, " +
                     "h.hotel_id as h_id, h.name, h.addresse, h.phone " +
                     "FROM local.reservation_temp r " +
                     "LEFT JOIN local.hotel h ON r.hotel_id = h.hotel_id " +
                     "WHERE r.reservation_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reservation = new Reservation();
                    reservation.setReservationId(rs.getInt("reservation_id"));
                    reservation.setClient(rs.getString("client"));
                    reservation.setNbPeople(rs.getInt("nb_people"));
                    reservation.setDateheure(rs.getTimestamp("dateheure"));
                    reservation.setHotelId(rs.getInt("hotel_id"));
                    
                    Hotel hotel = new Hotel();
                    hotel.setHotelId(rs.getInt("h_id"));
                    hotel.setName(rs.getString("name"));
                    hotel.setAddresse(rs.getString("addresse"));
                    hotel.setPhone(rs.getString("phone"));
                    reservation.setHotel(hotel);
                }
            }
        }
        
        return reservation;
    }

    @PostMapping("/api/reservations")
    @Api
    public Reservation createReservation(@Param("client") String client,
                                          @Param("nbPeople") int nbPeople,
                                          @Param("dateheure") String dateheure,
                                          @Param("hotelId") int hotelId) throws Exception {
        Reservation reservation = new Reservation();
        
        String sql = "INSERT INTO local.reservation_temp (client, nb_people, dateheure, hotel_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING reservation_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, client);
            ps.setInt(2, nbPeople);
            ps.setTimestamp(3, Timestamp.valueOf(dateheure.replace("T", " ")));
            ps.setInt(4, hotelId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reservation.setReservationId(rs.getInt("reservation_id"));
                    reservation.setClient(client);
                    reservation.setNbPeople(nbPeople);
                    reservation.setDateheure(Timestamp.valueOf(dateheure.replace("T", " ")));
                    reservation.setHotelId(hotelId);
                }
            }
        }
        
        return reservation;
    }

    @GetMapping("/api/hotels")
    @Api
    public List<Hotel> getAllHotels() throws Exception {
        List<Hotel> hotels = new ArrayList<>();
        
        String sql = "SELECT hotel_id, name, addresse, phone FROM local.hotel ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setAddresse(rs.getString("addresse"));
                hotel.setPhone(rs.getString("phone"));
                hotels.add(hotel);
            }
        }
        
        return hotels;
    }
}
