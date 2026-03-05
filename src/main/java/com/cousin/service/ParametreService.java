package com.cousin.service;

import com.cousin.model.Parametre;
import com.cousin.repository.ParametreRepository;

import java.sql.SQLException;
import java.util.List;

public class ParametreService {
    
    private static final String VITESSE_MOYENNE = "vitesse_moyenne";
    private static final String TEMPS_ATTENTE_HOTEL = "temps_attente_hotel";
    private static final int VITESSE_DEFAUT = 50; // km/h par défaut
    private static final int TEMPS_ATTENTE_DEFAUT = 10; // minutes par défaut

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
        Parametre p = parametreRepository.findByNom(VITESSE_MOYENNE);
        if (p != null) {
            return p.getValeur();
        }
        return VITESSE_DEFAUT;
    }

    /**
     * Récupère le temps d'attente par hôtel en minutes.
     */
    public int getTempsAttenteHotel() throws SQLException {
        Parametre p = parametreRepository.findByNom(TEMPS_ATTENTE_HOTEL);
        if (p != null) {
            return p.getValeur();
        }
        return TEMPS_ATTENTE_DEFAUT;
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