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
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationController {
    private final ReservationService reservationService = new ReservationService();
 
    @GetMapping("/")
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

    @GetMapping("/reservation/list")
    public ModelView showReservationListPage(
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin) throws SQLException {
        ModelView mv = new ModelView("/WEB-INF/views/reservation/list.jsp");
        
        List<Reservation> reservations;
        if (dateDebut != null && !dateDebut.isBlank() && dateFin != null && !dateFin.isBlank()) {
            LocalDate start = LocalDate.parse(dateDebut);
            LocalDate end = LocalDate.parse(dateFin);
            reservations = reservationService.listReservationsByDateRange(start, end);
        } else {
            reservations = reservationService.listReservations();
        }
        
        mv.addAttribute("reservations", reservations);
        mv.addAttribute("dateDebut", dateDebut);
        mv.addAttribute("dateFin", dateFin);
        return mv;
    }

    @GetMapping("/hotel/list")
    public ModelView showHotelListPage() throws SQLException {
        ModelView mv = new ModelView("/WEB-INF/views/hotel/list.jsp");
        mv.addAttribute("hotels", reservationService.listHotels());
        return mv;
    }
}
