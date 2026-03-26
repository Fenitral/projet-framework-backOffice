package com.cousin.model;

public class Vehicule {
    private int idVehicule;
    private String reference;
    private int nbPlace;
    private String typeVehicule;
    private String statut;           // DISPONIBLE, ASSIGNE, MAINTENANCE
    private String lieuActuel;       // Localisation actuelle du véhicule
    // Ajout pour gestion dynamique de l'affectation
    private int placesDisponibles;
    private int nbTrajets;

    // Heure de disponibilité (format HH:mm:ss)
    private String heureDisponibilite = "00:00:00";

    public Vehicule() {
    }

    public Vehicule(int idVehicule, String reference, int nbPlace, String typeVehicule) {
        this.idVehicule = idVehicule;
        this.reference = reference;
        this.nbPlace = nbPlace;
        this.typeVehicule = typeVehicule;
        this.statut = "DISPONIBLE"; // Par défaut
        this.placesDisponibles = nbPlace;
        this.nbTrajets = 0;
        this.heureDisponibilite = "00:00:00";
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }
    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public int getNbTrajets() {
        return nbTrajets;
    }
    public void setNbTrajets(int nbTrajets) {
        this.nbTrajets = nbTrajets;
    }

    public String getHeureDisponibilite() {
        return heureDisponibilite;
    }

    public void setHeureDisponibilite(String heureDisponibilite) {
        this.heureDisponibilite = heureDisponibilite;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbPlace(int nbPlace) {
        this.nbPlace = nbPlace;
    }

    public String getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(String typeVehicule) {
        this.typeVehicule = typeVehicule;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getLieuActuel() {
        return lieuActuel;
    }

    public void setLieuActuel(String lieuActuel) {
        this.lieuActuel = lieuActuel;
    }
}
