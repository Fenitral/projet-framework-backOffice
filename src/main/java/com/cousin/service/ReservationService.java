package com.cousin.service;

import com.cousin.model.Client;
import com.cousin.model.Hotel;
import com.cousin.model.Reservation;
import com.cousin.repository.ClientRepository;
import com.cousin.repository.HotelRepository;
import com.cousin.repository.ReservationRepository;
import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;

    public ReservationService() {
        this.hotelRepository = new HotelRepository();
        this.reservationRepository = new ReservationRepository();
        this.clientRepository = new ClientRepository();
    }

    public ReservationService(HotelRepository hotelRepository, ReservationRepository reservationRepository, ClientRepository clientRepository) {
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
    }

    public List<Hotel> listHotels() throws SQLException {
        return hotelRepository.findAll();
    }

    public List<Client> listClients() throws SQLException {
        return clientRepository.findAll();
    }

    public void createReservation(Reservation reservation) throws SQLException {
        reservationRepository.insert(reservation);
    }

    public List<Reservation> listReservations() throws SQLException {
        return reservationRepository.findAllWithHotel();
    }
}
