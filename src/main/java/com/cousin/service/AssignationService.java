package com.cousin.service;

import com.cousin.dto.GroupementDTO;
import com.cousin.dto.PlanificationDTO;
import com.cousin.dto.ReservationAffecteeDTO;
import com.cousin.dto.TrajetVehiculeDTO;
import com.cousin.model.Assignation;
import com.cousin.model.Reservation;
import com.cousin.model.TrajetExecution;
import com.cousin.model.Vehicule;
import com.cousin.repository.AssignationRepository;
import com.cousin.repository.ReservationRepository;
import com.cousin.repository.TrajetExecutionRepository;
import com.cousin.repository.VehiculeRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class AssignationService {

    // ID représentant l'aéroport dans la table distance
    private static final int AEROPORT_ID = 0;
    private static final int TEMPS_ATTENTE_GROUPEMENT_MINUTES = 30;
    
    // Types de carburant (priorité au Diesel)
    private static final String TYPE_DIESEL = "D";
    private final ReservationRepository reservationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final AssignationRepository assignationRepository;
    private final TrajetExecutionRepository trajetExecutionRepository;
    private final DistanceService distanceService;
    private final ParametreService parametreService;

    public AssignationService() {
        this.reservationRepository = new ReservationRepository();
        this.vehiculeRepository = new VehiculeRepository();
        this.assignationRepository = new AssignationRepository();
        this.trajetExecutionRepository = new TrajetExecutionRepository();
        this.distanceService = new DistanceService();
        this.parametreService = new ParametreService();
    }

    public AssignationService(ReservationRepository reservationRepository,
                              VehiculeRepository vehiculeRepository,
                              AssignationRepository assignationRepository,
                      DistanceService distanceService,
                      ParametreService parametreService) {
        this(reservationRepository,
            vehiculeRepository,
            assignationRepository,
            new TrajetExecutionRepository(),
            distanceService,
            parametreService);
        }

        public AssignationService(ReservationRepository reservationRepository,
                      VehiculeRepository vehiculeRepository,
                      AssignationRepository assignationRepository,
                              TrajetExecutionRepository trajetExecutionRepository,
                              DistanceService distanceService,
                              ParametreService parametreService) {
        this.reservationRepository = reservationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.assignationRepository = assignationRepository;
        this.trajetExecutionRepository = trajetExecutionRepository;
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


    public List<Reservation> sortReservationsByNbPassagerDesc(List<Reservation> reservations) {
        return reservations.stream()
                .sorted((r1, r2) -> {
                    int cmpPassagers = Integer.compare(r2.getNbPassager(), r1.getNbPassager());
                    if (cmpPassagers != 0) {
                        return cmpPassagers;
                    }
                    return resolveClientSortKey(r1).compareToIgnoreCase(resolveClientSortKey(r2));
                })
                .collect(Collectors.toList());
    }

    /**
     * SPRINT 8: Surcharge de tri avec option ancienneté en priorité
     * byOldestFirst = true: Tri par date d'arrivée ASC, puis nbPassagers DESC
     * byOldestFirst = false: Tri par nbPassagers DESC (défaut)
     */
    public List<Reservation> sortReservationsByNbPassagerDesc(List<Reservation> reservations, boolean byOldestFirst) {
        if (!byOldestFirst) {
            return sortReservationsByNbPassagerDesc(reservations);
        }
        // Tri par ancienneté (date arrivée ASC), puis nbPassagers DESC
        return reservations.stream()
                .sorted((r1, r2) -> {
                    if (r1.getDateHeureArrive() == null || r2.getDateHeureArrive() == null) {
                        return 0;
                    }
                    int cmpDate = r1.getDateHeureArrive().compareTo(r2.getDateHeureArrive());
                    if (cmpDate != 0) {
                        return cmpDate; // Plus ancien en premier
                    }
                    // À égalité de date, priorité au nombre passagers DESC
                    return Integer.compare(r2.getNbPassager(), r1.getNbPassager());
                })
                .collect(Collectors.toList());
    }

    /**
     * SPRINT 8: Trier les réservations avec priorité aux non-assignées
     * NON-ASSIGNÉES d'abord (par ancienneté, puis nbPassagers DESC)
     * puis NORMALES (par nbPassagers DESC)
     */
    public List<Reservation> sortReservationsWithUnassignedPriority(
            List<Reservation> allReservations,
            List<Reservation> unassignedReservations) {
        
        // Séparer assignées et non-assignées
        List<Reservation> nonAssignees = new ArrayList<>(unassignedReservations);
        List<Reservation> assignees = new ArrayList<>(allReservations);
        assignees.removeAll(nonAssignees);
        
        // Trier non-assignées par ancienneté + nombreux
        List<Reservation> sortedUnassigned = sortReservationsByNbPassagerDesc(nonAssignees, true);
        
        // Trier assignées par nombreux
        List<Reservation> sortedAssigned = sortReservationsByNbPassagerDesc(assignees, false);
        
        // Combiner: NON-ASSIGNÉES EN PREMIER
        List<Reservation> result = new ArrayList<>();
        result.addAll(sortedUnassigned);
        result.addAll(sortedAssigned);
        
        return result;
    }

    /**
     * SPRINT 8: Chercher les réservations en attente dans la fenêtre de retour du véhicule
     * Fenêtre: [heureRetour, heureRetour + windowMinutes]
     */
    public List<Reservation> findReservationsInReturnWindow(
            List<Reservation> waitingReservations,
            LocalDateTime vehicleReturnTime,
            int windowMinutes) {
        
        if (vehicleReturnTime == null || waitingReservations.isEmpty()) {
            return new ArrayList<>();
        }
        
        LocalDateTime windowEnd = vehicleReturnTime.plusMinutes(windowMinutes);
        
        return waitingReservations.stream()
                .filter(r -> r.getDateHeureArrive() != null)
                .filter(r -> !r.getDateHeureArrive().isBefore(vehicleReturnTime) &&
                            !r.getDateHeureArrive().isAfter(windowEnd))
                .sorted((r1, r2) -> r1.getDateHeureArrive().compareTo(r2.getDateHeureArrive()))
                .collect(Collectors.toList());
    }

    /**
     * SPRINT 8: Vérifier si les passagers d'une réservation peuvent être séparés
     * Retourne true si la somme des places dispo > nb passagers
     */
    public boolean canSplitPassengers(
            Reservation reservation,
            List<TrajetVehiculeDTO> availableVehicles) {
        
        if (reservation == null || availableVehicles == null || availableVehicles.isEmpty()) {
            return false;
        }
        
        int totalPlacesAvailable = availableVehicles.stream()
                .mapToInt(TrajetVehiculeDTO::getPlacesDisponibles)
                .sum();
        
        return totalPlacesAvailable > reservation.getNbPassager();
    }

    /**
     * SPRINT 8: Assigner partiellement une réservation à un véhicule
     * Retourne la réservation avec les passagers NON assignés
     * Le véhicule est modifié directement (passagers ajoutés)
     */
    public Reservation assignPartially(
            Reservation sourceReservation,
            TrajetVehiculeDTO vehicle,
            int nbToAssign) {
        
        if (sourceReservation.getNbPassager() <= nbToAssign) {
            // Tout peut être assigné
            return null;
        }
        
        // Créer réservation pour les passagers restants
        int remainingPassengers = sourceReservation.getNbPassager() - nbToAssign;
        Reservation remainingReservation = new Reservation(
                sourceReservation.getIdReservation(),
                sourceReservation.getDateHeureArrive(),
                sourceReservation.getIdClient(),
                remainingPassengers,
                sourceReservation.getHotel());
        
        if (sourceReservation.getClient() != null) {
            remainingReservation.setClient(sourceReservation.getClient());
        }
        if (sourceReservation.getClientId() != null) {
            remainingReservation.setClientId(sourceReservation.getClientId());
        }
        
        return remainingReservation;
    }

   /**
     * SPRINT 8 - RÈGLE BEST-FIT CRUCIALE:
     * Chercher la réservation PLUS PROCHE du nombre de places libres
     * Distance = |nbPassagers - placesLibres|
     * Si égalité: priorité à celui >= placesLibres
     * 
     * Retourne l'index de la meilleure réservation, ou -1 si aucune
     */
    public int findBestReservationForAvailableSpace(
            List<Reservation> reservations,
            int availableSpaces) {
        
        if (reservations == null || reservations.isEmpty() || availableSpaces <= 0) {
            return -1;
        }
        
        int bestIndex = -1;
        int minDistance = Integer.MAX_VALUE;
        int bestPassengers = 0;
        
        for (int i = 0; i < reservations.size(); i++) {
            Reservation res = reservations.get(i);
            if (res == null || res.getNbPassager() <= 0) {
                continue;
            }
            
            int nbPax = res.getNbPassager();
            int distance = Math.abs(nbPax - availableSpaces);
            
            // Meilleur candidat si:
            // 1. Distance plus petite
            // 2. Distance égale + nbPax >= availableSpaces (préférer celui qui rentre complètement)
            if (distance < minDistance) {
                minDistance = distance;
                bestIndex = i;
                bestPassengers = nbPax;
            } else if (distance == minDistance && nbPax >= availableSpaces && bestPassengers < availableSpaces) {
                // À distance égale, prendre celui >= availableSpaces si possible
                bestIndex = i;
                bestPassengers = nbPax;
            }
        }
        
        return bestIndex;
    }


    public List<TrajetVehiculeDTO> findCandidates(List<TrajetVehiculeDTO> etatsVehicules, int nbPassagers) {
        // On suppose que l'heure de la réservation/fenêtre est passée en paramètre ou accessible dans le contexte
        // Pour une robustesse immédiate, on filtre sur la fenêtre de départ si disponible
        final LocalTime heureReference;
        if (!etatsVehicules.isEmpty() && etatsVehicules.get(0).getHeureDepart() != null) {
            heureReference = etatsVehicules.get(0).getHeureDepart().toLocalTime();
        } else {
            heureReference = null;
        }
        return etatsVehicules.stream()
                .filter(v -> v.getPlacesDisponibles() > 0)
                .filter(v -> {
                    if (heureReference == null || v.getHeureDisponibilite() == null) return true;
                    LocalTime dispo = LocalTime.parse(v.getHeureDisponibilite());
                    return !heureReference.isBefore(dispo);
                })
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
        return type.equals(TYPE_DIESEL) || type.equals("DIESEL");
    }


    public TrajetVehiculeDTO selectBestVehicle(List<TrajetVehiculeDTO> candidats,
                                               int nbPassagers,
                                               Map<Integer, Integer> vehiculeTrajetCounts) {
        return selectBestVehicleByPriority(candidats, nbPassagers, vehiculeTrajetCounts);
    }

    /**
     * Convertit une Reservation en ReservationAffecteeDTO.
     */
    private ReservationAffecteeDTO toReservationAffecteeDTO(Reservation r, int ordreVisite, double Distance) {
        return toReservationAffecteeDTO(r, ordreVisite, Distance, r.getNbPassager());
    }

    private ReservationAffecteeDTO toReservationAffecteeDTO(Reservation r,
                                                            int ordreVisite,
                                                            double Distance,
                                                            int nbPassagersAffectes) {
        ReservationAffecteeDTO dto = new ReservationAffecteeDTO();
        dto.setIdReservation(r.getIdReservation());
        dto.setDateHeureArrive(r.getDateHeureArrive());
        dto.setIdClient(r.getIdClient());
        dto.setNbPassager(nbPassagersAffectes);
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

    private String resolveClientSortKey(Reservation reservation) {
        if (reservation == null) {
            return "";
        }
        if (reservation.getClient() != null && reservation.getClient().getName() != null) {
            return reservation.getClient().getName().trim();
        }
        if (reservation.getIdClient() != null) {
            return reservation.getIdClient().trim();
        }
        return "";
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
            etat.setHeureDisponibilite(v.getHeureDisponibilite());
            etats.add(etat);
        }
        return etats;
    }

    private int getTempsAttenteGroupementMinutes() throws SQLException {
        return TEMPS_ATTENTE_GROUPEMENT_MINUTES;
    }

    private boolean isVehiculeDisponiblePourFenetre(TrajetVehiculeDTO vehicule,
                                                    int nbPassagers,
                                                    LocalDateTime fenetreFin,
                                                    Map<Integer, LocalDateTime> vehiculeReturnTimes) {
        if (vehicule.getPlacesDisponibles() <= 0) {
            return false;
        }

        LocalDateTime retour = vehiculeReturnTimes.get(vehicule.getVehiculeId());
        return retour == null || !retour.isAfter(fenetreFin);
    }

        private TrajetVehiculeDTO selectBestVehicleByLoadAndFuel(List<TrajetVehiculeDTO> candidats,
                                      int nbPassagers,
                                      Map<Integer, Integer> vehiculeTrajetCounts) {
        return selectBestVehicleByPriority(candidats, nbPassagers, vehiculeTrajetCounts);
        }

        private TrajetVehiculeDTO selectBestVehicleByPriority(List<TrajetVehiculeDTO> candidats,
                                  int nbPassagers,
                                  Map<Integer, Integer> vehiculeTrajetCounts) {
        if (candidats == null || candidats.isEmpty()) {
            return null;
        }

        Map<Integer, Integer> counts = vehiculeTrajetCounts != null ? vehiculeTrajetCounts : Collections.emptyMap();

        // 1) Priorite au vehicule avec capacite la plus proche du besoin (moins de places vides).
        int minPlacesVides = candidats.stream()
            .mapToInt(v -> Math.abs(v.getPlacesDisponibles() - nbPassagers))
            .min()
            .orElse(Integer.MAX_VALUE);

        List<TrajetVehiculeDTO> byCapacity = candidats.stream()
            .filter(v -> Math.abs(v.getPlacesDisponibles() - nbPassagers) == minPlacesVides)
            .collect(Collectors.toList());

        if (byCapacity.size() == 1) {
            return byCapacity.get(0);
        }

        // 2) Parmi eux, priorite au vehicule avec le moins de trajets.
        int minTrajets = byCapacity.stream()
            .mapToInt(v -> counts.getOrDefault(v.getVehiculeId(), 0))
                .min()
                .orElse(Integer.MAX_VALUE);

        List<TrajetVehiculeDTO> leastLoaded = byCapacity.stream()
            .filter(v -> counts.getOrDefault(v.getVehiculeId(), 0) == minTrajets)
                .collect(Collectors.toList());

        if (leastLoaded.size() == 1) {
            return leastLoaded.get(0);
        }

        // 3) Parmi egalite, priorite au diesel.
        List<TrajetVehiculeDTO> diesels = leastLoaded.stream()
                .filter(v -> isDiesel(v.getTypeVehicule()))
                .collect(Collectors.toList());

        List<TrajetVehiculeDTO> finalPool = diesels.isEmpty() ? leastLoaded : diesels;

        // 4) Derniere egalite: choix aleatoire.
        Random random = new Random();
        return finalPool.get(random.nextInt(finalPool.size()));
    }

    private int calculatePassengerCount(TrajetVehiculeDTO trajet) {
        if (trajet == null || trajet.getListeReservations() == null) {
            return 0;
        }
        return trajet.getListeReservations().stream()
                .mapToInt(ReservationAffecteeDTO::getNbPassager)
                .sum();
    }

    private void persistTrajetExecutionIfNeeded(TrajetVehiculeDTO trajet, LocalDate dateService) throws SQLException {
        if (trajet == null
                || trajet.getListeReservations() == null
                || trajet.getListeReservations().isEmpty()
                || trajet.getHeureDepart() == null
                || trajet.getHeureRetourPrevue() == null
                || dateService == null) {
            return;
        }

        boolean exists = trajetExecutionRepository.existsByVehicleDateAndHours(
                trajet.getVehiculeId(),
                dateService,
                trajet.getHeureDepart(),
                trajet.getHeureRetourPrevue());

        if (exists) {
            return;
        }

        // If same service window already exists with another vehicle (random tie on refresh),
        // keep a single row and just switch the vehicle.
        Integer existingTrajetId = trajetExecutionRepository.findTrajetIdByDateAndHours(
            dateService,
            trajet.getHeureDepart(),
            trajet.getHeureRetourPrevue());

        if (existingTrajetId != null) {
            trajetExecutionRepository.updateVehicleByTrajetId(existingTrajetId, trajet.getVehiculeId());
            return;
        }

        TrajetExecution execution = new TrajetExecution();
        execution.setVehiculeId(trajet.getVehiculeId());
        execution.setDateService(dateService);
        execution.setHeureDepart(trajet.getHeureDepart());
        execution.setHeureRetour(trajet.getHeureRetourPrevue());
        execution.setDistanceTotale(BigDecimal.valueOf(trajet.getDistanceTotale()).setScale(2, RoundingMode.HALF_UP));
        execution.setNombrePassagers(calculatePassengerCount(trajet));

        trajetExecutionRepository.insert(execution);
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
     * SPRINT 5: Regroupe les réservations selon le temps d'attente configuré.
     * 
     * Algorithmique :
     * 1. Les réservations doivent être TRIÉES par dateHeureArrive (du plus tôt au plus tard)
     * 2. Pour chaque réservation, chercher un groupe existant avec:
     *    - fenetre_min <= dateHeureArrive <= fenetre_max
     *    où fenetre = [première_arrivée, première_arrivée + temps_attente_groupement]
     * 3. Si trouvé -> ajouter à ce groupe
     *    Si non trouvé -> créer un nouveau groupe
     * 
     * @param reservationsSorted liste des réservations TRIÉES par dateHeureArrive
     * @return List<List<Reservation>> groupes de réservations, chacun dans sa fenêtre configurée
     */
    public List<List<Reservation>> regrouperReservationsParFenetre30Min(List<Reservation> reservationsSorted) throws SQLException {
        List<List<Reservation>> groupes = new ArrayList<>();
        int tempsAttenteGroupement = getTempsAttenteGroupementMinutes();
        
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
                    LocalDateTime finFenetre = premierArrivee.plusMinutes(tempsAttenteGroupement);

                    // Vérifier si la réservation est dans la fenêtre paramétrée
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
      * - Le temps d'attente configuré sert à créer la fenêtre de regroupement
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

        // Historique de charge par vehicule sur la date planifiee.
        Map<Integer, Integer> vehiculeTrajetCounts = new HashMap<>();
        for (Vehicule v : vehicules) {
            int count = trajetExecutionRepository.countTrajetsByVehicleAndDate(v.getIdVehicule(), date);
            vehiculeTrajetCounts.put(v.getIdVehicule(), count);
        }

        // 5. Affecter les réservations aux véhicules
        List<ReservationAffecteeDTO> nonAffectees = new ArrayList<>();
        int totalPassagers = 0;
        Set<Integer> vehiculesAvecNouveauTrajet = new HashSet<>();

        for (Reservation reservation : reservationsTriees) {
            int passagersRestants = reservation.getNbPassager();

            while (passagersRestants > 0) {
                List<TrajetVehiculeDTO> candidats = findCandidates(etatsVehicules, passagersRestants);
                if (candidats.isEmpty()) {
                    break;
                }

                TrajetVehiculeDTO vehiculeChoisi = selectBestVehicle(
                        candidats,
                        passagersRestants,
                        vehiculeTrajetCounts);
                if (vehiculeChoisi == null) {
                    break;
                }

                int placesDisponibles = vehiculeChoisi.getPlacesDisponibles();
                if (placesDisponibles <= 0) {
                    break;
                }

                int aAffecter = Math.min(passagersRestants, placesDisponibles);

                int dernierLieu = vehiculeChoisi.getListeReservations().isEmpty()
                        ? AEROPORT_ID
                        : vehiculeChoisi.getListeReservations()
                                .get(vehiculeChoisi.getListeReservations().size() - 1)
                                .getIdHotel();

                double distance = distanceService.getDistance(dernierLieu, reservation.getHotel().getIdHotel());

                int ordreVisite = vehiculeChoisi.getListeReservations().size() + 1;
                ReservationAffecteeDTO affectee = toReservationAffecteeDTO(reservation, ordreVisite, distance, aAffecter);

                boolean premierAffectationDuTrajet = vehiculeChoisi.getListeReservations().isEmpty();
                vehiculeChoisi.addReservation(affectee);

                if (premierAffectationDuTrajet && vehiculesAvecNouveauTrajet.add(vehiculeChoisi.getVehiculeId())) {
                    vehiculeTrajetCounts.put(
                            vehiculeChoisi.getVehiculeId(),
                            vehiculeTrajetCounts.getOrDefault(vehiculeChoisi.getVehiculeId(), 0) + 1);
                }

                totalPassagers += aAffecter;
                passagersRestants -= aAffecter;
            }

            if (passagersRestants > 0) {
                ReservationAffecteeDTO nonAffectee = toReservationAffecteeDTO(reservation, 0, 0, passagersRestants);
                nonAffectees.add(nonAffectee);
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

                // Sprint 6: persister le trajet effectue pour historiser la charge vehicule
                persistTrajetExecutionIfNeeded(trajet, date);
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
    * selon le temps d'attente configuré autour de leur heure d'arrivée.
     *
     * Algorithme :
     * 1. Récupère toutes les réservations du jour
     * 2. Les trie par heure d'arrivée croissante
    * 3. Les regroupe selon le temps d'attente configuré
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

        // 3. Grouper selon le temps d'attente configuré
        List<List<Reservation>> fenetres = regrouperReservationsParFenetre30Min(triees);

        // 4. Récupérer tous les véhicules disponibles (une seule fois)
        List<Vehicule> tousVehicules = getAllVehicules();
        int vitesseMoyenne = parametreService.getVitesseMoyenne();

        // États persistants sur la journée pour appliquer les nouvelles règles
        Map<Integer, LocalDateTime> vehiculeReturnTimes = new HashMap<>();
        Map<Integer, Integer> vehiculeTrajetCounts = new HashMap<>();
        for (Vehicule v : tousVehicules) {
            int count = trajetExecutionRepository.countTrajetsByVehicleAndDate(v.getIdVehicule(), date);
            vehiculeTrajetCounts.put(v.getIdVehicule(), count);
        }

        // Réservations non affectées à reconsidérer au prochain groupement
        List<Reservation> reservationsEnAttente = new ArrayList<>();

        // 5. Construire un GroupementDTO par fenêtre
        for (int numGroupe = 0; numGroupe < fenetres.size(); numGroupe++) {
            List<Reservation> groupe = fenetres.get(numGroupe);
            if (groupe.isEmpty()) {
                continue;
            }

            // Heure de départ de référence (si aucun véhicule en retour dans la fenêtre)
            LocalDateTime heureDepartReference = calculerHeureDepartGroupe(groupe);
            LocalDateTime fenetreDebut = groupe.get(0).getDateHeureArrive();
            int tempsAttenteGroupement = getTempsAttenteGroupementMinutes();
            LocalDateTime fenetreFin = fenetreDebut != null ? fenetreDebut.plusMinutes(tempsAttenteGroupement) : null;

            // Initialiser les états véhicules pour CE groupe
            List<TrajetVehiculeDTO> etatsVehicules = initVehiculeStates(tousVehicules, heureDepartReference);

            // Les réservations en attente sont réessayées dans ce groupement,
            // mais ne changent pas la fenêtre temporelle du groupement.
            List<Reservation> reservationsAPlanifier = new ArrayList<>();
            reservationsAPlanifier.addAll(reservationsEnAttente);
            reservationsAPlanifier.addAll(groupe);

            // Trier les réservations du groupe par nbPassager DESC (règle Sprint 4)
            // SPRINT 8: Appliquer tri de priorité NON-ASSIGNÉES EN PREMIER
            List<Reservation> reservationsTriees = sortReservationsWithUnassignedPriority(
                    reservationsAPlanifier,
                    reservationsEnAttente);

            // SPRINT 8: Variables pour tracker les assignations de fenêtre retour
            Map<Integer, LocalDateTime> minHeureArriveeParVehicule = new HashMap<>();

            int totalPassagers = 0;
            boolean departDepuisRetourVehicule = false;
            List<Reservation> nonAffecteesPourLeProchain = new ArrayList<>();
            LocalDateTime derniereReservationAssigneeDansFenetre = null;

            // SPRINT 8: Appliquer règle BEST-FIT AVEC SÉPARATION DEUX PHASES
            // Phase 1: Assigner NON-ASSIGNÉES en priorité absolue
            // Phase 2: Assigner NOUVELLES réservations avec BEST-FIT
            
            // Séparer non-assignées et nouvelles
            List<Reservation> nonAssigneesFiltre = new ArrayList<>(reservationsEnAttente);
            List<Reservation> nouvellesReservations = new ArrayList<>();
            
            for (Reservation res : reservationsAPlanifier) {
                if (!reservationsEnAttente.contains(res)) {
                    nouvellesReservations.add(res);
                }
            }
            
            // Trier les listes séparément par ordre de priorité
            List<Reservation> nonAssgineesTriees = sortReservationsByNbPassagerDesc(nonAssigneesFiltre, true); // Anciennes d'abord
            List<Reservation> nouvellesTriees = sortReservationsByNbPassagerDesc(nouvellesReservations, false); // Nombreux d'abord
            
            System.out.println("\n[DEBUG SPRINT 8] === ASSIGNATION 2-PHASES: NON-ASSIGNÉES PUIS NOUVELLES ===");
            System.out.println("Phase 1: " + nonAssgineesTriees.size() + " NON-ASSIGNÉES");
            System.out.println("Phase 2: " + nouvellesTriees.size() + " NOUVELLES");
            
            // ═══════════════════════════════════════════════════════════════════════════
            // PHASE 1: ASSIGNER LES NON-ASSIGNÉES COMPLÈTEMENT EN PRIORITÉ
            // ═══════════════════════════════════════════════════════════════════════════
            List<Reservation> nonAssgineesEnCours = new ArrayList<>(nonAssgineesTriees);
            boolean assignationEffectuee = true;
            
            while (assignationEffectuee && !nonAssgineesEnCours.isEmpty()) {
                assignationEffectuee = false;
                
                // Pour chaque véhicule avec places libres
                for (TrajetVehiculeDTO trajet : etatsVehicules) {
                    int placesLibres = trajet.getPlacesDisponibles();
                    
                    if (placesLibres <= 0) {
                        continue; // Pas de place
                    }
                    
                    // Chercher la meilleure NON-ASSIGNÉE pour ces places libres
                    int bestIndex = findBestReservationForAvailableSpace(nonAssgineesEnCours, placesLibres);
                    
                    if (bestIndex < 0) {
                        continue; // Aucune non-assignée compatible
                    }
                    
                    Reservation meilleure = nonAssgineesEnCours.get(bestIndex);
                    int nbAAffecter = Math.min(meilleure.getNbPassager(), placesLibres);
                    
                    System.out.println("  [PHASE 1] ✓ NON-ASSIGNÉE [PRIORITÉ]: R#" + meilleure.getIdReservation() + 
                            "(" + meilleure.getNbPassager() + "pax) → V" + trajet.getVehiculeId() + 
                            " (" + placesLibres + " places), assigne " + nbAAffecter + " pax");
                    
                    // Vérifier fenêtre retour
                    LocalDateTime retourVehicule = vehiculeReturnTimes.get(trajet.getVehiculeId());
                    if (retourVehicule != null
                            && fenetreDebut != null
                            && fenetreFin != null
                            && !retourVehicule.isBefore(fenetreDebut)
                            && !retourVehicule.isAfter(fenetreFin)) {
                        departDepuisRetourVehicule = true;
                    }
                    
                    int dernierLieu = trajet.getListeReservations().isEmpty()
                            ? AEROPORT_ID
                            : trajet.getListeReservations()
                                    .get(trajet.getListeReservations().size() - 1)
                                    .getIdHotel();
                    
                    double distance = distanceService.getDistance(dernierLieu, meilleure.getHotel().getIdHotel());
                    int ordreVisite = trajet.getListeReservations().size() + 1;
                    ReservationAffecteeDTO affectee = toReservationAffecteeDTO(meilleure, ordreVisite, distance, nbAAffecter);
                    trajet.addReservation(affectee);
                    
                    if (fenetreDebut != null
                            && fenetreFin != null
                            && !meilleure.getDateHeureArrive().isBefore(fenetreDebut)
                            && !meilleure.getDateHeureArrive().isAfter(fenetreFin)
                            && (derniereReservationAssigneeDansFenetre == null
                            || meilleure.getDateHeureArrive().isAfter(derniereReservationAssigneeDansFenetre))) {
                        derniereReservationAssigneeDansFenetre = meilleure.getDateHeureArrive();
                    }
                    
                    totalPassagers += nbAAffecter;
                    
                    // Mettre à jour réservation ou supprimer
                    if (nbAAffecter >= meilleure.getNbPassager()) {
                        // Complètement assignée
                        nonAssgineesEnCours.remove(bestIndex);
                        System.out.println("        → R#" + meilleure.getIdReservation() + " COMPLÈTEMENT assignée ✓");
                    } else {
                        // Partiellement assignée - créer reste
                        int reste = meilleure.getNbPassager() - nbAAffecter;
                        Reservation restante = assignPartially(meilleure, trajet, nbAAffecter);
                        nonAssgineesEnCours.set(bestIndex, restante);
                        System.out.println("        → R#" + meilleure.getIdReservation() + " partiellement assignée, " + 
                                reste + " pax restants");
                    }
                    
                    assignationEffectuee = true;
                    break; // Recommencer la boucle véhicules
                }
            }
            
            // Non-assignées restantes peu après Phase 1
            for (Reservation res : nonAssgineesEnCours) {
                System.out.println("  [PHASE 1] ✗ NON-ASSIGNÉE RESTANTE: R#" + res.getIdReservation() + 
                        "(" + res.getNbPassager() + "pax) → sera reportée au prochain groupement");
                nonAffecteesPourLeProchain.add(res);
            }
            
            // ═══════════════════════════════════════════════════════════════════════════
            // PHASE 2: ASSIGNER LES NOUVELLES RÉSERVATIONS AVEC BEST-FIT
            // ═══════════════════════════════════════════════════════════════════════════
            List<Reservation> nouvellesEnCours = new ArrayList<>(nouvellesTriees);
            assignationEffectuee = true;
            
            while (assignationEffectuee && !nouvellesEnCours.isEmpty()) {
                assignationEffectuee = false;
                
                for (TrajetVehiculeDTO trajet : etatsVehicules) {
                    int placesLibres = trajet.getPlacesDisponibles();
                    
                    if (placesLibres <= 0) {
                        continue; // Pas de place
                    }
                    
                    int bestIndex = findBestReservationForAvailableSpace(nouvellesEnCours, placesLibres);
                    
                    if (bestIndex < 0) {
                        continue; // Aucune nouvelle compatible
                    }
                    
                    Reservation meilleure = nouvellesEnCours.get(bestIndex);
                    int nbAAffecter = Math.min(meilleure.getNbPassager(), placesLibres);
                    
                    System.out.println("  [PHASE 2] ✓ NOUVELLE [BEST-FIT]: R#" + meilleure.getIdReservation() + 
                            "(" + meilleure.getNbPassager() + "pax) → V" + trajet.getVehiculeId() + 
                            " (" + placesLibres + " places), assigne " + nbAAffecter + " pax");
                    
                    // Vérifier fenêtre retour
                    LocalDateTime retourVehicule = vehiculeReturnTimes.get(trajet.getVehiculeId());
                    if (retourVehicule != null
                            && fenetreDebut != null
                            && fenetreFin != null
                            && !retourVehicule.isBefore(fenetreDebut)
                            && !retourVehicule.isAfter(fenetreFin)) {
                        departDepuisRetourVehicule = true;
                    }
                    
                    int dernierLieu = trajet.getListeReservations().isEmpty()
                            ? AEROPORT_ID
                            : trajet.getListeReservations()
                                    .get(trajet.getListeReservations().size() - 1)
                                    .getIdHotel();
                    
                    double distance = distanceService.getDistance(dernierLieu, meilleure.getHotel().getIdHotel());
                    int ordreVisite = trajet.getListeReservations().size() + 1;
                    ReservationAffecteeDTO affectee = toReservationAffecteeDTO(meilleure, ordreVisite, distance, nbAAffecter);
                    trajet.addReservation(affectee);
                    
                    if (fenetreDebut != null
                            && fenetreFin != null
                            && !meilleure.getDateHeureArrive().isBefore(fenetreDebut)
                            && !meilleure.getDateHeureArrive().isAfter(fenetreFin)
                            && (derniereReservationAssigneeDansFenetre == null
                            || meilleure.getDateHeureArrive().isAfter(derniereReservationAssigneeDansFenetre))) {
                        derniereReservationAssigneeDansFenetre = meilleure.getDateHeureArrive();
                    }
                    
                    totalPassagers += nbAAffecter;
                    
                    // Mettre à jour réservation ou supprimer
                    if (nbAAffecter >= meilleure.getNbPassager()) {
                        // Complètement assignée
                        nouvellesEnCours.remove(bestIndex);
                        System.out.println("        → R#" + meilleure.getIdReservation() + " COMPLÈTEMENT assignée ✓");
                    } else {
                        // Partiellement assignée - créer reste
                        int reste = meilleure.getNbPassager() - nbAAffecter;
                        Reservation restante = assignPartially(meilleure, trajet, nbAAffecter);
                        nouvellesEnCours.set(bestIndex, restante);
                        System.out.println("        → R#" + meilleure.getIdReservation() + " partiellement assignée, " + 
                                reste + " pax restants");
                    }
                    
                    assignationEffectuee = true;
                    break; // Recommencer la boucle véhicules
                }
            }
            
            // Nouvelles non assignées
            for (Reservation res : nouvellesEnCours) {
                System.out.println("  [PHASE 2] ✗ NOUVELLE NON-ASSIGNÉE: R#" + res.getIdReservation() + 
                        "(" + res.getNbPassager() + "pax) → sera reportée au prochain groupement");
                nonAffecteesPourLeProchain.add(res);
            }

            // Regle metier: depart base = derniere reservation assignee de la fenetre.
            LocalDateTime heureDepartBaseGroupe = derniereReservationAssigneeDansFenetre != null
                    ? derniereReservationAssigneeDansFenetre
                    : heureDepartReference;

            // Heure de depart par voiture:
            // max(heure de base du groupement, heure de retour du vehicule si retour dans la fenetre).
            for (TrajetVehiculeDTO trajet : etatsVehicules) {
                if (trajet.getListeReservations().isEmpty()) {
                    continue;
                }

                LocalDateTime heureDepartVoiture = heureDepartBaseGroupe;
                LocalDateTime retourVehicule = vehiculeReturnTimes.get(trajet.getVehiculeId());
                boolean retourDansFenetre = retourVehicule != null
                        && fenetreDebut != null
                        && fenetreFin != null
                        && !retourVehicule.isBefore(fenetreDebut)
                        && !retourVehicule.isAfter(fenetreFin);

                if (retourDansFenetre && retourVehicule != null
                        && (heureDepartVoiture == null || retourVehicule.isAfter(heureDepartVoiture))) {
                    heureDepartVoiture = retourVehicule;
                }

                trajet.setHeureDepart(heureDepartVoiture);
            }

            // Non affectées => prochain groupement (leur heure n'influence pas la fenêtre suivante)
            reservationsEnAttente = nonAffecteesPourLeProchain;

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
                LocalDateTime heureActuelle = trajet.getHeureDepart() != null ? trajet.getHeureDepart() : heureDepartReference;
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

                // Sprint 6: persister le trajet effectue pour historiser la charge vehicule
                persistTrajetExecutionIfNeeded(trajet, date);

                if (trajet.getHeureRetourPrevue() != null) {
                    vehiculeReturnTimes.put(trajet.getVehiculeId(), trajet.getHeureRetourPrevue());
                }
                vehiculeTrajetCounts.put(
                        trajet.getVehiculeId(),
                        vehiculeTrajetCounts.getOrDefault(trajet.getVehiculeId(), 0) + 1);
            }

            // ═══════════════════════════════════════════════════════════════════════════
            // SPRINT 8: FENÊTRE DE RETOUR - Assigner les réservations non-assignées
            // aux véhicules qui reviennent dans les 30 prochaines minutes
            // ═══════════════════════════════════════════════════════════════════════════
            System.out.println("\n[DEBUG SPRINT 8] === RECHERCHE ASSIGNATIONS FENÊTRE RETOUR ===");
            
            for (TrajetVehiculeDTO trajet : etatsVehicules) {
                if (trajet.getListeReservations().isEmpty() || trajet.getHeureRetourPrevue() == null) {
                    continue;
                }
                
                // Chercher réservations dans fenêtre retour
                List<Reservation> reservationsEnFenetre = findReservationsInReturnWindow(
                        nonAffecteesPourLeProchain,
                        trajet.getHeureRetourPrevue(),
                        TEMPS_ATTENTE_GROUPEMENT_MINUTES);
                
                if (reservationsEnFenetre.isEmpty()) {
                    continue;
                }
                
                System.out.println("Véhicule V" + trajet.getVehiculeId() + 
                        " retour " + trajet.getHeureRetourPrevue() + 
                        " : " + reservationsEnFenetre.size() + " réservations trouvées dans fenêtre");
                
                // Assigner les réservations trouvées
                for (Reservation res : reservationsEnFenetre) {
                    int placesDisponibles = trajet.getPlacesDisponibles();
                    
                    if (placesDisponibles <= 0) {
                        System.out.println("  → V" + trajet.getVehiculeId() + " plus de places disponibles");
                        continue;
                    }
                    
                    int aAffecter = Math.min(res.getNbPassager(), placesDisponibles);
                    
                    int dernierLieu = trajet.getListeReservations().isEmpty()
                            ? AEROPORT_ID
                            : trajet.getListeReservations()
                                    .get(trajet.getListeReservations().size() - 1)
                                    .getIdHotel();
                    
                    double distance = distanceService.getDistance(dernierLieu, res.getHotel().getIdHotel());
                    int ordreVisite = trajet.getListeReservations().size() + 1;
                    ReservationAffecteeDTO affectee = toReservationAffecteeDTO(res, ordreVisite, distance, aAffecter);
                    trajet.addReservation(affectee);
                    
                    // Tracker heure min pour ajuster départ
                    LocalDateTime minHeure = minHeureArriveeParVehicule.get(trajet.getVehiculeId());
                    if (minHeure == null || res.getDateHeureArrive().isBefore(minHeure)) {
                        minHeureArriveeParVehicule.put(trajet.getVehiculeId(), res.getDateHeureArrive());
                    }
                    
                    System.out.println("  ✓ Assigné R#" + res.getIdReservation() + 
                            " : " + aAffecter + "/" + res.getNbPassager() + " pax à V" + trajet.getVehiculeId());
                    
                    totalPassagers += aAffecter;
                    
                    // Si partiellement assignée, créer réservation reste
                    if (aAffecter < res.getNbPassager()) {
                        Reservation reste = assignPartially(res, trajet, aAffecter);
                        if (reste != null) {
                            nonAffecteesPourLeProchain.remove(res);
                            nonAffecteesPourLeProchain.add(reste);
                        }
                    } else {
                        // Complètement assignée, retirer
                        nonAffecteesPourLeProchain.remove(res);
                    }
                }
            }
            
            // Recalculer à cause des nouveaux passagers dans fenêtre retour
            System.out.println("\n[DEBUG SPRINT 8] === RECALCUL APRÈS FENÊTRE RETOUR ===");
            for (TrajetVehiculeDTO trajet : etatsVehicules) {
                if (!trajet.getListeReservations().isEmpty() && minHeureArriveeParVehicule.containsKey(trajet.getVehiculeId())) {
                    
                    // Calculer distances aéroport → hôtel
                    for (ReservationAffecteeDTO res : trajet.getListeReservations()) {
                        double distDepuisAeroport = distanceService.getDistance(AEROPORT_ID, res.getIdHotel());
                        res.setDistanceDepuisAeroport(distDepuisAeroport);
                    }
                    
                    // Trier par distance aéroport
                    List<ReservationAffecteeDTO> ordonnees = trajet.getListeReservations().stream()
                            .sorted((r1, r2) -> {
                                int cmpDist = Double.compare(r1.getDistanceDepuisAeroport(), r2.getDistanceDepuisAeroport());
                                if (cmpDist != 0) return cmpDist;
                                String n1 = r1.getNomHotel() != null ? r1.getNomHotel() : "";
                                String n2 = r2.getNomHotel() != null ? r2.getNomHotel() : "";
                                return n1.compareToIgnoreCase(n2);
                            })
                            .collect(Collectors.toList());
                    
                    // Recalculer heures de passage
                    LocalDateTime heureDepart = minHeureArriveeParVehicule.get(trajet.getVehiculeId());
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
                    trajet.setHeureDepart(heureDepart);
                    
                    // Recalculer distances et heure retour
                    double distParcourue = calculerDistanceTotaleVehicule(trajet);
                    trajet.setDistanceParcourue(distParcourue);
                    
                    double distTotale = calculerDistanceTrajetReelle(trajet);
                    trajet.setDistanceTotale(distTotale);
                    
                    LocalDateTime nouvelleHeureRetour = calculerHeureRetour(trajet);
                    trajet.setHeureRetourPrevue(nouvelleHeureRetour);
                    
                    persistTrajetExecutionIfNeeded(trajet, date);
                    
                    if (nouvelleHeureRetour != null) {
                        vehiculeReturnTimes.put(trajet.getVehiculeId(), nouvelleHeureRetour);
                        System.out.println("V" + trajet.getVehiculeId() + 
                                " → nouvelle heure retour: " + nouvelleHeureRetour);
                    }
                }
            }

            // Ne garder que les véhicules actifs (avec au moins une réservation)
            List<TrajetVehiculeDTO> trajetsActifs = etatsVehicules.stream()
                    .filter(t -> !t.getListeReservations().isEmpty())
                    .collect(Collectors.toList());

            LocalDateTime heureDepartAffichee = heureDepartBaseGroupe;
            for (TrajetVehiculeDTO trajetActif : trajetsActifs) {
                if (trajetActif.getHeureDepart() != null
                        && (heureDepartAffichee == null || trajetActif.getHeureDepart().isAfter(heureDepartAffichee))) {
                    heureDepartAffichee = trajetActif.getHeureDepart();
                }
            }

            // Construire le GroupementDTO
            GroupementDTO groupementDTO = new GroupementDTO();
            groupementDTO.setNumeroGroupe(numGroupe + 1);
            if (fenetreDebut != null) {
                groupementDTO.setFenetreDebut(fenetreDebut);
                groupementDTO.setFenetreFin(fenetreDebut.plusMinutes(tempsAttenteGroupement));
            }
                groupementDTO.setHeureDepart(heureDepartAffichee);
            groupementDTO.setTrajets(trajetsActifs);
                groupementDTO.setTotalReservations(reservationsAPlanifier.size());
            groupementDTO.setTotalPassagers(totalPassagers);
                groupementDTO.setDepartInfo(departDepuisRetourVehicule
                    ? ""
                    : "");

                List<ReservationAffecteeDTO> nonAffecteesDTO = nonAffecteesPourLeProchain.stream()
                    .map(r -> toReservationAffecteeDTO(r, 0, 0))
                    .collect(Collectors.toList());
                groupementDTO.setReservationsNonAffectees(nonAffecteesDTO);

            groupements.add(groupementDTO);
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // SPRINT 8: CRÉER GROUPEMENT POUR LES NON-ASSIGNÉES RESTANTES
        // Si il y a des réservations non-assignées, créer un groupement
        // commençant à la prochaine heure retour de véhicule
        // ═══════════════════════════════════════════════════════════════════════════
        if (!reservationsEnAttente.isEmpty()) {
            System.out.println("\n[DEBUG SPRINT 8] === CRÉATION GROUPEMENT POUR NON-ASSIGNÉES ===");
            System.out.println("Réservations non-assignées restantes: " + reservationsEnAttente.size());
            
            // Trouver la prochaine heure retour de tous les véhicules
            LocalDateTime prochainRetourVehicule = vehiculeReturnTimes.values().stream()
                    .filter(h -> h != null)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            
            if (prochainRetourVehicule != null) {
                System.out.println("Prochaine heure retour véhicule: " + prochainRetourVehicule);
                
                // Créer la fenêtre de 30 min à partir du retour
                LocalDateTime fenetreDebut = prochainRetourVehicule;
                LocalDateTime fenetreFin = prochainRetourVehicule.plusMinutes(TEMPS_ATTENTE_GROUPEMENT_MINUTES);
                
                // Créer états véhicules pour ce nouveau groupement
                List<TrajetVehiculeDTO> etatsVehiculesSuivant = initVehiculeStates(tousVehicules, prochainRetourVehicule);
                
                // Trier NON-ASSIGNÉES avec priorité
                List<Reservation> reservationsTrieesSuivant = sortReservationsWithUnassignedPriority(
                        reservationsEnAttente,
                        reservationsEnAttente); // Toutes sont non-assignées
                
                int totalPassagersSuivant = 0;
                List<Reservation> nonAffecteesRestantes = new ArrayList<>();
                
                // Assigner les non-assignées au prochain groupement
                for (Reservation reservation : reservationsTrieesSuivant) {
                    if (reservation.getDateHeureArrive() == null) {
                        nonAffecteesRestantes.add(reservation);
                        continue;
                    }
                    
                    int passagersRestants = reservation.getNbPassager();
                    boolean affectationEffectuee = false;
                    
                    while (passagersRestants > 0) {
                        final int passagersDemandes = passagersRestants;
                        
                        List<TrajetVehiculeDTO> candidats = etatsVehiculesSuivant.stream()
                                .filter(v -> isVehiculeDisponiblePourFenetre(v,
                                        passagersDemandes,
                                        fenetreFin,
                                        vehiculeReturnTimes))
                                .collect(Collectors.toList());
                        
                        if (candidats.isEmpty()) {
                            break;
                        }
                        
                        TrajetVehiculeDTO vehiculeChoisi = selectBestVehicleByLoadAndFuel(
                                candidats,
                                passagersDemandes,
                                vehiculeTrajetCounts);
                        
                        if (vehiculeChoisi == null) {
                            break;
                        }
                        
                        int placesDisponibles = vehiculeChoisi.getPlacesDisponibles();
                        if (placesDisponibles <= 0) {
                            break;
                        }
                        
                        int aAffecter = Math.min(passagersRestants, placesDisponibles);
                        
                        int dernierLieu = vehiculeChoisi.getListeReservations().isEmpty()
                                ? AEROPORT_ID
                                : vehiculeChoisi.getListeReservations()
                                        .get(vehiculeChoisi.getListeReservations().size() - 1)
                                        .getIdHotel();
                        
                        double distance = distanceService.getDistance(dernierLieu, reservation.getHotel().getIdHotel());
                        int ordreVisite = vehiculeChoisi.getListeReservations().size() + 1;
                        ReservationAffecteeDTO affectee = toReservationAffecteeDTO(reservation, ordreVisite, distance, aAffecter);
                        vehiculeChoisi.addReservation(affectee);
                        affectationEffectuee = true;
                        
                        totalPassagersSuivant += aAffecter;
                        passagersRestants -= aAffecter;
                    }
                    
                    if (!affectationEffectuee || passagersRestants > 0) {
                        Reservation partielle = new Reservation();
                        partielle.setIdReservation(reservation.getIdReservation());
                        partielle.setDateHeureArrive(reservation.getDateHeureArrive());
                        partielle.setIdClient(reservation.getIdClient());
                        partielle.setNbPassager(passagersRestants > 0 ? passagersRestants : reservation.getNbPassager());
                        partielle.setHotel(reservation.getHotel());
                        partielle.setClient(reservation.getClient());
                        partielle.setClientId(reservation.getClientId());
                        nonAffecteesRestantes.add(partielle);
                    }
                }
                
                // Traiter les trajets (distances, heures, retours)
                for (TrajetVehiculeDTO trajet : etatsVehiculesSuivant) {
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
                    LocalDateTime heureActuelle = trajet.getHeureDepart() != null ? trajet.getHeureDepart() : prochainRetourVehicule;
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
                    
                    persistTrajetExecutionIfNeeded(trajet, date);
                    
                    if (trajet.getHeureRetourPrevue() != null) {
                        vehiculeReturnTimes.put(trajet.getVehiculeId(), trajet.getHeureRetourPrevue());
                    }
                    vehiculeTrajetCounts.put(
                            trajet.getVehiculeId(),
                            vehiculeTrajetCounts.getOrDefault(trajet.getVehiculeId(), 0) + 1);
                }
                
                // Ne garder que les véhicules actifs
                List<TrajetVehiculeDTO> trajetsActifsSuivant = etatsVehiculesSuivant.stream()
                        .filter(t -> !t.getListeReservations().isEmpty())
                        .collect(Collectors.toList());
                
                if (!trajetsActifsSuivant.isEmpty()) {
                    // Créer le GroupementDTO pour les non-assignées
                    GroupementDTO groupementSuivant = new GroupementDTO();
                    groupementSuivant.setNumeroGroupe(groupements.size() + 1);
                    groupementSuivant.setFenetreDebut(fenetreDebut);
                    groupementSuivant.setFenetreFin(fenetreFin);
                    groupementSuivant.setHeureDepart(prochainRetourVehicule);
                    groupementSuivant.setTrajets(trajetsActifsSuivant);
                    groupementSuivant.setTotalReservations(reservationsTrieesSuivant.size());
                    groupementSuivant.setTotalPassagers(totalPassagersSuivant);
                    groupementSuivant.setDepartInfo("NON-ASSIGNÉES - Fenêtre Retour");
                    
                    List<ReservationAffecteeDTO> nonAffecteesDTO = nonAffecteesRestantes.stream()
                            .map(r -> toReservationAffecteeDTO(r, 0, 0))
                            .collect(Collectors.toList());
                    groupementSuivant.setReservationsNonAffectees(nonAffecteesDTO);
                    
                    groupements.add(groupementSuivant);
                    System.out.println("✓ Groupement créé avec " + trajetsActifsSuivant.size() + " trajets, " + 
                            totalPassagersSuivant + " passagers assignés");
                }
            }
        }

        return groupements;
    }
}