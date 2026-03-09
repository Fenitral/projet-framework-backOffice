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
    private double distance;
    private Integer clientId;
    private String clientName;
    private String clientEmail;

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}
    
