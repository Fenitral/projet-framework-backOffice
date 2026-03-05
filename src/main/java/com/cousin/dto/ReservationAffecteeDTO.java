package com.cousin.dto;

import java.time.LocalDateTime;

public class ReservationAffecteeDTO {
    private int idReservation;
    private LocalDateTime dateHeureArrive;
    private String idClient;
    private int nbPassager;
    private int idHotel;
    private String nomHotel;
    private int ordreVisite;
    private double distanceDepuisPrecedent;

    public ReservationAffecteeDTO() {
    }

    // Getters et Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDateTime getDateHeureArrive() {
        return dateHeureArrive;
    }

    public void setDateHeureArrive(LocalDateTime dateHeureArrive) {
        this.dateHeureArrive = dateHeureArrive;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public int getNbPassager() {
        return nbPassager;
    }

    public void setNbPassager(int nbPassager) {
        this.nbPassager = nbPassager;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public String getNomHotel() {
        return nomHotel;
    }

    public void setNomHotel(String nomHotel) {
        this.nomHotel = nomHotel;
    }

    public int getOrdreVisite() {
        return ordreVisite;
    }

    public void setOrdreVisite(int ordreVisite) {
        this.ordreVisite = ordreVisite;
    }

    public double getDistanceDepuisPrecedent() {
        return distanceDepuisPrecedent;
    }

    public void setDistanceDepuisPrecedent(double distanceDepuisPrecedent) {
        this.distanceDepuisPrecedent = distanceDepuisPrecedent;
    }
}