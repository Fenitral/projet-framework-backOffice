// filepath: src/main/java/com/cousin/controller/AssignationController.java
package com.cousin.controller;

import com.cousin.dto.AllocationReservationDTO;
import com.cousin.dto.PlanificationDTO;
import com.cousin.model.Assignation;
import com.cousin.model.Reservation;
import com.cousin.model.Vehicule;
import com.cousin.service.AssignationService;
import com.cousin.service.ReservationAllocationService;
import com.framework.annotation.*;

import com.framework.model.ModelView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AssignationController {

    private final AssignationService assignationService = new AssignationService();
    private final ReservationAllocationService reservationAllocationService = new ReservationAllocationService();

    private List<Vehicule> cloneVehiculesForPreview(List<Vehicule> vehicules) {
        List<Vehicule> copies = new ArrayList<>();
        if (vehicules == null) {
            return copies;
        }

        for (Vehicule source : vehicules) {
            Vehicule copy = new Vehicule();
            copy.setIdVehicule(source.getIdVehicule());
            copy.setReference(source.getReference());
            copy.setNbPlace(source.getNbPlace());
            copy.setTypeVehicule(source.getTypeVehicule());
            copy.setStatut(source.getStatut());
            copy.setLieuActuel(source.getLieuActuel());
            copy.setNbTrajets(source.getNbTrajets());
            copy.setPlacesDisponibles(source.getNbPlace());
            copy.setHeureDisponibilite(source.getHeureDisponibilite());
            copies.add(copy);
        }

        return copies;
    }

    private Map<Integer, Integer> buildReservationPriorityMap(LocalDate date) throws SQLException {
        Map<Integer, Integer> priorityByReservationId = new HashMap<>();

        List<Reservation> reservations = assignationService.getReservationsByDate(date);
        List<Vehicule> vehicules = assignationService.getAllVehicules();

        List<AllocationReservationDTO> allocations =
                reservationAllocationService.prepareReservationsForAllocation(date, new ArrayList<>(reservations));
        reservationAllocationService.allocatePassengersToVehicles(allocations, cloneVehiculesForPreview(vehicules), new ArrayList<>(reservations));

        for (AllocationReservationDTO allocation : allocations) {
            if (allocation.getReservationId() != null) {
                priorityByReservationId.put(allocation.getReservationId().intValue(), allocation.getPrioriteClient());
            }
        }

        return priorityByReservationId;
    }

    /**
     * Affiche la page de formulaire de planification.
     * 
     * GET /planification
     */
    @GetMapping("/planification")
    public ModelView showPlanificationForm() {
        ModelView mv = new ModelView("/WEB-INF/views/planification.jsp");
        mv.addAttribute("today", LocalDate.now().toString());
        return mv;
    }

    /**
     * Génère et affiche les résultats de la planification.
     * 
     * GET /affichageResultats?dateStr=2026-03-05&heureDepart=08:00
     */
    @GetMapping("/affichageResultats")
    public ModelView showAffichageResultats(
            @Param("dateStr") String dateStr,
            @Param("heureDepart") String heureStr) throws SQLException {
        
        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }

        LocalTime heure;
        if (heureStr != null && !heureStr.isBlank()) {
            heure = LocalTime.parse(heureStr);
        } else {
            heure = LocalTime.of(8, 0);
        }

        LocalDateTime heureDepart = LocalDateTime.of(date, heure);

        // Générer la planification
        PlanificationDTO planification = assignationService.planifier(date, heureDepart);

        ModelView mv = new ModelView("/WEB-INF/views/affichageResultats.jsp");
        mv.addAttribute("planification", planification);
        mv.addAttribute("datePlanification", date.toString());
        mv.addAttribute("heureDepart", heure.toString());
        mv.addAttribute("reservationPriorityMap", buildReservationPriorityMap(date));
        return mv;
    }

    /**
     * Génère la planification d'une journée.
     * 
     * GET /api/planification?date=2026-03-05&heureDepart=08:00
     */
    @GetMapping("/api/planification")
    @Json
    public PlanificationDTO getPlanification(
            @Param("date") String dateStr,
            @Param("heureDepart") String heureStr) throws SQLException {
        
        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }

        LocalTime heure;
        if (heureStr != null && !heureStr.isBlank()) {
            heure = LocalTime.parse(heureStr);
        } else {
            heure = LocalTime.of(8, 0);
        }

        LocalDateTime heureDepart = LocalDateTime.of(date, heure);

        return assignationService.planifier(date, heureDepart);
    }

    /**
     * Récupère les réservations d'une date.
     * 
     * GET /api/reservations?date=2026-03-05
     */
    @GetMapping("/api/reservations")
    @Json
    public List<Reservation> getReservations(@Param("date") String dateStr) throws SQLException {
        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }
        return assignationService.getReservationsByDate(date);
    }

    /**
     * Récupère tous les véhicules.
     * 
     * GET /api/vehicules
     */
    @GetMapping("/api/vehicules")
    @Json
    public List<Vehicule> getVehicules() throws SQLException {
        return assignationService.getAllVehicules();
    }

    /**
     * Récupère les assignations d'une date.
     * 
     * GET /api/assignations?date=2026-03-05
     */
    @GetMapping("/api/assignations")
    @Json
    public List<Assignation> getAssignations(@Param("date") String dateStr) throws SQLException {
        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }
        return assignationService.getAssignationsByDate(date);
    }

    /**
     * Génère ET sauvegarde la planification d'une journée.
     * Reste sur la page de résultats avec un message de succès.
     * 
     * GET /planification/save?date=2026-03-05&heureDepart=08:00
     */
    @GetMapping("/planification/save")
    public ModelView savePlanification(
            @Param("date") String dateStr,
            @Param("heureDepart") String heureStr) throws SQLException {
        
        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }

        LocalTime heure;
        if (heureStr != null && !heureStr.isBlank()) {
            heure = LocalTime.parse(heureStr);
        } else {
            heure = LocalTime.of(8, 0);
        }

        LocalDateTime heureDepart = LocalDateTime.of(date, heure);

        // Générer la planification
        PlanificationDTO planification = assignationService.planifier(date, heureDepart);
        
        // Sauvegarder
        assignationService.sauvegarderPlanification(planification);
        
        // Retourner à la page de résultats avec un message de succès
        ModelView mv = new ModelView("/WEB-INF/views/affichageResultats.jsp");
        mv.addAttribute("planification", planification);
        mv.addAttribute("datePlanification", date.toString());
        mv.addAttribute("heureDepart", heure.toString());
        mv.addAttribute("successMessage", "Planification sauvegardée avec succès !");
        mv.addAttribute("reservationPriorityMap", buildReservationPriorityMap(date));
        return mv;
    }
}