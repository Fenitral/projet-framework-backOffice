package com.cousin.model;

import java.time.LocalDateTime;

public class Assignation {
    private int assignationId;
    private int reservationId;
    private int clientId;
    private int regroupementId;
    private int vehiculeId;
    private LocalDateTime assignedDate;

    public Assignation() {
    }

    public Assignation(int reservationId, int clientId, int vehiculeId, LocalDateTime assignedDate) {
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.vehiculeId = vehiculeId;
        this.assignedDate = assignedDate;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    // Getters et Setters
    public int getAssignationId() {
        return assignationId;
    }

    public void setAssignationId(int assignationId) {
        this.assignationId = assignationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getRegroupementId() {
        return regroupementId;
    }

    public void setRegroupementId(int regroupementId) {
        this.regroupementId = regroupementId;
    }

    public int getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(int vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }
}