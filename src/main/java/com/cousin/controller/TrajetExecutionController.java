package com.cousin.controller;

import com.cousin.model.TrajetExecution;
import com.cousin.service.TrajetExecutionService;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.Param;
import com.framework.model.ModelView;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TrajetExecutionController {
    private final TrajetExecutionService trajetExecutionService = new TrajetExecutionService();

    @GetMapping("/trajet/list")
    public ModelView listTrajets(
            @Param("date") String dateStr) throws SQLException {
        
        ModelView mv = new ModelView("/WEB-INF/views/trajet/list.jsp");
        
        try {
            if (dateStr != null && !dateStr.isBlank()) {
                LocalDate date = LocalDate.parse(dateStr);
                List<TrajetExecution> trajets = trajetExecutionService.getTrajetsByDate(date);
                mv.addAttribute("trajets", trajets);
                mv.addAttribute("selectedDate", date);
            } else {
                LocalDate today = LocalDate.now();
                List<TrajetExecution> trajets = trajetExecutionService.getTrajetsByDate(today);
                mv.addAttribute("trajets", trajets);
                mv.addAttribute("selectedDate", today);
            }
        } catch (Exception e) {
            mv.addAttribute("error", "Erreur lors du chargement des trajets: " + e.getMessage());
        }
        
        return mv;
    }

    @GetMapping("/trajet/stats")
    public ModelView getTrajetStats(
            @Param("vehicule_id") Integer vehiculeId,
            @Param("date") String dateStr) throws SQLException {
        
        ModelView mv = new ModelView("/WEB-INF/views/trajet/stats.jsp");
        
        try {
            if (vehiculeId != null && dateStr != null && !dateStr.isBlank()) {
                LocalDate date = LocalDate.parse(dateStr);
                int count = trajetExecutionService.getCountTrajetsByVehicleAndDate(vehiculeId, date);
                List<TrajetExecution> trajets = trajetExecutionService.getTrajetsByVehicleAndDate(vehiculeId, date);
                
                mv.addAttribute("vehiculeId", vehiculeId);
                mv.addAttribute("date", date);
                mv.addAttribute("count", count);
                mv.addAttribute("trajets", trajets);
            }
        } catch (Exception e) {
            mv.addAttribute("error", "Erreur lors du chargement des statistiques: " + e.getMessage());
        }
        
        return mv;
    }

    @GetMapping("/api/trajets")
    public ModelView apiGetTrajets(
            @Param("date") String dateStr) throws SQLException {
        
        ModelView mv = new ModelView("/WEB-INF/views/trajet/list.jsp");
        
        try {
            if (dateStr != null && !dateStr.isBlank()) {
                LocalDate date = LocalDate.parse(dateStr);
                List<TrajetExecution> trajets = trajetExecutionService.getTrajetsByDate(date);
                mv.addAttribute("trajets", trajets);
                mv.addAttribute("success", true);
            } else {
                mv.addAttribute("success", false);
                mv.addAttribute("message", "Date requise");
            }
        } catch (Exception e) {
            mv.addAttribute("success", false);
            mv.addAttribute("message", e.getMessage());
        }
        
        return mv;
    }

    @GetMapping("/api/trajets/vehicule")
    public ModelView apiGetTrajetsByVehicule(
            @Param("vehicule_id") Integer vehiculeId,
            @Param("date") String dateStr) throws SQLException {
        
        ModelView mv = new ModelView("/WEB-INF/views/trajet/list.jsp");
        
        try {
            if (vehiculeId != null && dateStr != null && !dateStr.isBlank()) {
                LocalDate date = LocalDate.parse(dateStr);
                List<TrajetExecution> trajets = trajetExecutionService.getTrajetsByVehicleAndDate(vehiculeId, date);
                int count = trajetExecutionService.getCountTrajetsByVehicleAndDate(vehiculeId, date);
                
                mv.addAttribute("trajets", trajets);
                mv.addAttribute("count", count);
                mv.addAttribute("success", true);
            } else {
                mv.addAttribute("success", false);
                mv.addAttribute("message", "vehicule_id et date requis");
            }
        } catch (Exception e) {
            mv.addAttribute("success", false);
            mv.addAttribute("message", e.getMessage());
        }
        
        return mv;
    }
}
