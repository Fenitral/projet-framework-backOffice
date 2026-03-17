package com.cousin.service;

import com.cousin.dto.GroupementDTO;
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
import java.time.LocalTime;
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
     * Trie les réservations selon les règles de planification (ancienne logique) :
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
     * SPRINT 4: Trie les réservations par nombre de passagers décroissant.
     * Les réservations avec le plus de passagers sont traitées en premier.
     */
    public List<Reservation> sortReservationsByNbPassagerDesc(List<Reservation> reservations) {
        return reservations.stream()
                .sorted((r1, r2) -> Integer.compare(r2.getNbPassager(), r1.getNbPassager()))
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
     * SPRINT 4: Sélectionne le meilleur véhicule selon la nouvelle logique d'assignation.
     * Règles de priorité :
     * 1. Chercher d'abord parmi les véhicules DÉJÀ UTILISÉS s'il y en a un avec assez de place
     * 2. Si aucun véhicule utilisé n'a assez de place, assigner un NOUVEAU véhicule
     * 3. Parmi les candidats, choisir celui avec le minimum de places vides (après assignation)
     * 4. Priorité Diesel si égalité
     * 5. Si égalité, choix aléatoire
     */
    public TrajetVehiculeDTO selectBestVehicle(List<TrajetVehiculeDTO> candidats, int nbPassagers) {
        if (candidats == null || candidats.isEmpty()) {
            return null;
        }

        // SPRINT 4: Séparer les véhicules déjà utilisés des véhicules vides
        List<TrajetVehiculeDTO> vehiculesUtilises = candidats.stream()
                .filter(v -> !v.getListeReservations().isEmpty())
                .filter(v -> v.getPlacesDisponibles() >= nbPassagers)
                .collect(Collectors.toList());

        List<TrajetVehiculeDTO> vehiculesVides = candidats.stream()
                .filter(v -> v.getListeReservations().isEmpty())
                .filter(v -> v.getPlacesDisponibles() >= nbPassagers)
                .collect(Collectors.toList());

        // Priorité aux véhicules déjà utilisés s'ils ont assez de place
        List<TrajetVehiculeDTO> vehiculesAConsiderer;
        if (!vehiculesUtilises.isEmpty()) {
            vehiculesAConsiderer = vehiculesUtilises;
        } else {
            vehiculesAConsiderer = vehiculesVides;
        }

        if (vehiculesAConsiderer.isEmpty()) {
            return null;
        }

        // Trouver le véhicule avec le minimum de places vides après assignation
        // (c'est-à-dire celui qui sera le plus rempli)
        int minPlacesVides = vehiculesAConsiderer.stream()
                .mapToInt(v -> v.getPlacesDisponibles() - nbPassagers)
                .min()
                .orElse(Integer.MAX_VALUE);

        // Filtrer les véhicules avec ce minimum de places vides
        List<TrajetVehiculeDTO> vehiculesOptimaux = vehiculesAConsiderer.stream()
                .filter(v -> (v.getPlacesDisponibles() - nbPassagers) == minPlacesVides)
                .collect(Collectors.toList());

        // Si plusieurs candidats, priorité aux Diesel
        if (vehiculesOptimaux.size() > 1) {
            List<TrajetVehiculeDTO> diesels = vehiculesOptimaux.stream()
                    .filter(v -> isDiesel(v.getTypeVehicule()))
                    .collect(Collectors.toList());
            if (!diesels.isEmpty()) {
                vehiculesOptimaux = diesels;
            }
        }

        // Choix aléatoire si plusieurs véhicules optimaux
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
     * SPRINT 5: Trie les réservations par heure d'arrivée (ascendant).
     * Les réservations arrivant le plus tôt sont traitées en premier.
     * 
     * @param reservations liste non triée de réservations
     * @return List<Reservation> réservations triées par dateHeureArrive croissant
     */
    public List<Reservation> trierReservationsParHeureArrivee(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return new ArrayList<>();
        }
        
        return reservations.stream()
                .sorted((r1, r2) -> {
                    LocalDateTime dt1 = r1.getDateHeureArrive();
                    LocalDateTime dt2 = r2.getDateHeureArrive();
                    
                    // Gérer les null (si une réservation n'a pas de date)
                    if (dt1 == null && dt2 == null) {
                        return 0;
                    }
                    if (dt1 == null) {
                        return 1; // null va à la fin
                    }
                    if (dt2 == null) {
                        return -1; // null va à la fin
                    }
                    
                    return dt1.compareTo(dt2);
                })
                .collect(Collectors.toList());
    }

    /**
     * SPRINT 5: Regroupe les réservations par fenêtre de 30 minutes.
     * 
     * Algorithmique :
     * 1. Les réservations doivent être TRIÉES par dateHeureArrive (du plus tôt au plus tard)
     * 2. Pour chaque réservation, chercher un groupe existant avec:
     *    - fenetre_min <= dateHeureArrive <= fenetre_max
     *    où fenetre = [première_arrivée, première_arrivée + 30 minutes]
     * 3. Si trouvé -> ajouter à ce groupe
     *    Si non trouvé -> créer un nouveau groupe
     * 
     * @param reservationsSorted liste des réservations TRIÉES par dateHeureArrive
     * @return List<List<Reservation>> groupes de réservations, chacun dans sa fenêtre 30 min
     */
    public List<List<Reservation>> regrouperReservationsParFenetre30Min(List<Reservation> reservationsSorted) {
        List<List<Reservation>> groupes = new ArrayList<>();
        
        if (reservationsSorted == null || reservationsSorted.isEmpty()) {
            return groupes;
        }

        // Pour chaque réservation, trouver son groupe ou en créer un
        for (Reservation reservation : reservationsSorted) {
            LocalDateTime dateArriveeRes = reservation.getDateHeureArrive();
            if (dateArriveeRes == null) {
                continue; // Ignorer les réservations sans date d'arrivée
            }

            // Chercher si cette réservation appartient à une fenêtre existante
            List<Reservation> groupeTrouve = null;
            for (List<Reservation> groupe : groupes) {
                if (!groupe.isEmpty()) {
                    // Récupérer l'heure d'arrivée du PREMIER élément du groupe (référence de fenêtre)
                    LocalDateTime premierArrivee = groupe.get(0).getDateHeureArrive();
                    LocalDateTime finFenetre = premierArrivee.plusMinutes(30);

                    // Vérifier si la réservation est dans la fenêtre [premierArrivee, premierArrivee + 30 min]
                    if (!dateArriveeRes.isBefore(premierArrivee) && !dateArriveeRes.isAfter(finFenetre)) {
                        groupeTrouve = groupe;
                        break;
                    }
                }
            }

            // Ajouter la réservation au groupe trouvé ou en créer un nouveau
            if (groupeTrouve != null) {
                groupeTrouve.add(reservation);
            } else {
                List<Reservation> nouveauGroupe = new ArrayList<>();
                nouveauGroupe.add(reservation);
                groupes.add(nouveauGroupe);
            }
        }

        return groupes;
    }

    /**
     * SPRINT 5: Calcule l'heure de départ pour un groupe de réservations.
     * 
     * RÈGLE: heure_départ = MAX(heure_arrivée) du groupe
     * - Les 30 minutes servent à créer la fenêtre de regroupement
     * - L'heure de départ = la dernière heure d'arrivée des vols du groupe
     * - Si résultat < 08:00 → forcer 08:00 (départ minimum légal)
     * 
     * @param reservations liste de réservations d'un même groupe
     * @return LocalDateTime heure de départ calculée
     */
    public LocalDateTime calculerHeureDepartGroupe(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)); // Default 08:00
        }
        
        // Trouver l'heure d'arrivée MAX du groupe
        LocalDateTime maxArrivee = reservations.stream()
                .map(Reservation::getDateHeureArrive)
                .filter(dt -> dt != null)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)));
        
        // Appliquer minimum 08:00
        LocalTime heureMinimum = LocalTime.of(8, 0);
        if (maxArrivee.toLocalTime().isBefore(heureMinimum)) {
            maxArrivee = maxArrivee.withHour(8).withMinute(0).withSecond(0);
        }
        
        return maxArrivee;
    }

    /**
     * SPRINT 5: Construit les lignes de résultats pour affichage écran.
     * 
     * Chaque ligne = 1 véhicule avec ses réservations assignées
     * Format: {départ, véhicule, [réservations], km, heure_retour}
     * 
     * @param trajets liste des trajets véhicules planifiés
     * @return List<TrajetVehiculeDTO> (pour JSON serialization)
     */
    public List<TrajetVehiculeDTO> buildPlanificationLignes(List<TrajetVehiculeDTO> trajets) {
        if (trajets == null || trajets.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Les trajets sont déjà complètement remplis par planifier()
        // Cette méthode assure juste que tous les champs sont présents
        for (TrajetVehiculeDTO trajet : trajets) {
            // Vérifier que tous les champs critiques sont présents
            if (trajet.getHeureDepart() == null) {
                trajet.setHeureDepart(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)));
            }
            if (trajet.getDistanceTotale() == 0) {
                try {
                    trajet.setDistanceTotale((int) calculerDistanceTrajetReelle(trajet));
                } catch (SQLException e) {
                    trajet.setDistanceTotale(0);
                }
            }
            if (trajet.getHeureRetourPrevue() == null) {
                try {
                    trajet.setHeureRetourPrevue(calculerHeureRetour(trajet));
                } catch (SQLException e) {
                    // Fallback
                    trajet.setHeureRetourPrevue(trajet.getHeureDepart().plusHours(1));
                }
            }
        }
        
        return trajets;
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

        // 3. SPRINT 4: Trier les réservations par nombre de passagers décroissant
        // Les plus grandes réservations sont traitées en premier
        List<Reservation> reservationsTriees = sortReservationsByNbPassagerDesc(reservations);

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

        // 6. Trier les réservations par distance depuis l'aéroport (le plus proche en premier)
        //    puis calculer les distances et heures de passage à chaque hôtel
        int vitesseMoyenne = parametreService.getVitesseMoyenne();
        
        for (TrajetVehiculeDTO trajet : etatsVehicules) {
            if (!trajet.getListeReservations().isEmpty()) {
                // D'abord, calculer la distance aéroport -> hôtel pour chaque réservation
                for (ReservationAffecteeDTO res : trajet.getListeReservations()) {
                    double distDepuisAeroport = distanceService.getDistance(AEROPORT_ID, res.getIdHotel());
                    res.setDistanceDepuisAeroport(distDepuisAeroport);
                }
                
                // Trier par distance depuis l'aéroport (le plus proche en premier)
                // En cas d'égalité de distance, tri alphabétique par nom d'hôtel
                List<ReservationAffecteeDTO> reservationsTrieesParDistance = trajet.getListeReservations().stream()
                        .sorted((r1, r2) -> {
                            int compareDistance = Double.compare(r1.getDistanceDepuisAeroport(), r2.getDistanceDepuisAeroport());
                            if (compareDistance != 0) {
                                return compareDistance;
                            }
                            // Même distance -> tri alphabétique par nom d'hôtel
                            String nom1 = r1.getNomHotel() != null ? r1.getNomHotel() : "";
                            String nom2 = r2.getNomHotel() != null ? r2.getNomHotel() : "";
                            return nom1.compareToIgnoreCase(nom2);
                        })
                        .collect(Collectors.toList());
                
                // Calculer les distances et heures de passage
                LocalDateTime heureActuelle = trajet.getHeureDepart();
                int lieuPrecedent = AEROPORT_ID;
                
                for (int i = 0; i < reservationsTrieesParDistance.size(); i++) {
                    ReservationAffecteeDTO res = reservationsTrieesParDistance.get(i);
                    res.setOrdreVisite(i + 1);
                    
                    // Distance depuis le point précédent (aéroport ou hôtel précédent)
                    double distDepuisPrecedent = distanceService.getDistance(lieuPrecedent, res.getIdHotel());
                    res.setDistance(distDepuisPrecedent);
                    
                    // Calculer l'heure d'arrivée à cet hôtel (sans temps d'attente)
                    if (heureActuelle != null && vitesseMoyenne > 0) {
                        double tempsTrajetMinutes = (distDepuisPrecedent / vitesseMoyenne) * 60;
                        heureActuelle = heureActuelle.plusMinutes((long) tempsTrajetMinutes);
                        res.setHeurePassage(heureActuelle);
                    }
                    
                    lieuPrecedent = res.getIdHotel();
                }
                
                // Mettre à jour la liste triée
                trajet.setListeReservations(reservationsTrieesParDistance);
            }
        }

        // 7. Calculer les distances et heures de retour
        double distanceTotaleJour = 0;
        for (TrajetVehiculeDTO trajet : etatsVehicules) {
            if (!trajet.getListeReservations().isEmpty()) {
                // Distance parcourue: somme des distances depuis précédent (aéroport -> hôtel1 -> ... -> hôtelN)
                double distanceParcourue = calculerDistanceTotaleVehicule(trajet);
                trajet.setDistanceParcourue(distanceParcourue);
                
                // Distance totale: avec retour à l'aéroport (aéroport -> hôtels -> aéroport)
                double distanceTotale = calculerDistanceTrajetReelle(trajet);
                trajet.setDistanceTotale(distanceTotale);
                
                trajet.setHeureRetourPrevue(calculerHeureRetour(trajet));
                distanceTotaleJour += distanceTotale;
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

    /**
     * SPRINT 5 : Planifie les réservations d'une journée en les regroupant
     * par fenêtre de 30 minutes autour de leur heure d'arrivée.
     *
     * Algorithme :
     * 1. Récupère toutes les réservations du jour
     * 2. Les trie par heure d'arrivée croissante
     * 3. Les regroupe en fenêtres de 30 minutes
     * 4. Pour chaque groupe, l'heure de départ = MAX(heure_arrivée) du groupe
     * 5. Assigne les véhicules selon les règles Sprint 4 (+ grande réservation en premier,
     *    véhicule déjà utilisé prioritaire, minimum de places vides, Diesel en priorité)
     * 6. Retourne la liste des groupements, chacun avec ses trajets véhicules
     *
     * @param date date du jour à planifier
     * @return liste de GroupementDTO, un par fenêtre horaire
     */
    public List<GroupementDTO> planifierParGroupements(LocalDate date) throws SQLException {
        List<GroupementDTO> groupements = new ArrayList<>();

        // 1. Récupérer toutes les réservations du jour
        List<Reservation> toutesReservations = getReservationsByDate(date);
        if (toutesReservations.isEmpty()) {
            return groupements;
        }

        // 2. Trier par heure d'arrivée croissante
        List<Reservation> triees = trierReservationsParHeureArrivee(toutesReservations);

        // 3. Grouper en fenêtres de 30 minutes
        List<List<Reservation>> fenetres = regrouperReservationsParFenetre30Min(triees);

        // 4. Récupérer tous les véhicules disponibles (une seule fois)
        List<Vehicule> tousVehicules = getAllVehicules();
        int vitesseMoyenne = parametreService.getVitesseMoyenne();

        // 5. Construire un GroupementDTO par fenêtre
        for (int numGroupe = 0; numGroupe < fenetres.size(); numGroupe++) {
            List<Reservation> groupe = fenetres.get(numGroupe);
            if (groupe.isEmpty()) {
                continue;
            }

            // Heure de départ = MAX(heure arrivée) du groupe
            LocalDateTime heureDepart = calculerHeureDepartGroupe(groupe);

            // Initialiser les états véhicules pour CE groupe (repart de zéro)
            List<TrajetVehiculeDTO> etatsVehicules = initVehiculeStates(tousVehicules, heureDepart);

            // Trier les réservations du groupe par nbPassager DESC (règle Sprint 4)
            List<Reservation> reservationsTriees = sortReservationsByNbPassagerDesc(groupe);

            int totalPassagers = 0;

            // Assigner les réservations aux véhicules
            for (Reservation reservation : reservationsTriees) {
                List<TrajetVehiculeDTO> candidats = findCandidates(etatsVehicules, reservation.getNbPassager());

                if (!candidats.isEmpty()) {
                    TrajetVehiculeDTO vehiculeChoisi = selectBestVehicle(candidats, reservation.getNbPassager());

                    int dernierLieu = vehiculeChoisi.getListeReservations().isEmpty()
                            ? AEROPORT_ID
                            : vehiculeChoisi.getListeReservations()
                                    .get(vehiculeChoisi.getListeReservations().size() - 1)
                                    .getIdHotel();

                    double distance = distanceService.getDistance(dernierLieu, reservation.getHotel().getIdHotel());
                    int ordreVisite = vehiculeChoisi.getListeReservations().size() + 1;
                    ReservationAffecteeDTO affectee = toReservationAffecteeDTO(reservation, ordreVisite, distance);
                    vehiculeChoisi.addReservation(affectee);
                    totalPassagers += reservation.getNbPassager();
                }
                // Les réservations non affectables dans ce groupe sont ignorées
                // (capacité insuffisante) — aucun véhicule assez grand
            }

            // Trier les réservations de chaque véhicule par distance depuis l'aéroport
            // et recalculer les heures de passage
            for (TrajetVehiculeDTO trajet : etatsVehicules) {
                if (trajet.getListeReservations().isEmpty()) {
                    continue;
                }

                // Calculer distance aéroport→hôtel pour chaque réservation
                for (ReservationAffecteeDTO res : trajet.getListeReservations()) {
                    double distDepuisAeroport = distanceService.getDistance(AEROPORT_ID, res.getIdHotel());
                    res.setDistanceDepuisAeroport(distDepuisAeroport);
                }

                // Trier par distance aéroport croissante, puis alphabétique
                List<ReservationAffecteeDTO> ordonnees = trajet.getListeReservations().stream()
                        .sorted((r1, r2) -> {
                            int cmp = Double.compare(r1.getDistanceDepuisAeroport(), r2.getDistanceDepuisAeroport());
                            if (cmp != 0) return cmp;
                            String n1 = r1.getNomHotel() != null ? r1.getNomHotel() : "";
                            String n2 = r2.getNomHotel() != null ? r2.getNomHotel() : "";
                            return n1.compareToIgnoreCase(n2);
                        })
                        .collect(Collectors.toList());

                // Recalculer distances inter-stops et heures de passage
                LocalDateTime heureActuelle = heureDepart;
                int lieuPrecedent = AEROPORT_ID;

                for (int i = 0; i < ordonnees.size(); i++) {
                    ReservationAffecteeDTO res = ordonnees.get(i);
                    res.setOrdreVisite(i + 1);

                    double distDepuisPrecedent = distanceService.getDistance(lieuPrecedent, res.getIdHotel());
                    res.setDistance(distDepuisPrecedent);

                    if (heureActuelle != null && vitesseMoyenne > 0) {
                        double tempsMin = (distDepuisPrecedent / vitesseMoyenne) * 60.0;
                        heureActuelle = heureActuelle.plusMinutes((long) tempsMin);
                        res.setHeurePassage(heureActuelle);
                    }

                    lieuPrecedent = res.getIdHotel();
                }

                trajet.setListeReservations(ordonnees);

                // Calculer km parcouru et heure de retour
                double distParcourue = calculerDistanceTotaleVehicule(trajet);
                trajet.setDistanceParcourue(distParcourue);

                double distTotale = calculerDistanceTrajetReelle(trajet);
                trajet.setDistanceTotale(distTotale);

                trajet.setHeureRetourPrevue(calculerHeureRetour(trajet));
            }

            // Ne garder que les véhicules actifs (avec au moins une réservation)
            List<TrajetVehiculeDTO> trajetsActifs = etatsVehicules.stream()
                    .filter(t -> !t.getListeReservations().isEmpty())
                    .collect(Collectors.toList());

            // Construire le GroupementDTO
            GroupementDTO groupementDTO = new GroupementDTO();
            groupementDTO.setNumeroGroupe(numGroupe + 1);
            groupementDTO.setHeureDepart(heureDepart);
            groupementDTO.setTrajets(trajetsActifs);
            groupementDTO.setTotalReservations(groupe.size());
            groupementDTO.setTotalPassagers(totalPassagers);

            groupements.add(groupementDTO);
        }

        return groupements;
    }
}