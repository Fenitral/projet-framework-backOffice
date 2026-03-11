package com.cousin.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrajetVehiculeDTO {
    private int vehiculeId;
    private String vehiculeReference;
    private String typeVehicule;
    private int capacite;
    private List<ReservationAffecteeDTO> listeReservations;
    private double distanceTotale;       // Distance totale avec retour aéroport
    private double distanceParcourue;    // Distance parcourue sans retour (somme des distances depuis précédent)
    private LocalDateTime heureDepart;
    private LocalDateTime heureRetourPrevue;
    private int placesUtilisees;

    public TrajetVehiculeDTO() {
        this.listeReservations = new ArrayList<>();
        this.distanceTotale = 0;
        this.distanceParcourue = 0;
        this.placesUtilisees = 0;
    }

    // Getters et Setters
    public int getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(int vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public String getVehiculeReference() {
        return vehiculeReference;
    }

    public void setVehiculeReference(String vehiculeReference) {
        this.vehiculeReference = vehiculeReference;
    }

    public String getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(String typeVehicule) {
        this.typeVehicule = typeVehicule;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public List<ReservationAffecteeDTO> getListeReservations() {
        return listeReservations;
    }

    public void setListeReservations(List<ReservationAffecteeDTO> listeReservations) {
        this.listeReservations = listeReservations;
    }

    public double getDistanceTotale() {
        return distanceTotale;
    }

    public void setDistanceTotale(double distanceTotale) {
        this.distanceTotale = distanceTotale;
    }

    public double getDistanceParcourue() {
        return distanceParcourue;
    }

    public void setDistanceParcourue(double distanceParcourue) {
        this.distanceParcourue = distanceParcourue;
    }

    public LocalDateTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalDateTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalDateTime getHeureRetourPrevue() {
        return heureRetourPrevue;
    }

    public void setHeureRetourPrevue(LocalDateTime heureRetourPrevue) {
        this.heureRetourPrevue = heureRetourPrevue;
    }

    public int getPlacesUtilisees() {
        return placesUtilisees;
    }

    public void setPlacesUtilisees(int placesUtilisees) {
        this.placesUtilisees = placesUtilisees;
    }

    public int getPlacesDisponibles() {
        return capacite - placesUtilisees;
    }

    public void addReservation(ReservationAffecteeDTO reservation) {
        this.listeReservations.add(reservation);
        this.placesUtilisees += reservation.getNbPassager();
    }
}