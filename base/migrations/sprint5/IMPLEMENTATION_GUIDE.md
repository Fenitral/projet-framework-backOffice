# GUIDE D'IMPLÉMENTATION - Sprint 5 Planification

## Vue d'ensemble

```
AssignationService.planifier(LocalDate date) 
  ↓
  ├─ 1. getReservationsByDate(date)
  ├─ 2. trierReservationsParHeureArrivee()
  ├─ 3. regrouperReservationsParFenetre30Min()
  │   Pour chaque groupe:
  ├─ 4. calculerHeureDepartGroupe()
  ├─ 5. assignerVehicules()
  ├─ 6. calculerDistanceTotaleVehicule()
  ├─ 7. calculerHeureRetour()
  ├─ 8. buildPlanificationLignes()
  └─ 9. sauvegarderEnBD()
  ↓
  return List<PlanificationDTO>
```

---

## Méthodes à implémenter

### 1️⃣ Trier réservations par heure d'arrivée

```java
/**
 * Trier réservations par heure d'arrivé ascendant.
 * 
 * @param reservations liste non-triée
 * @return reservations triées par dateHeureArrive ASC
 */
private List<Reservation> trierReservationsParHeureArrivee(List<Reservation> reservations) {
    return reservations.stream()
        .sorted(Comparator.comparing(Reservation::getDateHeureArrive))
        .collect(Collectors.toList());
}
```

**Test:**
```java
@Test
void trierReservationsParHeureArrivee_doitTrierAsc() {
    // Réservations avec heures: [10:30, 09:00, 10:00]
    // Résultat attendu: [09:00, 10:00, 10:30]
}
```

---

### 2️⃣ Regrouper réservations par fenêtre 30 min

```java
/**
 * Regrouper réservations par fenêtre de 30 minutes.
 * 
 * La fenêtre commence à l'heure du premier vol et se termine 30 min après.
 * 
 * Exemple:
 *   Réservation 1: arrive 09:00 → fenêtre [09:00, 09:30]
 *   Réservation 2: arrive 09:15 → fenêtre [09:00, 09:30] (même groupe)
 *   Réservation 3: arrive 09:35 → fenêtre [09:35, 10:05] (nouveau groupe)
 * 
 * @param reservations triées par heure d'arrivée
 * @return Map<LocalDateTime, List<Reservation>> clé=heure_debut_fenetre
 */
private Map<LocalDateTime, List<Reservation>> regrouperReservationsParFenetre30Min(
        List<Reservation> reservations) {
    
    Map<LocalDateTime, List<Reservation>> groupes = new LinkedHashMap<>();
    
    for (Reservation res : reservations) {
        LocalDateTime arrivee = res.getDateHeureArrive();
        
        // Chercher fenêtre existante
        LocalDateTime fenetreExistante = groupes.keySet().stream()
            .filter(debut -> arrivee.isBefore(debut.plusMinutes(30)))
            .findFirst()
            .orElse(null);
        
        if (fenetreExistante != null) {
            // Ajouter à fenêtre existante
            groupes.get(fenetreExistante).add(res);
        } else {
            // Créer nouvelle fenêtre
            List<Reservation> nouveauGroupe = new ArrayList<>();
            nouveauGroupe.add(res);
            groupes.put(arrivee, nouveauGroupe);
        }
    }
    
    return groupes;
}
```

**Tests:**
```java
@Test
void regrouperReservationsParFenetre30Min_uneReservation() {
    // 1 réservation → 1 groupe
}

@Test
void regrouperReservationsParFenetre30Min_deuxReservations30Min() {
    // Réservations [09:00, 09:15] → 1 groupe (fenêtre [09:00, 09:30])
}

@Test
void regrouperReservationsParFenetre30Min_deuxReservationsHorsFenetre() {
    // Réservations [09:00, 09:35] → 2 groupes
}
```

---

### 3️⃣ Calculer heure départ groupe

```java
/**
 * Calculer heure de départ pour un groupe de réservations.
 * 
 * RÈGLE:
 *   heure_depart = MAX(heure_arrivee) + 30 MINUTES
 *   Si heure_depart < 08:00 → forcer 08:00
 * 
 * @param reservations du groupe
 * @return LocalDateTime heure départ calculée
 */
private LocalDateTime calculerHeureDepartGroupe(List<Reservation> reservations) {
    LocalDateTime maxArrivee = reservations.stream()
        .map(Reservation::getDateHeureArrive)
        .max(Comparator.naturalOrder())
        .orElse(LocalDateTime.now());
    
    LocalDateTime heureDepart = maxArrivee.plusMinutes(30);
    
    // Minimum légal 08:00
    LocalDateTime minDepart = maxArrivee.withHour(8).withMinute(0).withSecond(0);
    
    return heureDepart.isBefore(minDepart) ? minDepart : heureDepart;
}
```

