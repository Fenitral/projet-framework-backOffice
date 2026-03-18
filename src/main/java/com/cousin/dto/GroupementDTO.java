package com.cousin.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SPRINT 5 : Représente un groupement de réservations planifiées.
 * Un groupement contient toutes les voitures qui partagent la même heure de départ.
 */
public class GroupementDTO {

    private int numeroGroupe;
    private LocalDateTime fenetreDebut;
    private LocalDateTime fenetreFin;
    private LocalDateTime heureDepart;        // Même pour tous les véhicules du groupe
    private List<TrajetVehiculeDTO> trajets;  // Un trajet par véhicule utilisé
    private int totalReservations;
    private int totalPassagers;
    private List<ReservationAffecteeDTO> reservationsNonAffectees;
    private String departInfo;

    public GroupementDTO() {
        this.trajets = new ArrayList<>();
        this.reservationsNonAffectees = new ArrayList<>();
    }

    public int getNumeroGroupe() {
        return numeroGroupe;
    }

    public void setNumeroGroupe(int numeroGroupe) {
        this.numeroGroupe = numeroGroupe;
    }

    public LocalDateTime getFenetreDebut() {
        return fenetreDebut;
    }

    public void setFenetreDebut(LocalDateTime fenetreDebut) {
        this.fenetreDebut = fenetreDebut;
    }

    public LocalDateTime getFenetreFin() {
        return fenetreFin;
    }

    public void setFenetreFin(LocalDateTime fenetreFin) {
        this.fenetreFin = fenetreFin;
    }

    public LocalDateTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalDateTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public List<TrajetVehiculeDTO> getTrajets() {
        return trajets;
    }

    public void setTrajets(List<TrajetVehiculeDTO> trajets) {
        this.trajets = trajets;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public int getTotalPassagers() {
        return totalPassagers;
    }

    public void setTotalPassagers(int totalPassagers) {
        this.totalPassagers = totalPassagers;
    }

    public List<ReservationAffecteeDTO> getReservationsNonAffectees() {
        return reservationsNonAffectees;
    }

    public void setReservationsNonAffectees(List<ReservationAffecteeDTO> reservationsNonAffectees) {
        this.reservationsNonAffectees = reservationsNonAffectees;
    }

    public String getDepartInfo() {
        return departInfo;
    }

    public void setDepartInfo(String departInfo) {
        this.departInfo = departInfo;
    }
}
