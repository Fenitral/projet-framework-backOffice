package com.cousin.controller;

import com.cousin.model.Hotel;
import com.cousin.model.Reservation;
import com.cousin.service.ReservationService;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.Json;
import com.framework.annotation.Param;
import com.framework.annotation.PostMapping;
import com.framework.model.ModelView;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ReservationController {
    private final ReservationService reservationService = new ReservationService();

    @GetMapping("/reservation/form")
    public ModelView showForm() throws SQLException {
        List<Hotel> hotels = reservationService.listHotels();
        ModelView mv = new ModelView("/WEB-INF/views/reservation.jsp");
        mv.addAttribute("hotels", hotels);
        return mv;
    }

    @PostMapping("/reservation/create")
    public ModelView createReservation(
            @Param("dateHeureArrive") String dateHeureArrive,
            @Param("idClient") String idClient,
            @Param("nbPassager") int nbPassager,
            @Param("hotel.idHotel") int hotelId) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setIdClient(idClient);
        reservation.setNbPassager(nbPassager);
        if (dateHeureArrive != null && !dateHeureArrive.isBlank()) {
            reservation.setDateHeureArrive(LocalDateTime.parse(dateHeureArrive));
        }

        Hotel hotel = new Hotel();
        hotel.setIdHotel(hotelId);
        reservation.setHotel(hotel);

        reservationService.createReservation(reservation);

        ModelView mv = new ModelView("/WEB-INF/views/reservation.jsp");
        mv.addAttribute("hotels", reservationService.listHotels());
        mv.addAttribute("message", "Reservation enregistree");
        return mv;
    }

    @GetMapping("/api/reservation/list")
    @Json
    public List<Reservation> listReservations() throws SQLException {
        return reservationService.listReservations();
    }
}
