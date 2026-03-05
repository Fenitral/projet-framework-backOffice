package com.cousin.model;

public class Unite {
    private int uniteId;
    private String nomUnite;

    public Unite() {
    }

    public Unite(int uniteId, String nomUnite) {
        this.uniteId = uniteId;
        this.nomUnite = nomUnite;
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