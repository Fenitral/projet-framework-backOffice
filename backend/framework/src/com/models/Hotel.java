package com.models;

public class Hotel {
    private int hotelId;
    private String name;
    private String addresse;
    private String phone;

    public Hotel() {
    }

    public Hotel(int hotelId, String name, String addresse, String phone) {
        this.hotelId = hotelId;
        this.name = name;
        this.addresse = addresse;
        this.phone = phone;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
