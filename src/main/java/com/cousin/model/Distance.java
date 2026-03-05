package com.cousin.model;

public class Distance {
    private int distanceId;
    private int idHotelFrom;
    private int idHotelTo;
    private int valeur;

    public Distance() {
    }

    public Distance(int distanceId, int idHotelFrom, int idHotelTo, int valeur) {
        this.distanceId = distanceId;
        this.idHotelFrom = idHotelFrom;
        this.idHotelTo = idHotelTo;
        this.valeur = valeur;
    }

    // Getters et Setters
    public int getDistanceId() {
        return distanceId;
    }

    public void setDistanceId(int distanceId) {
        this.distanceId = distanceId;
    }

    public int getIdHotelFrom() {
        return idHotelFrom;
    }

    public void setIdHotelFrom(int idHotelFrom) {
        this.idHotelFrom = idHotelFrom;
    }

    public int getIdHotelTo() {
        return idHotelTo;
    }

    public void setIdHotelTo(int idHotelTo) {
        this.idHotelTo = idHotelTo;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
}