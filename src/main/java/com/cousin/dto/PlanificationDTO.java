package com.cousin.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanificationDTO {
    private LocalDate datePlanification;
    private List<TrajetVehiculeDTO> trajets;
    private List<ReservationAffecteeDTO> reservationsNonAffectees;
    private double distanceTotaleJour;
    private int totalPassagers;
    private int totalReservations;

    public PlanificationDTO() {
        this.trajets = new ArrayList<>();
        this.reservationsNonAffectees = new ArrayList<>();
    }

    // Getters et Setters
    public LocalDate getDatePlanification() {
        return datePlanification;
    }

    public void setDatePlanification(LocalDate datePlanification) {
        this.datePlanification = datePlanification;
    }

    public List<TrajetVehiculeDTO> getTrajets() {
        return trajets;
    }

    public void setTrajets(List<TrajetVehiculeDTO> trajets) {
        this.trajets = trajets;
    }

    public List<ReservationAffecteeDTO> getReservationsNonAffectees() {
        return reservationsNonAffectees;
    }

    public void setReservationsNonAffectees(List<ReservationAffecteeDTO> reservationsNonAffectees) {
        this.reservationsNonAffectees = reservationsNonAffectees;
    }

    public double getDistanceTotaleJour() {
        return distanceTotaleJour;
    }

    public void setDistanceTotaleJour(double distanceTotaleJour) {
        this.distanceTotaleJour = distanceTotaleJour;
    }

    public int getTotalPassagers() {
        return totalPassagers;
    }

    public void setTotalPassagers(int totalPassagers) {
        this.totalPassagers = totalPassagers;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }
}