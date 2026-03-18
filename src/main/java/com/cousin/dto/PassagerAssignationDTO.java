package com.cousin.dto;

public class PassagerAssignationDTO {
    private Long reservationId;
    private int nombrePassagers;
    private int positionVisite;
    private String lieuVisite;

    // Getters and setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public int getNombrePassagers() { return nombrePassagers; }
    public void setNombrePassagers(int nombrePassagers) { this.nombrePassagers = nombrePassagers; }
    public int getPositionVisite() { return positionVisite; }
    public void setPositionVisite(int positionVisite) { this.positionVisite = positionVisite; }
    public String getLieuVisite() { return lieuVisite; }
    public void setLieuVisite(String lieuVisite) { this.lieuVisite = lieuVisite; }
}
