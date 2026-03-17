package com.cousin.service;

import com.cousin.model.TrajetExecution;
import com.cousin.repository.TrajetExecutionRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;


public class TrajetExecutionService {
    
    private TrajetExecutionRepository trajetRepository;
    
    public TrajetExecutionService() {
        this.trajetRepository = new TrajetExecutionRepository();
    }

    public int createTrajet(Integer vehiculeId, LocalDate dateService, 
                           LocalDateTime heureDepart, LocalDateTime heureRetour,
                           BigDecimal distanceTotale, Integer nombrePassagers) throws SQLException {
        
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        
        TrajetExecution trajet = new TrajetExecution(vehiculeId, dateService, heureDepart, heureRetour, distanceTotale, nombrePassagers);
        return trajetRepository.insert(trajet);
    }

 
    public int getCountTrajetsByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        return trajetRepository.countTrajetsByVehicleAndDate(vehiculeId, dateService);
    }

    /**
     * Récupère tous les trajets d'un véhicule pour une date donnée.
     */
    public List<TrajetExecution> getTrajetsByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        return trajetRepository.findByVehicleAndDate(vehiculeId, dateService);
    }

    /**
     * Récupère tous les trajets pour une date donnée.
     */
    public List<TrajetExecution> getTrajetsByDate(LocalDate dateService) throws SQLException {
        if (dateService == null) {
            throw new SQLException("dateService est obligatoire");
        }
        return trajetRepository.findByDate(dateService);
    }

    /**
     * Récupère un trajet par son ID.
     */
    public TrajetExecution getTrajetById(Integer trajetId) throws SQLException {
        if (trajetId == null) {
            throw new SQLException("trajetId est obligatoire");
        }
        return trajetRepository.findById(trajetId);
    }

    /**
     * Supprime un trajet par son ID.
     */
    public void deleteTrajet(Integer trajetId) throws SQLException {
        if (trajetId == null) {
            throw new SQLException("trajetId est obligatoire");
        }
        trajetRepository.deleteById(trajetId);
    }

    /**
     * Supprime tous les trajets pour une date donnée (RESET TEST).
     */
    public int deleteTrajetsByDate(LocalDate dateService) throws SQLException {
        if (dateService == null) {
            throw new SQLException("dateService est obligatoire");
        }
        return trajetRepository.deleteByDate(dateService);
    }

    /**
     * Supprime tous les trajets pour un véhicule et une date donnée.
     */
    public int deleteTrajetsByVehicleAndDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        return trajetRepository.deleteByVehicleAndDate(vehiculeId, dateService);
    }

    /**
     * Vérifie si un véhicule a dei trajets prévus pour une date donnée.
     */
    public boolean hasTrajetForDate(Integer vehiculeId, LocalDate dateService) throws SQLException {
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        return getCountTrajetsByVehicleAndDate(vehiculeId, dateService) > 0;
    }
}
