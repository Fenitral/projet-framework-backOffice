package com.cousin.model;

import java.time.LocalDateTime;

public class Reservation {
    private int idReservation;
    private LocalDateTime dateHeureArrive;
    private String idClient;
    private int nbPassager;
    private Hotel hotel;
    private Integer clientId;
    private Client client;
    private String statut; // PENDING, ASSIGNED, CANCELLED

    public Reservation() {
    }

    public Reservation(int idReservation, LocalDateTime dateHeureArrive, String idClient, int nbPassager, Hotel hotel) {
        this.idReservation = idReservation;
        this.dateHeureArrive = dateHeureArrive;
        this.idClient = idClient;
        this.nbPassager = nbPassager;
        this.hotel = hotel;
    }

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

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", dateHeureArrive=" + dateHeureArrive +
                ", idClient='" + idClient + '\'' +
                ", nbPassager=" + nbPassager +
                ", statut='" + statut + '\'' +
                '}';
    }
}
