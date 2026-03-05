package com.cousin.model;

public class Parametre {
    private int parametreId;
    private String nomParam;
    private int valeur;
    private int uniteId;
    private String nomUnite;

    public Parametre() {
    }

    public Parametre(int parametreId, String nomParam, int valeur, int uniteId) {
        this.parametreId = parametreId;
        this.nomParam = nomParam;
        this.valeur = valeur;
        this.uniteId = uniteId;
    }

    // Getters et Setters
    public int getParametreId() {
        return parametreId;
    }

    public void setParametreId(int parametreId) {
        this.parametreId = parametreId;
    }

    public String getNomParam() {
        return nomParam;
    }

    public void setNomParam(String nomParam) {
        this.nomParam = nomParam;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public int getUniteId() {
        return uniteId;
    }

    public void setUniteId(int uniteId) {
        this.uniteId = uniteId;
    }

    public String getNomUnite() {
        return nomUnite;
    }

    public void setNomUnite(String nomUnite) {
        this.nomUnite = nomUnite;
    }
}