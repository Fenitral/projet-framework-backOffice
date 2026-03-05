package com.cousin.service;

import com.cousin.dto.PlanificationDTO;
import com.cousin.dto.ReservationAffecteeDTO;
import com.cousin.dto.TrajetVehiculeDTO;
import com.cousin.model.Assignation;
import com.cousin.model.Reservation;
import com.cousin.model.Vehicule;
import com.cousin.repository.AssignationRepository;
import com.cousin.repository.ReservationRepository;
import com.cousin.repository.VehiculeRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AssignationService {

    // ID représentant l'aéroport dans la table distance
    private static final int AEROPORT_ID = 0;
    
    // Types de carburant (priorité au Diesel)
    private static final String TYPE_DIESEL = "D";
    private static final String TYPE_ESSENCE = "ES";
    private static final String TYPE_HYBRIDE = "H";
    private static final String TYPE_ELECTRIQUE = "EL";

    private final ReservationRepository reservationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final AssignationRepository assignationRepository;
    private final DistanceService distanceService;
    private final ParametreService parametreService;

    public AssignationService() {
        this.reservationRepository = new ReservationRepository();
        this.vehiculeRepository = new VehiculeRepository();
        this.assignationRepository = new AssignationRepository();
        this.distanceService = new DistanceService();
        this.parametreService = new ParametreService();
    }

    public AssignationService(ReservationRepository reservationRepository,
                              VehiculeRepository vehiculeRepository,
                              AssignationRepository assignationRepository,
                              DistanceService distanceService,
                              ParametreService parametreService) {
        this.reservationRepository = reservationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.assignationRepository = assignationRepository;
        this.distanceService = distanceService;
        this.parametreService = parametreService;
    }

    /**
     * Récupère toutes les réservations pour une date donnée.
     */
    public List<Reservation> getReservationsByDate(LocalDate date) throws SQLException {
        return reservationRepository.findByDate(date);
    }

    /**
     * Récupère tous les véhicules disponibles.
     */
    public List<Vehicule> getAllVehicules() throws SQLException {
        return vehiculeRepository.findAll();
    }

    /**
     * Trie les réservations par distance aéroport-hôtel croissante.
     * En cas d'égalité, tri alphabétique par nom d'hôtel.
     */
    public List<Reservation> sortReservationsByDistance(List<Reservation> reservations) throws SQLException {
        // Créer une map des distances aéroport -> hôtel
        Map<Integer, Integer> distanceCache = new HashMap<>();
        
        for (Reservation r : reservations) {
            int hotelId = r.getHotel().getIdHotel();
            if (!distanceCache.containsKey(hotelId)) {
                int distance = distanceService.getDistance(AEROPORT_ID, hotelId);
                distanceCache.put(hotelId, distance);
            }
        }

        // Trier par distance, puis par nom d'hôtel (alphabétique)
        return reservations.stream()
                .sorted((r1, r2) -> {
                    int dist1 = distanceCache.getOrDefault(r1.getHotel().getIdHotel(), Integer.MAX_VALUE);
                    int dist2 = distanceCache.getOrDefault(r2.getHotel().getIdHotel(), Integer.MAX_VALUE);
                    
                    if (dist1 != dist2) {
                        return Integer.compare(dist1, dist2);
                    }
                    // Égalité -> tri alphabétique
                    String nom1 = r1.getHotel().getNom() != null ? r1.getHotel().getNom() : "";
                    String nom2 = r2.getHotel().getNom() != null ? r2.getHotel().getNom() : "";
                    return nom1.compareToIgnoreCase(nom2);
                })
                .collect(Collectors.toList());
    }

    /**
     * Trouve les véhicules candidats pour une réservation.
     * Un véhicule est candidat s'il a assez de places disponibles.
     */
    public List<TrajetVehiculeDTO> findCandidates(List<TrajetVehiculeDTO> etatsVehicules, int nbPassagers) {
        return etatsVehicules.stream()
                .filter(v -> v.getPlacesDisponibles() >= nbPassagers)
                .collect(Collectors.toList());
    }

    /**
     * Sélectionne le meilleur véhicule parmi les candidats.
     * Priorité : Diesel > autres types. Si égalité, choix aléatoire.
     */
   public TrajetVehiculeDTO selectBestVehicle(List<TrajetVehiculeDTO> candidats, int nbPassagers) {
        if (candidats == null || candidats.isEmpty()) {
            return null;
        }

        // Étape 1 : Trouver la capacité minimale suffisante
        int capaciteMinimale = candidats.stream()
                .mapToInt(TrajetVehiculeDTO::getPlacesDisponibles)
                .filter(places -> places >= nbPassagers)
                .min()
                .orElse(Integer.MAX_VALUE);

        // Filtrer les véhicules avec cette capacité minimale
        List<TrajetVehiculeDTO> vehiculesOptimaux = candidats.stream()
                .filter(v -> v.getPlacesDisponibles() == capaciteMinimale)
                .collect(Collectors.toList());

        // Étape 2 : Parmi ceux-là, priorité aux Diesel
        List<TrajetVehiculeDTO> diesels = vehiculesOptimaux.stream()
                .filter(v -> TYPE_DIESEL.equalsIgnoreCase(v.getTypeVehicule()))
                .collect(Collectors.toList());

        List<TrajetVehiculeDTO> selection = diesels.isEmpty() ? vehiculesOptimaux : diesels;

        // Étape 3 : Choix aléatoire
        Random random = new Random();
        return selection.get(random.nextInt(selection.size()));
    }

    /**
     * Convertit une Reservation en ReservationAffecteeDTO.
     */
    private ReservationAffecteeDTO toReservationAffecteeDTO(Reservation r, int ordreVisite, double distanceDepuisPrecedent) {
        ReservationAffecteeDTO dto = new ReservationAffecteeDTO();
        dto.setIdReservation(r.getIdReservation());
        dto.setDateHeureArrive(r.getDateHeureArrive());
        dto.setIdClient(r.getIdClient());
        dto.setNbPassager(r.getNbPassager());
        dto.setIdHotel(r.getHotel().getIdHotel());
        dto.setNomHotel(r.getHotel().getNom());
        dto.setOrdreVisite(ordreVisite);
        dto.setDistanceDepuisPrecedent(distanceDepuisPrecedent);
        return dto;
    }

    /**
     * Initialise les états des véhicules à partir de la liste des véhicules.
     */
    private List<TrajetVehiculeDTO> initVehiculeStates(List<Vehicule> vehicules, LocalDateTime heureDepart) {
        List<TrajetVehiculeDTO> etats = new ArrayList<>();
        for (Vehicule v : vehicules) {
            TrajetVehiculeDTO etat = new TrajetVehiculeDTO();
            etat.setVehiculeId(v.getIdVehicule());
            etat.setVehiculeReference(v.getReference());
            etat.setTypeVehicule(v.getTypeVehicule());
            etat.setCapacite(v.getNbPlace());
            etat.setHeureDepart(heureDepart);
            etats.add(etat);
        }
        return etats;
    }

    /**
     * Calcule la distance totale parcourue par un véhicule.
     * Aéroport -> Hôtel1 -> Hôtel2 -> ... -> HôtelN -> Aéroport
     */
    public double calculerDistanceTotaleVehicule(TrajetVehiculeDTO trajet) throws SQLException {
        List<ReservationAffecteeDTO> reservations = trajet.getListeReservations();
        if (reservations == null || reservations.isEmpty()) {
            return 0;
        }

        List<Integer> lieuIds = new ArrayList<>();
        lieuIds.add(AEROPORT_ID); // Départ aéroport

        // Ajouter tous les hôtels dans l'ordre de visite
        for (ReservationAffecteeDTO r : reservations) {
            lieuIds.add(r.getIdHotel());
        }

        lieuIds.add(AEROPORT_ID); // Retour aéroport

        return distanceService.calculerDistanceTotaleTrajet(lieuIds);
    }

    /**
     * Calcule l'heure de retour prévue pour un véhicule.
     * Basé sur : distance totale / vitesse moyenne + temps d'attente par hôtel
     */
    public LocalDateTime calculerHeureRetour(TrajetVehiculeDTO trajet) throws SQLException {
        if (trajet.getHeureDepart() == null) {
            return null;
        }

        double distanceTotale = trajet.getDistanceTotale();
        int vitesseMoyenne = parametreService.getVitesseMoyenne();
        int tempsAttenteParHotel = parametreService.getTempsAttenteHotel();
        int nbHotels = trajet.getListeReservations().size();

        // Temps de trajet en minutes
        double tempsTrajetMinutes = (distanceTotale / vitesseMoyenne) * 60;
        
        // Temps total d'attente
        int tempsAttenteTotal = nbHotels * tempsAttenteParHotel;

        // Temps total en minutes
        long tempsTotalMinutes = (long) tempsTrajetMinutes + tempsAttenteTotal;

        return trajet.getHeureDepart().plusMinutes(tempsTotalMinutes);
    }

    /**
     * Effectue la planification complète pour une date donnée.
     */
    public PlanificationDTO planifier(LocalDate date, LocalDateTime heureDepart) throws SQLException {
        PlanificationDTO planification = new PlanificationDTO();
        planification.setDatePlanification(date);

        // 1. Récupérer les réservations du jour
        List<Reservation> reservations = getReservationsByDate(date);
        
        // 2. Récupérer tous les véhicules
        List<Vehicule> vehicules = getAllVehicules();

        // 3. Trier les réservations par distance croissante
        List<Reservation> reservationsTriees = sortReservationsByDistance(reservations);

        // 4. Initialiser les états des véhicules
        List<TrajetVehiculeDTO> etatsVehicules = initVehiculeStates(vehicules, heureDepart);

        // 5. Affecter les réservations aux véhicules
        List<ReservationAffecteeDTO> nonAffectees = new ArrayList<>();
        int totalPassagers = 0;

        for (Reservation reservation : reservationsTriees) {
            // Trouver les candidats
            List<TrajetVehiculeDTO> candidats = findCandidates(etatsVehicules, reservation.getNbPassager());

            if (candidats.isEmpty()) {
                // Aucun véhicule disponible -> réservation non affectée
                ReservationAffecteeDTO nonAffectee = toReservationAffecteeDTO(reservation, 0, 0);
                nonAffectees.add(nonAffectee);
            } else {
                // Sélectionner le meilleur véhicule
                TrajetVehiculeDTO vehiculeChoisi = selectBestVehicle(candidats, reservation.getNbPassager());
                
                // Calculer la distance depuis le point précédent
                int dernierLieu = vehiculeChoisi.getListeReservations().isEmpty() 
                        ? AEROPORT_ID 
                        : vehiculeChoisi.getListeReservations()
                                .get(vehiculeChoisi.getListeReservations().size() - 1)
                                .getIdHotel();
                
                double distance = distanceService.getDistance(dernierLieu, reservation.getHotel().getIdHotel());

                // Créer le DTO et l'ajouter au véhicule
                int ordreVisite = vehiculeChoisi.getListeReservations().size() + 1;
                ReservationAffecteeDTO affectee = toReservationAffecteeDTO(reservation, ordreVisite, distance);
                vehiculeChoisi.addReservation(affectee);
                
                totalPassagers += reservation.getNbPassager();
            }
        }

        // 6. Calculer les distances totales et heures de retour
        double distanceTotaleJour = 0;
        for (TrajetVehiculeDTO trajet : etatsVehicules) {
            if (!trajet.getListeReservations().isEmpty()) {
                double distanceTrajet = calculerDistanceTotaleVehicule(trajet);
                trajet.setDistanceTotale(distanceTrajet);
                trajet.setHeureRetourPrevue(calculerHeureRetour(trajet));
                distanceTotaleJour += distanceTrajet;
            }
        }

        // 7. Filtrer les trajets avec au moins une réservation
        List<TrajetVehiculeDTO> trajetsActifs = etatsVehicules.stream()
                .filter(t -> !t.getListeReservations().isEmpty())
                .collect(Collectors.toList());

        // 8. Construire le résultat
        planification.setTrajets(trajetsActifs);
        planification.setReservationsNonAffectees(nonAffectees);
        planification.setDistanceTotaleJour(distanceTotaleJour);
        planification.setTotalPassagers(totalPassagers);
        planification.setTotalReservations(reservations.size());

        return planification;
    }

/**
     * Sauvegarde la planification dans la base de données.
     */
    public void sauvegarderPlanification(PlanificationDTO planification) throws SQLException {
        // Supprimer les anciennes assignations de cette date
        assignationRepository.deleteByDate(planification.getDatePlanification());

        // Insérer les nouvelles assignations
        for (TrajetVehiculeDTO trajet : planification.getTrajets()) {
            for (ReservationAffecteeDTO reservation : trajet.getListeReservations()) {
                Assignation assignation = new Assignation();
                
                // Gérer le cas où idClient peut être null ou non numérique
                String idClientStr = reservation.getIdClient();
                if (idClientStr != null && !idClientStr.isBlank()) {
                    try {
                        assignation.setClientId(Integer.parseInt(idClientStr));
                    } catch (NumberFormatException e) {
                        // Si idClient n'est pas un nombre, utiliser l'id de réservation
                        assignation.setClientId(reservation.getIdReservation());
                    }
                } else {
                    assignation.setClientId(reservation.getIdReservation());
                }
                
                assignation.setVehiculeId(trajet.getVehiculeId());
                assignation.setAssignedDate(LocalDateTime.now());
                
                assignationRepository.insert(assignation);
            }
        }
    }

    /**
     * Récupère les assignations d'une date.
     */
    public List<Assignation> getAssignationsByDate(LocalDate date) throws SQLException {
        return assignationRepository.findByDate(date);
    }
}