**Tests:**
```java
@Test
void calculerHeureDepartGroupe_09h15_depart09h45() {
    // Max arrivée 09:15 → départ 09:45
}

@Test
void calculerHeureDepartGroupe_07h20_forceDeparth08h00() {
    // Max arrivée 07:20 → départ forcé 08:00 (minimum)
}

@Test
void calculerHeureDepartGroupe_10h30_depart11h00() {
    // Max arrivée 10:30 → départ 11:00
}
```

---

### 4️⃣ Calculer distance totale véhicule

```java
/**
 * Calculer distance totale d'un trajet véhicule.
 * 
 * Formule:
 *   distance_totale = Aéroport → 1er_hôtel → 2e_hôtel → ... → Aéroport
 * 
 * @param trajet contenant vehiculeId et liste réservations avec hôtels
 * @return distance en km
 */
public int calculerDistanceTotaleVehicule(TrajetVehiculeDTO trajet) {
    int distanceTotale = 0;
    List<Integer> hotelIds = extraireHotelsDuTrajet(trajet);
    
    // Aéroport (NULL) → 1er hôtel
    if (!hotelIds.isEmpty()) {
        distanceTotale += distanceRepository.getDistanceValue(0, hotelIds.get(0));
    }
    
    // Hôtel à hôtel
    for (int i = 0; i < hotelIds.size() - 1; i++) {
        distanceTotale += distanceRepository.getDistanceValue(
            hotelIds.get(i), 
            hotelIds.get(i + 1)
        );
    }
    
    // Dernier hôtel → Aéroport (NULL)
    if (!hotelIds.isEmpty()) {
        distanceTotale += distanceRepository.getDistanceValue(
            hotelIds.get(hotelIds.size() - 1), 
            0
        );
    }
    
    return distanceTotale;
}
```

**Tests:**
```java
@Test
void calculerDistanceTotaleVehicule_unSeulHotel() {
    // Aéroport → Hôtel A → Aéroport
    // Distance = 12 + 12 = 24 km
}

@Test
void calculerDistanceTotaleVehicule_deuxHotels() {
    // Aéroport → Hôtel B → Hôtel C → Aéroport
    // Distance = 8 + 3 + 15 = 26 km
}
```

---

### 5️⃣ Calculer heure retour aéroport

```java
/**
 * Calculer heure retour au aéroport.
 * 
 * Formule:
 *   heure_retour = heure_depart 
 *                + (distance_totale / vitesse_moyenne) en minutes
 * 
 * Données depuis local.parametre:
 *   - vitesse_moyenne (ex: 50 km/h)
 * 
 * @param trajet avec heure_depart et distance_totale déjà calculées
 * @return LocalDateTime heure retour
 */
public LocalDateTime calculerHeureRetour(TrajetVehiculeDTO trajet) {
    // Récupérer paramètres de BD
    int vitesseMoyenne = parametreRepository.getValeur("vitesse_moyenne"); // 50 km/h
    
    // Temps route en minutes: (distance / vitesse) * 60
    long tempsRoute = (trajet.getDistance_totale() * 60) / vitesseMoyenne;
    
    // Temps attente total
    long tempsAttentesTotal = nbHotels * tempsAttenteHotel;
    
    // Heure retour
    return trajet.getHeure_depart_aeroport()
        .plusMinutes(tempsRoute)
        .plusMinutes(tempsAttentesTotal);
}
```

**Tests:**
```java
@Test
void calculerHeureRetour_23kmVitesse50kph_10minParHotel() {
    // Départ: 09:45
    // Route: 23 km ÷ 50 km/h = 27.6 min ≈ 28 min
    // Hôtels: 2 × 10 = 20 min
    // Retour: 09:45 + 28 + 20 = 10:33
}
```

---

### 6️⃣ Construire PlanificationDTO

```java
/**
 * Construire la ligne résultat pour affichage écran.
 * 
 * @param vehicule assigné
 * @param reservations du groupe
 * @param heureDepart du groupe
 * @param distanceTotale calculée
 * @param heureRetour calculée
 * @return PlanificationDTO formatée
 */
private PlanificationDTO buildPlanificationLignes(
        Vehicule vehicule,
        List<Reservation> reservations,
        LocalDateTime heureDepart,
        int distanceTotale,
        LocalDateTime heureRetour) {
    
    PlanificationDTO dto = new PlanificationDTO();
    
    // Heure départ
    dto.setHeure_depart(heureDepart);
    
    // Véhicule
    VehiculeDTO vehDto = new VehiculeDTO();
    vehDto.setId(vehicule.getIdVehicule());
    vehDto.setReference(vehicule.getReference());
    vehDto.setNbplace(vehicule.getNbPlace());
    vehDto.setTypevehicule(vehicule.getTypeVehicule());
    vehDto.setStatut(vehicule.getStatut());
    dto.setVehicule(vehDto);
    
    // Réservations avec détails clients
    List<ReservationLigneDTO> reservationLignes = reservations.stream()
        .map(res -> {
            ReservationLigneDTO ligne = new ReservationLigneDTO();
            ligne.setId_reservation(res.getIdReservation());
            ligne.setId_client(res.getClient().getClientId());
            ligne.setName(res.getClient().getName());
            ligne.setEmail(res.getClient().getEmail());
            ligne.setNbpassager(res.getNbPassager());
            ligne.setId_hotel(res.getHotel().getIdHotel());
            ligne.setHotel_name(res.getHotel().getNom());
            return ligne;
        })
        .collect(Collectors.toList());
    dto.setReservations(reservationLignes);
    
    // Hôtels visités (unique)
    List<Integer> hotels = reservations.stream()
        .map(res -> res.getHotel().getIdHotel())
        .distinct()
        .collect(Collectors.toList());
    dto.setHotels(hotels);
    
    // Distance et heure retour
    dto.setDistance_totale(distanceTotale);
    dto.setHeure_retour_aeroport(heureRetour);
    
    return dto;
}
```

