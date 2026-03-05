package com.cousin.service;

import com.cousin.model.Distance;
import com.cousin.repository.DistanceRepository;

import java.sql.SQLException;
import java.util.List;

public class DistanceService {

    private final DistanceRepository distanceRepository;

    public DistanceService() {
        this.distanceRepository = new DistanceRepository();
    }

    public DistanceService(DistanceRepository distanceRepository) {
        this.distanceRepository = distanceRepository;
    }

    /**
     * Récupère la distance entre deux lieux (hôtels ou aéroport).
     * L'aéroport peut être représenté par un ID spécial (ex: 0 ou -1).
     */
    public int getDistance(int lieuDepartId, int lieuArriveeId) throws SQLException {
        return distanceRepository.getDistanceValue(lieuDepartId, lieuArriveeId);
    }

    /**
     * Liste toutes les distances.
     */
    public List<Distance> getAllDistances() throws SQLException {
        return distanceRepository.findAll();
    }

    /**
     * Calcule la distance totale d'un trajet.
     * Le trajet est défini par une liste d'IDs de lieux (dans l'ordre de visite).
     * 
     * @param lieuIds Liste ordonnée des IDs (commence par aéroport, finit par aéroport)
     * @return Distance totale en km
     */
    public int calculerDistanceTotaleTrajet(List<Integer> lieuIds) throws SQLException {
        if (lieuIds == null || lieuIds.size() < 2) {
            return 0;
        }

        int distanceTotale = 0;
        for (int i = 0; i < lieuIds.size() - 1; i++) {
            int from = lieuIds.get(i);
            int to = lieuIds.get(i + 1);
            distanceTotale += getDistance(from, to);
        }
        return distanceTotale;
    }

    /**
     * Ajoute une nouvelle distance.
     */
    public void createDistance(Distance distance) throws SQLException {
        distanceRepository.insert(distance);
    }
}