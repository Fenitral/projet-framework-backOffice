package com.cousin.dto;

import java.util.List;

public class AllocationReservationDTO {
    private Long reservationId;
    private int nombrePassagersTotal;
    private int nombrePassagersAssignes;
    private List<PassagerAssignationDTO> listeAffectations;
    private int nombrePassagersNonAssignes;
    private int prioriteClient;

    // Getters and setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public int getNombrePassagersTotal() { return nombrePassagersTotal; }
    public void setNombrePassagersTotal(int nombrePassagersTotal) { this.nombrePassagersTotal = nombrePassagersTotal; }
    public int getNombrePassagersAssignes() { return nombrePassagersAssignes; }
    public void setNombrePassagersAssignes(int nombrePassagersAssignes) { this.nombrePassagersAssignes = nombrePassagersAssignes; }
    public List<PassagerAssignationDTO> getListeAffectations() { return listeAffectations; }
    public void setListeAffectations(List<PassagerAssignationDTO> listeAffectations) { this.listeAffectations = listeAffectations; }
    public int getNombrePassagersNonAssignes() { return nombrePassagersNonAssignes; }
    public void setNombrePassagersNonAssignes(int nombrePassagersNonAssignes) { this.nombrePassagersNonAssignes = nombrePassagersNonAssignes; }
    public int getPrioriteClient() { return prioriteClient; }
    public void setPrioriteClient(int prioriteClient) { this.prioriteClient = prioriteClient; }
}
