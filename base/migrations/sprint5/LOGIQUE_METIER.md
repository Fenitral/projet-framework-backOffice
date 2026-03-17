/**
 * LOGIQUE MÉTIER SPRINT 5 - PLANIFICATION DES TRAJETS
 * ALIGNÉE AVEC TODO: [Service] Implémenter algorithme calcul des départs
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. REGROUPEMENT DES CLIENTS (Fenêtre de Temps 30 minutes)
 *    ─────────────────────────────────────────────────────
 *    ENTRÉE: Réservations du jour triées par heure d'arrivée (vol)
 *    
 *    - Les clients ayant des réservations dans une même fenêtre de 30 minutes
 *      sont regroupés ensemble (même groupe = même heure départ)
 *    - Plusieurs réservations assignées au même groupe si arrivent dans l'intervalle
 *    - Le premier vol arrivant du groupe fixe le début de la fenêtre
 *    - Exemple: Vol arrive 09:00 → Fenêtre = [09:00, 09:30]
 *              Tous vols [09:00-09:30] → même groupe
 * 
 *    MÉTHODE: regrouperReservationsParFenetre30Min(List<Reservation>)
 *    SORTIE: Map<String, List<Reservation>> [clé = fenêtre]
 * 
 * 2. CALCUL DE L'HEURE DE DÉPART DU GROUPE
 *    ──────────────────────────────────────
 *    RÈGLE: heure_depart = MAX(heure_arrivee_vol) du groupe
 *    
 *    - Les 30 minutes servent à CRÉER LA FENÊTRE de regroupement
 *    - L'heure de départ = la dernière heure d'arrivée des vols du groupe
 *    - Si résultat < 08:00 → forcer 08:00 (départ minimum légal)
 *    - Tous les véhicules d'un même groupe partent ensemble à cette heure
 * 
 *    EXEMPLES CONCRETS:
 *    
 *      ┌─ JOUR: 2026-03-16
 *      │
 *      ├─ VOL 1 arrive 07:00
 *      │  👉 Crée fenêtre [07:00, 07:30] (30 minutes)
 *      │
 *      ├─ VOL 2 arrive 07:15
 *      │  ✓ 07:15 est dans fenêtre [07:00, 07:30]
 *      │  👉 Même groupe que VOL 1
 *      │
 *      └─ VOL 3 arrive 07:32
 *         ✗ 07:32 est HORS fenêtre [07:00, 07:30]
 *         👉 Crée nouvelle fenêtre [07:32, 08:02] (groupe différent)
 *    
 *      RÉSULTAT GROUPES:
 *      • GROUPE 1: [VOL 1 (07:00), VOL 2 (07:15)]
 *        FENÊTRE: [07:00, 07:30] (30 minutes pour regrouper)
 *        HEURE DÉPART = MAX(07:15) = 07:15 ✓ (dernière arrivée du groupe)
 *        → Tous les VÉHICULES de ce groupe partent à 07:15
 *      
 *      • GROUPE 2: [VOL 3 (07:32)]
 *        FENÊTRE: [07:32, 08:02] (nouvelle fenêtre, nouveau groupe)
 *        HEURE DÉPART = MAX(07:32) = 07:32 ✓ (dernière arrivée du groupe)
 *        → Tous les VÉHICULES de ce groupe partent à 07:32
 *    
 *      RÈGLE IMPORTANTE:
 *      - Les 30 minutes définissent L'INTERVALLE DE REGROUPEMENT (fenêtre)
 *      - L'heure de départ = dernière arrivée (MAX) du groupe
 *      - Tous les véhicules d'un même groupe partent à la MÊME HEURE (MAX arrivée)
 *      - Exemple: VH-001 et VH-002 dans le Groupe 1 partent ENSEMBLE à 07:15
 * 
 *    MÉTHODE: calculerHeureDepartGroupe(List<ReservationAffecteeDTO>)
 *    SORTIE: LocalDateTime (heure départ calculée)
 * 
 * 3. TRAJET ET DISTANCES
 *    ────────────────────
 *    - Chaque véhicule peut avoir des trajets différents (hôtels visités)
 *    - Distance totale = aéroport → 1er hôtel → ... → dernier hôtel → aéroport
 *    - Respect de l'ordre d'insertion (tri par distance aéroport-hôtel)
 *    - Heure retour = heure_départ + (distance_totale / vitesse_moyenne)
 * 
 *    MÉTHODES:
 *    • calculerDistanceTotaleVehicule(TrajetVehiculeDTO) → INT (km)
 *    • calculerDistanceTrajetReelle(TrajetVehiculeDTO) → INT (km)
 *    • calculerHeureRetour(TrajetVehiculeDTO) → LocalDateTime
 * 
 * 4. FORMAT DE RÉSULTAT ATTENDU (pour affichage écran)
 *    ─────────────────────────────────────────────────
 *    {
 *      heure_depart: "2026-03-16T09:45:00",
 *      vehicule: {
 *        id: 1,
 *        reference: "VH-001",
 *        nbplace: 4,
 *        typevehicule: "D",
 *        statut: "DISPONIBLE"
 *      },
 *      reservations: [
 *        {id_reservation: 1, id_client: 1, name: "Tina", email: "...", nbpassager: 3, id_hotel: 2, hotel_name: "Colbert"},
 *        {id_reservation: 2, id_client: 2, name: "Fenitra", email: "...", nbpassager: 5, id_hotel: 3, hotel_name: "Novotel"}
 *      ],
 *      hotels: [2, 3],           // Hôtels visités par ce véhicule
 *      distance_totale: 23,       // km
 *      heure_retour_aeroport: "2026-03-16T10:35:00"
 *    }
 * 
 *    CLASSE: PlanificationDTO
 *    MÉTHODE: buildPlanificationLignes(...)
 * 
 * 5. ORDRE DE TRAITEMENT (flux complet)
 *    ──────────────────────────────────
 *    
 *    ENTRÉE - Réservations à planifier (2026-03-16):
 *    ┌─────────────────────────────────────────────────────────────┐
 *    │ Réservation A: 07:00 → Hôtel A, 3 passagers                 │
 *    │ Réservation B: 07:15 → Hôtel B, 5 passagers                 │
 *    │ Réservation C: 07:32 → Hôtel C, 2 passagers                 │
 *    └─────────────────────────────────────────────────────────────┘
 *    
 *    ÉTAPE 1: Trier par heure d'arrivée (ascendant)
 *    ├─ [A (07:00), B (07:15), C (07:32)]
 *    
 *    ÉTAPE 2: Regrouper par fenêtre 30 minutes
 *    ├─ Groupe 1: [A (07:00), B (07:15)]  Fenêtre [07:00, 07:30]
 *    └─ Groupe 2: [C (07:32)]              Fenêtre [07:32, 08:02]
 *    
 *    ÉTAPE 3: Pour GROUPE 1
 *    │
 *    ├─ 3a. Calculer heure départ
 *    │     └─ MAX(07:15) = 07:15 ✓ (dernière arrivée du groupe)
 *    │
 *    ├─ 3b. Assigner véhicules disponibles
 *    │     ├─ Vérifier capacité: 3 + 5 = 8 passagers
 *    │     ├─ Véhicule VH-001: 4 places → Rés A (3 pax) ✓
 *    │     ├─ Véhicule VH-002: 5 places → Rés B (5 pax) ✓
 *    │     └─ Assignation: {VH-001, VH-002}
 *    │
 *    ├─ 3c. Calculer distance pour chaque véhicule
 *    │     ├─ VH-001 (Rés A → Hôtel A):
 *    │     │  Distance = Aéroport → Hôtel A → Aéroport = 12 + 12 = 24 km
 *    │     └─ VH-002 (Rés B → Hôtel B):
 *    │        Distance = Aéroport → Hôtel B → Aéroport = 8 + 8 = 16 km
 *    │
 *    ├─ 3d. Calculer heure retour pour chaque véhicule
 *    │     ├─ VH-001: 07:15 + (24km ÷ 50km/h × 60) + (1hôtel × 10min)
 *    │     │           = 07:15 + 29 + 10 = 07:54 ✓
 *    │     └─ VH-002: 07:15 + (16km ÷ 50km/h × 60) + (1hôtel × 10min)
 *    │                = 07:15 + 19 + 10 = 07:44 ✓
 *    │
 *    └─ 3e. Créer résultats GROUPE 1
 *         ├─ Résultat 1: {départ:07:15, VH-001, [Rés A], 24km, retour:07:54}
 *         └─ Résultat 2: {départ:07:15, VH-002, [Rés B], 16km, retour:07:44}
 *    
 *    ÉTAPE 4: Pour GROUPE 2
 *    │
 *    ├─ 4a. Calculer heure départ
 *    │     └─ MAX(07:32) = 07:32 ✓ (dernière arrivée du groupe)
 *    │
 *    ├─ 4b. Assigner véhicules
 *    │     ├─ Véhicule VH-003: 2 places → Rés C (2 pax) ✓
 *    │     └─ Assignation: {VH-003}
 *    │
 *    ├─ 4c. Calculer distance
 *    │     └─ VH-003: Aéroport → Hôtel C → Aéroport = 15 + 15 = 30 km
 *    │
 *    ├─ 4d. Calculer heure retour
 *    │     └─ VH-003: 07:32 + (30km ÷ 50km/h × 60) + (1hôtel × 10min)
 *    │                = 07:32 + 36 + 10 = 08:18 ✓
 *    │
 *    └─ 4e. Créer résultat GROUPE 2
 *         └─ Résultat 3: {départ:07:32, VH-003, [Rés C], 30km, retour:08:18}
 *    
 *    ÉTAPE 5: Retourner résultats finaux
 *    └─ [Résultat 1, Résultat 2, Résultat 3]
 * 
 *    POINTS CLÉS:
 *    ✓ Groupe 1: VH-001 ET VH-002 partent ENSEMBLE à 07:15 (même heure = MAX arrivée)
 *    ✓ Groupe 2: VH-003 part seul à 07:32 (MAX arrivée de son groupe)
 *    ✓ Chaque résultat contient TOUS les champs pour l'affichage écran
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * CLASSES/FICHIERS À IMPLÉMENTER:
 * 
 * ✓ AssignationService.java (méthodes principales)
 *   • planifier(LocalDate date, LocalDateTime heureDepart)
 *   • regrouperReservationsParFenetre30Min(List<Reservation>)
 *   • calculerHeureDepartGroupe(List<ReservationAffecteeDTO>)
 *   • trierReservationsParHeureArrivee(List<Reservation>)
 *   • calculerDistanceTotaleVehicule(TrajetVehiculeDTO)
 *   • calculerDistanceTrajetReelle(TrajetVehiculeDTO)
 *   • calculerHeureRetour(TrajetVehiculeDTO)
 *   • buildPlanificationLignes(...)
 * 
 * ✓ DTOs (restructuration/ajouts)
 *   • PlanificationDTO.java (résultat final pour écran)
 *   • TrajetVehiculeDTO.java (trajet d'un véhicule)
 *   • ReservationAffecteeDTO.java (réservation affectée)
 * 
 * ✓ Tests Unitaires
 *   • AssignationServiceTest.java
 *   • planifier_uneReservation_seulGroupe()
 *   • planifier_plusieursReservationsDans30Min_memeDepart()
 *   • planifier_reservationHorsFenetre_nouveauDepart()
 */
