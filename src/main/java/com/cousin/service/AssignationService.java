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
     * Trie les réservations selon les règles de planification :
     * 1. Par dateHeureArrive (les plus tôt en premier)
     * 2. Par distance aéroport-hôtel croissante
     * 3. En cas d'égalité de dateHeure ET distance → tri alphabétique par nom d'hôtel
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

        // Trier par dateHeureArrive, puis par distance, puis par nom d'hôtel (alphabétique)
        return reservations.stream()
                .sorted((r1, r2) -> {
                    // 1. Comparer par dateHeureArrive
                    LocalDateTime dt1 = r1.getDateHeureArrive();
                    LocalDateTime dt2 = r2.getDateHeureArrive();
                    
                    if (dt1 != null && dt2 != null && !dt1.equals(dt2)) {
                        return dt1.compareTo(dt2);
                    }
                    
                    // 2. Même dateHeure ou null -> comparer par distance
                    int dist1 = distanceCache.getOrDefault(r1.getHotel().getIdHotel(), Integer.MAX_VALUE);
                    int dist2 = distanceCache.getOrDefault(r2.getHotel().getIdHotel(), Integer.MAX_VALUE);
                    
                    if (dist1 != dist2) {
                        return Integer.compare(dist1, dist2);
                    }
                    
                    // 3. Même distance -> tri alphabétique par nom d'hôtel
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
     * Vérifie si un véhicule est de type Diesel.
     * Accepte les formats : "D", "Diesel", "diesel", "DIESEL", etc.
     */
    private boolean isDiesel(String typeVehicule) {
        if (typeVehicule == null) {
            return false;
        }
        String type = typeVehicule.trim().toUpperCase();
        return type.equals("D") || type.equals("DIESEL");
    }

    /**
     * Sélectionne le meilleur véhicule parmi les candidats.
     * Règles de priorité :
     * 1. Priorité aux véhicules Diesel
     * 2. Parmi les Diesel (ou non-Diesel si aucun Diesel disponible), choisir celui avec la capacité minimale suffisante
     * 3. Si égalité, choix aléatoire
     */
    public TrajetVehiculeDTO selectBestVehicle(List<TrajetVehiculeDTO> candidats, int nbPassagers) {
        if (candidats == null || candidats.isEmpty()) {
            return null;
        }

        // Étape 1 : Séparer les Diesel des autres
        List<TrajetVehiculeDTO> diesels = candidats.stream()
                .filter(v -> isDiesel(v.getTypeVehicule()))
                .collect(Collectors.toList());

        // Utiliser les Diesel s'il y en a, sinon utiliser tous les candidats
        List<TrajetVehiculeDTO> vehiculesAConsiderer = diesels.isEmpty() ? candidats : diesels;

        // Étape 2 : Parmi ceux-là, trouver la capacité minimale suffisante
        int capaciteMinimale = vehiculesAConsiderer.stream()
                .mapToInt(TrajetVehiculeDTO::getPlacesDisponibles)
                .filter(places -> places >= nbPassagers)
                .min()
                .orElse(Integer.MAX_VALUE);

        // Filtrer les véhicules avec cette capacité minimale
        List<TrajetVehiculeDTO> vehiculesOptimaux = vehiculesAConsiderer.stream()
                .filter(v -> v.getPlacesDisponibles() == capaciteMinimale)
                .collect(Collectors.toList());

        // Étape 3 : Choix aléatoire si plusieurs véhicules optimaux
        Random random = new Random();
        return vehiculesOptimaux.get(random.nextInt(vehiculesOptimaux.size()));
    }

    /**
     * Convertit une Reservation en ReservationAffecteeDTO.
     */
    private ReservationAffecteeDTO toReservationAffecteeDTO(Reservation r, int ordreVisite, double Distance) {
        ReservationAffecteeDTO dto = new ReservationAffecteeDTO();
        dto.setIdReservation(r.getIdReservation());
        dto.setDateHeureArrive(r.getDateHeureArrive());
        dto.setIdClient(r.getIdClient());
        dto.setNbPassager(r.getNbPassager());
        dto.setIdHotel(r.getHotel().getIdHotel());
        dto.setNomHotel(r.getHotel().getNom());
        dto.setOrdreVisite(ordreVisite);
        dto.setDistance(Distance);
        
        // Ajouter les informations du client
        if (r.getClientId() != null) {
            dto.setClientId(r.getClientId());
        }
        if (r.getClient() != null) {
            dto.setClientName(r.getClient().getName());
            dto.setClientEmail(r.getClient().getEmail());
        }
        
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
     * Somme des distances aéroport -> chaque hôtel
     */
    public double calculerDistanceTotaleVehicule(TrajetVehiculeDTO trajet) throws SQLException {
        List<ReservationAffecteeDTO> reservations = trajet.getListeReservations();
        if (reservations == null || reservations.isEmpty()) {
            return 0;
        }

        // Somme des distances depuis le point précédent (déjà calculées)
        double distanceTotale = 0;
        for (ReservationAffecteeDTO r : reservations) {
            distanceTotale += r.getDistance();
        }

        return distanceTotale;
    }

    /**
     * Calcule la distance réelle parcourue par le véhicule.
     * Trajet: Aéroport → Hôtel1 → Hôtel2 → ... → HôtelN → Aéroport
     * Les distances inter-hôtels sont maintenant dans la base de données.
     */
    public double calculerDistanceTrajetReelle(TrajetVehiculeDTO trajet) throws SQLException {
        List<ReservationAffecteeDTO> reservations = trajet.getListeReservations();
        if (reservations == null || reservations.isEmpty()) {
            return 0;
        }

        // Somme des distances depuis le point précédent (déjà calculées)
        double distance = 0;
        for (ReservationAffecteeDTO r : reservations) {
            distance += r.getDistance();
        }
        
        // Ajouter la distance du dernier hôtel vers l'aéroport
        int dernierHotelId = reservations.get(reservations.size() - 1).getIdHotel();
        distance += distanceService.getDistance(dernierHotelId, AEROPORT_ID);
        
        return distance;
    }

    /**
     * Calcule l'heure de retour prévue pour un véhicule.
     * Basé sur : distance réelle parcourue / vitesse moyenne
     */
    public LocalDateTime calculerHeureRetour(TrajetVehiculeDTO trajet) throws SQLException {
        if (trajet.getHeureDepart() == null) {
            return null;
        }

        // Utiliser la distance réelle parcourue (pas la somme des distances aéroport->hôtel)
        double distanceReelle = calculerDistanceTrajetReelle(trajet);
        int vitesseMoyenne = parametreService.getVitesseMoyenne();

        // Temps de trajet en minutes = (distance / vitesse) * 60
        double tempsTrajetMinutes = (distanceReelle / vitesseMoyenne) * 60;

        return trajet.getHeureDepart().plusMinutes((long) tempsTrajetMinutes);
    }

    /**
     * Effectue la planification complète pour une date donnée.
     * Exclut les réservations déjà planifiées (qui ont une assignation).
     */
    public PlanificationDTO planifier(LocalDate date, LocalDateTime heureDepart) throws SQLException {
        PlanificationDTO planification = new PlanificationDTO();
        planification.setDatePlanification(date);

        // 1. Récupérer les réservations du jour
        List<Reservation> reservations = getReservationsByDate(date);
        
        // 1b. Exclure les réservations déjà assignées
        List<Integer> reservationsDejaAssignees = assignationRepository.findAssignedReservationIds();
        reservations = reservations.stream()
                .filter(r -> !reservationsDejaAssignees.contains(r.getIdReservation()))
                .collect(Collectors.toList());
        
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

        // 6. Trier les réservations par ordre alphabétique du nom d'hôtel dans chaque véhicule
        //    puis calculer les distances et heures de passage à chaque hôtel
        int vitesseMoyenne = parametreService.getVitesseMoyenne();
        
        for (TrajetVehiculeDTO trajet : etatsVehicules) {
            if (!trajet.getListeReservations().isEmpty()) {
                // Trier alphabétiquement par nom d'hôtel
                List<ReservationAffecteeDTO> reservationsTrieesAlpha = trajet.getListeReservations().stream()
                        .sorted((r1, r2) -> {
                            String nom1 = r1.getNomHotel() != null ? r1.getNomHotel() : "";
                            String nom2 = r2.getNomHotel() != null ? r2.getNomHotel() : "";
                            return nom1.compareToIgnoreCase(nom2);
                        })
                        .collect(Collectors.toList());
                
                // Calculer les distances et heures de passage
                LocalDateTime heureActuelle = trajet.getHeureDepart();
                int lieuPrecedent = AEROPORT_ID;
                
                for (int i = 0; i < reservationsTrieesAlpha.size(); i++) {
                    ReservationAffecteeDTO res = reservationsTrieesAlpha.get(i);
                    res.setOrdreVisite(i + 1);
                    
                    // Distance depuis le point précédent (aéroport ou hôtel précédent)
                    double distDepuisPrecedent = distanceService.getDistance(lieuPrecedent, res.getIdHotel());
                    res.setDistance(distDepuisPrecedent);
                    
                    // Distance aéroport -> cet hôtel (pour affichage)
                    double distDepuisAeroport = distanceService.getDistance(AEROPORT_ID, res.getIdHotel());
                    res.setDistanceDepuisAeroport(distDepuisAeroport);
                    
                    // Calculer l'heure d'arrivée à cet hôtel (sans temps d'attente)
                    if (heureActuelle != null && vitesseMoyenne > 0) {
                        double tempsTrajetMinutes = (distDepuisPrecedent / vitesseMoyenne) * 60;
                        heureActuelle = heureActuelle.plusMinutes((long) tempsTrajetMinutes);
                        res.setHeurePassage(heureActuelle);
                    }
                    
                    lieuPrecedent = res.getIdHotel();
                }
                
                // Mettre à jour la liste triée
                trajet.setListeReservations(reservationsTrieesAlpha);
            }
        }

        // 7. Calculer les distances totales (réelles) et heures de retour
        double distanceTotaleJour = 0;
        for (TrajetVehiculeDTO trajet : etatsVehicules) {
            if (!trajet.getListeReservations().isEmpty()) {
                // Distance réelle parcourue: aéroport -> hôtels -> aéroport
                double distanceTrajet = calculerDistanceTrajetReelle(trajet);
                trajet.setDistanceTotale(distanceTrajet);
                trajet.setHeureRetourPrevue(calculerHeureRetour(trajet));
                distanceTotaleJour += distanceTrajet;
            }
        }

        // 8. Filtrer les trajets avec au moins une réservation
        List<TrajetVehiculeDTO> trajetsActifs = etatsVehicules.stream()
                .filter(t -> !t.getListeReservations().isEmpty())
                .collect(Collectors.toList());

        // 9. Construire le résultat
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
        // Insérer les nouvelles assignations (ne pas supprimer les anciennes)
        for (TrajetVehiculeDTO trajet : planification.getTrajets()) {
            for (ReservationAffecteeDTO reservation : trajet.getListeReservations()) {
                Assignation assignation = new Assignation();
                
                // Enregistrer l'ID de la réservation pour éviter les doublons
                assignation.setReservationId(reservation.getIdReservation());
                
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