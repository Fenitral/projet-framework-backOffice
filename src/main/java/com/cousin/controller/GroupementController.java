package com.cousin.controller;

import com.cousin.dto.GroupementDTO;
import com.cousin.service.AssignationService;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.Param;
import com.framework.model.ModelView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPRINT 5 : Contrôleur pour la fonctionnalité "Groupement des voitures".
 *
 * Flux :
 *   GET /groupement              → formulaire de saisie de date
 *   GET /groupementResultats     → page d'affichage des groupements
 */
@Controller
public class GroupementController {

    private final AssignationService assignationService = new AssignationService();

    /**
     * Affiche le formulaire de saisie de la date.
     *
     * GET /groupement
     */
    @GetMapping("/groupement")
    public ModelView showGroupementForm() {
        ModelView mv = new ModelView("/WEB-INF/views/groupement.jsp");
        mv.addAttribute("today", LocalDate.now().toString());
        return mv;
    }

    /**
     * Calcule et affiche les groupements de réservations pour la date choisie.
     *
     * GET /groupementResultats?dateStr=2026-03-16
     */
    @GetMapping("/groupementResultats")
    public ModelView showGroupementResultats(
            @Param("dateStr") String dateStr,
            @Param("heureStr") String heureStr) throws SQLException {

        LocalDate date;
        if (dateStr != null && !dateStr.isBlank()) {
            date = LocalDate.parse(dateStr);
        } else {
            date = LocalDate.now();
        }

        List<GroupementDTO> groupements = assignationService.planifierParGroupements(date);

        LocalTime heureFiltre = null;
        if (heureStr != null && !heureStr.isBlank()) {
            heureFiltre = LocalTime.parse(heureStr);
            LocalDateTime dateHeureFiltre = LocalDateTime.of(date, heureFiltre);
            groupements = groupements.stream()
                .filter(groupement -> groupement.getFenetreDebut() != null
                    && groupement.getFenetreFin() != null
                    && !dateHeureFiltre.isBefore(groupement.getFenetreDebut())
                    && !dateHeureFiltre.isAfter(groupement.getFenetreFin()))
                .collect(Collectors.toList());
        }

        ModelView mv = new ModelView("/WEB-INF/views/groupementResultats.jsp");
        mv.addAttribute("groupements", groupements);
        mv.addAttribute("datePlanification", date.toString());
        mv.addAttribute("heureFiltre", heureFiltre != null ? heureFiltre.toString() : "");
        return mv;
    }
}
