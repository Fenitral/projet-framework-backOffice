package com.cousin.service;

import com.cousin.model.Reservation;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationReallocationService {

    private final VehiculeService vehiculeService;
    private final List<Reservation> unassignedReservations = new ArrayList<>();

    public ReservationReallocationService(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    public void addUnassignedReservation(Reservation reservation) {
        if (reservation != null) {
            this.unassignedReservations.add(reservation);
        }
    }

    public List<Reservation> getOrderedCandidatesForNextWindow(LocalDate date, 
                                                               List<Reservation> newReservations) throws SQLException {
        List<Reservation> result = new ArrayList<>();
        
        List<?> availableVehicles = vehiculeService.getAvailableVehicles(date);
        if (availableVehicles == null || availableVehicles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Reservation> sortedUnassigned = this.unassignedReservations.stream()
            .sorted((r1, r2) -> Integer.compare(r1.getIdReservation(), r2.getIdReservation()))
            .collect(Collectors.toList());
        
        result.addAll(sortedUnassigned);

        if (newReservations != null && !newReservations.isEmpty()) {
            List<Reservation> sortedNew = newReservations.stream()
                .sorted((r1, r2) -> r1.getDateHeureArrive().compareTo(r2.getDateHeureArrive()))
                .collect(Collectors.toList());
            result.addAll(sortedNew);
        }

        return result;
    }

    public void markAsAssigned(Reservation reservation) {
        if (reservation != null) {
            reservation.setStatut("ASSIGNED");
            this.unassignedReservations.removeIf(r -> r.getIdReservation() == reservation.getIdReservation());
        }
    }

    public List<Reservation> getUnassignedReservations() {
        return new ArrayList<>(this.unassignedReservations);
    }

    public int getUnassignedCount() {
        return this.unassignedReservations.size();
    }

    public void resetForNewDay() {
        this.unassignedReservations.clear();
    }

    public void printUnassignedSummary() {
        if (this.unassignedReservations.isEmpty()) {
            System.out.println("✅ Aucune réservation en attente");
            return;
        }
        
        System.out.println("⏳ Réservations en attente: " + this.unassignedReservations.size());
        for (Reservation r : this.unassignedReservations) {
            System.out.printf("  - Resa #%d | %02d:%02d | %d pass\n",
                r.getIdReservation(),
                r.getDateHeureArrive().getHour(),
                r.getDateHeureArrive().getMinute(),
                r.getNbPassager());
        }
    }
}
