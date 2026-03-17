package com.cousin.service;

import com.cousin.model.Parametre;
import com.cousin.repository.ParametreRepository;

import java.sql.SQLException;
import java.util.List;

public class ParametreService {
    
    private static final String VITESSE_MOYENNE = "vitesse_moyenne";
    private static final String TEMPS_ATTENTE_GROUPEMENT = "temps_attente_groupement";

    private final ParametreRepository parametreRepository;

    public ParametreService() {
        this.parametreRepository = new ParametreRepository();
    }

    public ParametreService(ParametreRepository parametreRepository) {
        this.parametreRepository = parametreRepository;
    }

    /**
     * Récupère la vitesse moyenne en km/h.
     */
    public int getVitesseMoyenne() throws SQLException {
        return getValeurObligatoire(VITESSE_MOYENNE);
    }

    /**
     * Récupère le temps d'attente de groupement en minutes.
     */
    public int getTempsAttenteGroupement() throws SQLException {
        return getValeurObligatoire(TEMPS_ATTENTE_GROUPEMENT);
    }

    private int getValeurObligatoire(String nomParametre) throws SQLException {
        Parametre parametre = parametreRepository.findByNom(nomParametre);
        if (parametre == null) {
            throw new SQLException("Parametre manquant en base: " + nomParametre);
        }
        return parametre.getValeur();
    }

    /**
     * Met à jour un paramètre.
     */
    public void updateParametre(Parametre parametre) throws SQLException {
        parametreRepository.update(parametre);
    }

    /**
     * Liste tous les paramètres.
     */
    public List<Parametre> getAllParametres() throws SQLException {
        return parametreRepository.findAll();
    }

    /**
     * Crée un nouveau paramètre.
     */
    public void createParametre(Parametre parametre) throws SQLException {
        parametreRepository.insert(parametre);
    }
}