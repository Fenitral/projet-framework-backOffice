package com.models;

import java.sql.Timestamp;

public class Reservation {
    private int reservationId;
    private String client;
    private int nbPeople;
    private Timestamp dateheure;
    private int hotelId;
    private Hotel hotel;

    public Reservation() {
    }

    public Reservation(int reservationId, String client, int nbPeople, Timestamp dateheure, int hotelId) {
        this.reservationId = reservationId;
        this.client = client;
        this.nbPeople = nbPeople;
        this.dateheure = dateheure;
        this.hotelId = hotelId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getNbPeople() {
        return nbPeople;
    }

    public void setNbPeople(int nbPeople) {
        this.nbPeople = nbPeople;
    }

    public Timestamp getDateheure() {
        return dateheure;
    }

    public void setDateheure(Timestamp dateheure) {
        this.dateheure = dateheure;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
