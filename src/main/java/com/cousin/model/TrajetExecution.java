package com.cousin.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class TrajetExecution {
    
    private Integer trajetId;
    private Integer vehiculeId;
    private LocalDate dateService;
    private LocalDateTime heureDepart;
    private LocalDateTime heureRetour;
    private BigDecimal distanceTotale;
    private Integer nombrePassagers;
    private LocalDateTime createdAt;

    // Constructeurs
    public TrajetExecution() {
    }

    public TrajetExecution(Integer vehiculeId, LocalDate dateService, 
                          LocalDateTime heureDepart, LocalDateTime heureRetour,
                          BigDecimal distanceTotale, Integer nombrePassagers) {
        this.vehiculeId = vehiculeId;
        this.dateService = dateService;
        this.heureDepart = heureDepart;
        this.heureRetour = heureRetour;
        this.distanceTotale = distanceTotale;
        this.nombrePassagers = nombrePassagers;
    }

    // Getters et Setters
    public Integer getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(Integer trajetId) {
        this.trajetId = trajetId;
    }

    public Integer getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Integer vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public LocalDate getDateService() {
        return dateService;
    }

    public void setDateService(LocalDate dateService) {
        this.dateService = dateService;
    }

    public LocalDateTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalDateTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalDateTime getHeureRetour() {
        return heureRetour;
    }

    public void setHeureRetour(LocalDateTime heureRetour) {
        this.heureRetour = heureRetour;
    }

    public BigDecimal getDistanceTotale() {
        return distanceTotale;
    }

    public void setDistanceTotale(BigDecimal distanceTotale) {
        this.distanceTotale = distanceTotale;
    }

    public Integer getNombrePassagers() {
        return nombrePassagers;
    }

    public void setNombrePassagers(Integer nombrePassagers) {
        this.nombrePassagers = nombrePassagers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TrajetExecution{" +
                "trajetId=" + trajetId +
                ", vehiculeId=" + vehiculeId +
                ", dateService=" + dateService +
                ", heureDepart=" + heureDepart +
                ", heureRetour=" + heureRetour +
                ", distanceTotale=" + distanceTotale +
                ", nombrePassagers=" + nombrePassagers +
                ", createdAt=" + createdAt +
                '}';
    }
}
