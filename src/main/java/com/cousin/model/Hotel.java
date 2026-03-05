package com.cousin.model;

public class Hotel {
    private int idHotel;
    private String nom;
    private String aeroport;

    public Hotel() {
    }

    public Hotel(int idHotel, String nom) {
        this.idHotel = idHotel;
        this.nom = nom;
    }

    public Hotel(int idHotel, String nom, String aeroport) {
        this.idHotel = idHotel;
        this.nom = nom;
        this.aeroport = aeroport;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAeroport() {
        return aeroport;
    }

    public void setAeroport(String aeroport) {
        this.aeroport = aeroport;
    }
}