---

### 7️⃣ Méthode principale planifier()

```java
/**
 * Planifier tous les trajets pour une date donnée.
 * 
 * @param date de planification
 * @return List<PlanificationDTO> résultats affichage écran
 */
public List<PlanificationDTO> planifier(LocalDate date) throws SQLException {
    
    // 1. Récupérer réservations du jour
    List<Reservation> reservations = reservationRepository.findByDate(date);
    if (reservations.isEmpty()) {
        return new ArrayList<>();
    }
    
    // 2. Trier par heure d'arrivée
    reservations = trierReservationsParHeureArrivee(reservations);
    
    // 3. Regrouper par fenêtre 30 min
    Map<LocalDateTime, List<Reservation>> groupes = 
        regrouperReservationsParFenetre30Min(reservations);
    
    // 4. Pour chaque groupe
    List<PlanificationDTO> resultats = new ArrayList<>();
    
    for (Map.Entry<LocalDateTime, List<Reservation>> entree : groupes.entrySet()) {
        List<Reservation> groupeRes = entree.getValue();
        
        // 4a. Calculer heure départ
        LocalDateTime heureDepart = calculerHeureDepartGroupe(groupeRes);
        
        // 4b. Assigner véhicules (à implémenter dans classe séparée)
        List<Vehicule> vehiculesAssignes = assignerVehiculesPourGroupe(groupeRes);
        
        // 4c-d. Pour chaque véhicule
        for (Vehicule vehicule : vehiculesAssignes) {
            List<Reservation> resVehicule = filtrerReservationsVehicule(groupeRes, vehicule);
            
            // Calculer distance
            int distanceTotale = calculerDistanceTotaleVehicule(resVehicule);
            
            // Calculer heure retour
            LocalDateTime heureRetour = calculerHeureRetour(
                heureDepart, 
                distanceTotale, 
                resVehicule
            );
            
            // Construire résultat
            PlanificationDTO planif = buildPlanificationLignes(
                vehicule, 
                resVehicule, 
                heureDepart, 
                distanceTotale, 
                heureRetour
            );
            
            resultats.add(planif);
        }
    }
    
    return resultats;
}
```

---

## Classes/DTOs à modifier

### PlanificationDTO.java

```java
public class PlanificationDTO {
    private LocalDateTime heure_depart;
    private VehiculeDTO vehicule;
    private List<ReservationLigneDTO> reservations;
    private List<Integer> hotels;  // IDs hôtels visités
    private int distance_totale;   // km
    private LocalDateTime heure_retour_aeroport;
    
    // Getters/Setters...
}

public class ReservationLigneDTO {
    private int id_reservation;
    private int id_client;
    private String name;
    private String email;
    private int nbpassager;
    private int id_hotel;
    private String hotel_name;
    // Getters/Setters...
}

public class VehiculeDTO {
    private int id;
    private String reference;
    private int nbplace;
    private String typevehicule;
    private String statut;
    // Getters/Setters...
}
```

### TrajetVehiculeDTO.java

```java
public class TrajetVehiculeDTO {
    private Vehicule vehicule;
    private List<Reservation> reservations;
    private int distance_totale;
    private LocalDateTime heure_depart_aeroport;
    private LocalDateTime heure_retour_aeroport;
    private int ordre_trajet;
    // Getters/Setters...
}
```

---

## Tests à créer (AssignationServiceTest.java)

```java
@Test
void planifier_uneReservation_seulGroupe() {
    // 1 réservation seule → 1 groupe, 1 véhicule
    // Vérifier: heure_depart, distance, heure_retour
}

@Test
void planifier_plusieursReservationsDans30Min_memeDepart() {
    // Réservations [09:00, 09:15, 09:20] → 1 groupe
    // Vérifier: heure_depart = 09:50 (max + 30 min)
    // Véhicule peut contenir les passagers
}

@Test
void planifier_reservationHorsFenetre_nouveauDepart() {
    // Réservations [09:00, 09:35] → 2 groupes
    // Vérifier: groupe 1 départ 09:30, groupe 2 départ 10:05
}

@Test
void planifier_capaciteInsuftisante_deuxVehicules() {
    // 2 réservations [5p + 7p], 1 véhicule 4p → 2 véhicules assignés
}
```
