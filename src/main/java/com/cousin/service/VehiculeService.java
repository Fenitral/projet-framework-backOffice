package com.cousin.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cousin.model.Vehicule;
import com.cousin.model.TrajetExecution;
import com.cousin.repository.VehiculeRepository;
import com.cousin.repository.TrajetExecutionRepository;


public class VehiculeService {
    private final VehiculeRepository vehiculeRepository;
    private final TrajetExecutionRepository trajetRepository;
    
    private final java.util.Map<Integer, java.time.LocalDateTime> vehiculeReturnTimes = new java.util.HashMap<>();

    public VehiculeService() {
        this.vehiculeRepository = new VehiculeRepository();
        this.trajetRepository = new TrajetExecutionRepository();
    }

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.trajetRepository = new TrajetExecutionRepository();
    }

    public void createVehicule(Vehicule vehicule) throws SQLException {
        vehiculeRepository.insert(vehicule);
    }

    public void updateVehicule(Vehicule vehicule) throws SQLException {
        vehiculeRepository.update(vehicule);
    }

    public void deleteVehicule(int idVehicule) throws SQLException {
        vehiculeRepository.deleteById(idVehicule);
    }

    public Vehicule getVehicule(int idVehicule) throws SQLException {
        return vehiculeRepository.findById(idVehicule);
    }

    public List<Vehicule> listVehicules() throws SQLException {
        return vehiculeRepository.findAll();
    }

    public List<Vehicule> getAvailableVehicles(LocalDate date) throws SQLException {
        List<Vehicule> allVehicules = vehiculeRepository.findAll();
        List<Vehicule> available = new ArrayList<>();
        
        for (Vehicule v : allVehicules) {
            if ("DISPONIBLE".equalsIgnoreCase(v.getStatut())) {
                available.add(v);
            }
        }
        
        return available;
    }

    public int getVehicleTrajetCount(Integer vehiculeId, LocalDate dateService) throws SQLException {
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService sont obligatoires");
        }
        return trajetRepository.countTrajetsByVehicleAndDate(vehiculeId, dateService);
    }

    public Vehicule getBestAvailableVehicle(LocalDate date) throws SQLException {
        List<Vehicule> candidates = getAvailableVehicles(date);
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        List<VehiculeCharge> charges = new ArrayList<>();
        for (Vehicule v : candidates) {
            int count = getVehicleTrajetCount(v.getIdVehicule(), date);
            charges.add(new VehiculeCharge(v, count));
        }
        
        charges.sort((a, b) -> {
            if (a.trajetCount != b.trajetCount) {
                return Integer.compare(a.trajetCount, b.trajetCount);
            }
            return Integer.compare(a.vehicule.getIdVehicule(), b.vehicule.getIdVehicule());
        });
        
        return charges.get(0).vehicule;
    }

    public List<Vehicule> getAvailableVehiculesSortedByLoad(LocalDate date) throws SQLException {
        List<Vehicule> candidates = getAvailableVehicles(date);
        
        List<VehiculeCharge> charges = new ArrayList<>();
        for (Vehicule v : candidates) {
            int count = getVehicleTrajetCount(v.getIdVehicule(), date);
            charges.add(new VehiculeCharge(v, count));
        }
        
        charges.sort((a, b) -> Integer.compare(a.trajetCount, b.trajetCount));
        
        List<Vehicule> sorted = new ArrayList<>();
        for (VehiculeCharge vc : charges) {
            sorted.add(vc.vehicule);
        }
        return sorted;
    }

    private static class VehiculeCharge {
        Vehicule vehicule;
        int trajetCount;
        
        VehiculeCharge(Vehicule vehicule, int trajetCount) {
            this.vehicule = vehicule;
            this.trajetCount = trajetCount;
        }
    }

    public boolean isVehiculeAvailable(Integer vehiculeId) throws SQLException {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
        return vehicule != null && "DISPONIBLE".equalsIgnoreCase(vehicule.getStatut());
    }

    public String getVehicleLoadSummary(LocalDate date) throws SQLException {
        List<Vehicule> allVehicules = vehiculeRepository.findAll();
        StringBuilder summary = new StringBuilder("Charge des véhicules pour " + date + ":\n");
        
        for (Vehicule v : allVehicules) {
            int count = getVehicleTrajetCount(v.getIdVehicule(), date);
            String status = v.getStatut();
            summary.append(String.format("  Véhicule %d (%s): %d trajets\n", 
                v.getIdVehicule(), status, count));
        }
        
        return summary.toString();
    }

    public boolean isVehiculeAvailableAtTime(Integer vehiculeId, java.time.LocalDateTime heureDebut) throws SQLException {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId);
        if (vehicule == null || !"DISPONIBLE".equalsIgnoreCase(vehicule.getStatut())) {
            return false;
        }
        
        java.time.LocalDateTime heureRetour = vehiculeReturnTimes.get(vehiculeId);
        
        if (heureRetour == null) {
            return true;
        }
        
        return heureRetour.isBefore(heureDebut) || heureRetour.equals(heureDebut);
    }

    public void recordTrajet(Integer vehiculeId, java.time.LocalDate dateService, 
                           java.time.LocalDateTime heureDepart, 
                           java.time.LocalDateTime heureRetour,
                           java.math.BigDecimal distanceTotale,
                           Integer nombrePassagers) throws SQLException {
        
        if (vehiculeId == null || dateService == null) {
            throw new SQLException("vehiculeId et dateService obligatoires");
        }
        
        TrajetExecution trajet = new TrajetExecution();
        trajet.setVehiculeId(vehiculeId);
        trajet.setDateService(dateService);
        trajet.setHeureDepart(heureDepart);
        trajet.setHeureRetour(heureRetour);
        trajet.setDistanceTotale(distanceTotale);
        trajet.setNombrePassagers(nombrePassagers);
        
        trajetRepository.insert(trajet);
        
        vehiculeReturnTimes.put(vehiculeId, heureRetour);
    }

    public java.time.LocalDateTime getVehiculeReturnTime(Integer vehiculeId) {
        return vehiculeReturnTimes.get(vehiculeId);
    }

    public void initializeDaySchedule(java.time.LocalDate date) {
        vehiculeReturnTimes.clear();
    }

    public List<Vehicule> filterAndSortByLoad(List<Vehicule> candidats, 
                                              int nbPassagers,
                                              java.time.LocalDateTime heureDebut,
                                              java.time.LocalDate dateService) throws SQLException {
        List<Vehicule> eligible = new java.util.ArrayList<>();
        
        for (Vehicule v : candidats) {
            if (v.getNbPlace() < nbPassagers) {
                continue;
            }
            
            if (!isVehiculeAvailableAtTime(v.getIdVehicule(), heureDebut)) {
                continue;
            }
            
            eligible.add(v);
        }
        
        if (eligible.isEmpty()) {
            return eligible;
        }
        
        eligible.sort((v1, v2) -> {
            try {
                int count1 = trajetRepository.countTrajetsByVehicleAndDate(v1.getIdVehicule(), dateService);
                int count2 = trajetRepository.countTrajetsByVehicleAndDate(v2.getIdVehicule(), dateService);
                
                if (count1 != count2) {
                    return Integer.compare(count1, count2);
                }
                
                return Integer.compare(v1.getIdVehicule(), v2.getIdVehicule());
                
            } catch (SQLException e) {
                return 0;
            }
        });
        
        return eligible;
    }

    public Vehicule selectBestVehicle(List<Vehicule> candidats,
                                     int nbPassagers,
                                     java.time.LocalDateTime heureDebut,
                                     java.time.LocalDate dateService) throws SQLException {
        
        List<Vehicule> sorted = filterAndSortByLoad(candidats, nbPassagers, heureDebut, dateService);
        
        if (sorted.isEmpty()) {
            return null;
        }
        
        return sorted.get(0);
    }

    public void resetDaySchedule() {
        vehiculeReturnTimes.clear();
    }
}
