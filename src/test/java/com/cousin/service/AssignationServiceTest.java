package com.cousin.service;

import com.cousin.dto.PlanificationDTO;
import com.cousin.dto.TrajetVehiculeDTO;
import com.cousin.model.Hotel;
import com.cousin.model.Reservation;
import com.cousin.model.Vehicule;
import com.cousin.repository.AssignationRepository;
import com.cousin.repository.ReservationRepository;
import com.cousin.repository.VehiculeRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AssignationService
 * SPRINT 5: Logique de planification par fenêtres de temps 30 minutes
 */
public class AssignationServiceTest {

    private AssignationService assignationService;
    private ReservationRepository mockReservationRepository;
    private VehiculeRepository mockVehiculeRepository;
    private AssignationRepository mockAssignationRepository;
    private DistanceService mockDistanceService;
    private ParametreService mockParametreService;

    @Before
    public void setUp() {
        // Initialiser les mocks
        mockReservationRepository = mock(ReservationRepository.class);
        mockVehiculeRepository = mock(VehiculeRepository.class);
        mockAssignationRepository = mock(AssignationRepository.class);
        mockDistanceService = mock(DistanceService.class);
        mockParametreService = mock(ParametreService.class);

        // Créer le service avec les mocks
        assignationService = new AssignationService(
                mockReservationRepository,
                mockVehiculeRepository,
                mockAssignationRepository,
                mockDistanceService,
                mockParametreService
        );

        // Configurer les comportements par défaut des mocks
        when(mockParametreService.getVitesseMoyenne()).thenReturn(50); // 50 km/h
        when(mockAssignationRepository.findAssignedReservationIds()).thenReturn(new ArrayList<>());
    }

    /**
     * TEST 1: Une seule réservation sans fenêtre groupement
     * 
     * Scenario:
     * - 1 réservation: Vol arrive 09:00 → Hôtel A, 3 passagers
     * - 1 véhicule: VH-001 avec 4 places
     * 
     * Résultat attendu:
     * - 1 groupe avec heure départ = 09:00
     * - 1 trajet assigné (VH-001)
     * - Aucune réservation non affectée
     */
    @Test
    public void planifier_uneReservation_seulGroupe() throws Exception {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);
        LocalDateTime heureDepart = LocalDateTime.of(date, LocalTime.of(8, 0));

        // Créer 1 réservation
        Hotel hotelA = new Hotel();
        hotelA.setIdHotel(1);
        hotelA.setNom("Hôtel A");

        Reservation res1 = new Reservation();
        res1.setIdReservation(1);
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));
        res1.setNbPassager(3);
        res1.setHotel(hotelA);
        res1.setIdClient("1");

        // Créer 1 véhicule
        Vehicule vh1 = new Vehicule();
        vh1.setIdVehicule(1);
        vh1.setReference("VH-001");
        vh1.setNbPlace(4);
        vh1.setTypeVehicule("D");
        vh1.setStatut("DISPONIBLE");

        // Configurer les mocks
        when(mockReservationRepository.findByDate(date)).thenReturn(List.of(res1));
        when(mockVehiculeRepository.findAll()).thenReturn(List.of(vh1));
        when(mockDistanceService.getDistance(0, 1)).thenReturn(12); // Aéroport -> Hôtel A = 12 km
        when(mockDistanceService.getDistance(1, 0)).thenReturn(12); // Hôtel A -> Aéroport = 12 km

        // ACT
        PlanificationDTO resultat = assignationService.planifier(date, heureDepart);

        // ASSERT
        assertNotNull("Le résultat ne doit pas être null", resultat);
        assertEquals("Date de planification correcte", date, resultat.getDatePlanification());
        assertEquals("Le nombre de trajets doit être 1", 1, resultat.getTrajets().size());
        assertEquals("Aucune réservation non affectée", 0, resultat.getReservationsNonAffectees().size());
        assertEquals("Total passagers = 3", 3, resultat.getTotalPassagers());

        // Vérifier le trajet
        TrajetVehiculeDTO trajet = resultat.getTrajets().get(0);
        assertEquals("Véhicule VH-001 assigné", "VH-001", trajet.getVehiculeReference());
        assertEquals("1 réservation dans le trajet", 1, trajet.getListeReservations().size());
        assertEquals("Heure départ = 09:00", LocalTime.of(9, 0), trajet.getHeureDepart().toLocalTime());
    }

    /**
     * TEST 2: Plusieurs réservations dans la même fenêtre 30 minutes = même départ
     * 
     * Scenario:
     * - Réservation 1: Vol arrive 09:00 → Hôtel A, 3 passagers
     * - Réservation 2: Vol arrive 09:15 → Hôtel B, 5 passagers
     * - Fenêtre groupement: [09:00, 09:30] → MÊME GROUPE
     * - 2 véhicules: VH-001 (4 places), VH-002 (5 places)
     * 
     * Résultat attendu:
     * - 1 groupe avec heure départ = MAX(09:15) = 09:15
     * - 2 trajets assignés (VH-001 + VH-002)
     * - Tous les 2 trajets auront heureDepart = 09:15 (même groupe)
     * - Aucune réservation non affectée
     */
    @Test
    public void planifier_plusieursReservationsDans30Min_memeDepart() throws Exception {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);
        LocalDateTime heureDepart = LocalDateTime.of(date, LocalTime.of(8, 0));

        // Créer réservations dans la fenêtre [09:00, 09:30]
        Hotel hotelA = new Hotel();
        hotelA.setIdHotel(1);
        hotelA.setNom("Hôtel A");

        Hotel hotelB = new Hotel();
        hotelB.setIdHotel(2);
        hotelB.setNom("Hôtel B");

        Reservation res1 = new Reservation();
        res1.setIdReservation(1);
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));
        res1.setNbPassager(3);
        res1.setHotel(hotelA);
        res1.setIdClient("1");

        Reservation res2 = new Reservation();
        res2.setIdReservation(2);
        res2.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 15)));
        res2.setNbPassager(5);
        res2.setHotel(hotelB);
        res2.setIdClient("2");

        // Créer véhicules
        Vehicule vh1 = new Vehicule();
        vh1.setIdVehicule(1);
        vh1.setReference("VH-001");
        vh1.setNbPlace(4);
        vh1.setTypeVehicule("D");
        vh1.setStatut("DISPONIBLE");

        Vehicule vh2 = new Vehicule();
        vh2.setIdVehicule(2);
        vh2.setReference("VH-002");
        vh2.setNbPlace(5);
        vh2.setTypeVehicule("ES");
        vh2.setStatut("DISPONIBLE");

        // Configurer les mocks
        when(mockReservationRepository.findByDate(date)).thenReturn(List.of(res1, res2));
        when(mockVehiculeRepository.findAll()).thenReturn(List.of(vh1, vh2));
        when(mockDistanceService.getDistance(0, 1)).thenReturn(12); // Aéroport -> Hôtel A
        when(mockDistanceService.getDistance(0, 2)).thenReturn(8);  // Aéroport -> Hôtel B
        when(mockDistanceService.getDistance(1, 0)).thenReturn(12); // Hôtel A -> Aéroport
        when(mockDistanceService.getDistance(2, 0)).thenReturn(8);  // Hôtel B -> Aéroport

        // ACT
        PlanificationDTO resultat = assignationService.planifier(date, heureDepart);

        // ASSERT
        assertNotNull("Le résultat ne doit pas être null", resultat);
        assertEquals("Le nombre de trajets doit être 2", 2, resultat.getTrajets().size());
        assertEquals("Aucune réservation non affectée", 0, resultat.getReservationsNonAffectees().size());
        assertEquals("Total passagers = 8", 8, resultat.getTotalPassagers());

        // Vérifier que les 2 trajets ont la même heure de départ (09:15 = MAX)
        TrajetVehiculeDTO trajet1 = resultat.getTrajets().get(0);
        TrajetVehiculeDTO trajet2 = resultat.getTrajets().get(1);

        assertEquals("Trajet 1: heure départ = 09:15", LocalTime.of(9, 15), trajet1.getHeureDepart().toLocalTime());
        assertEquals("Trajet 2: heure départ = 09:15", LocalTime.of(9, 15), trajet2.getHeureDepart().toLocalTime());
    }

    /**
     * TEST 3: Réservation hors fenêtre 30 minutes = nouveau groupe + nouveau départ
     * 
     * Scenario:
     * - Réservation 1: Vol arrive 09:00 → Hôtel A, 3 passagers
     * - Réservation 2: Vol arrive 09:15 → Hôtel B, 5 passagers (fenêtre 1: [09:00, 09:30])
     * - Réservation 3: Vol arrive 09:32 → Hôtel C, 2 passagers (fenêtre 2: [09:32, 10:02])
     * - 3 véhicules: VH-001, VH-002, VH-003
     * 
     * Résultat attendu:
     * - 2 groupes identifiés
     * - Groupe 1 (Res1+Res2): heure départ = 09:15
     * - Groupe 2 (Res3): heure départ = 09:32
     * - 3 trajets assignés (VH-001, VH-002, VH-003)
     * - Aucune réservation non affectée
     */
    @Test
    public void planifier_reservationHorsFenetre_nouveauDepart() throws Exception {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);
        LocalDateTime heureDepart = LocalDateTime.of(date, LocalTime.of(8, 0));

        // Créer hôtels
        Hotel hotelA = new Hotel();
        hotelA.setIdHotel(1);
        hotelA.setNom("Hôtel A");

        Hotel hotelB = new Hotel();
        hotelB.setIdHotel(2);
        hotelB.setNom("Hôtel B");

        Hotel hotelC = new Hotel();
        hotelC.setIdHotel(3);
        hotelC.setNom("Hôtel C");

        // Groupe 1: [09:00, 09:30]
        Reservation res1 = new Reservation();
        res1.setIdReservation(1);
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));
        res1.setNbPassager(3);
        res1.setHotel(hotelA);
        res1.setIdClient("1");

        Reservation res2 = new Reservation();
        res2.setIdReservation(2);
        res2.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 15)));
        res2.setNbPassager(5);
        res2.setHotel(hotelB);
        res2.setIdClient("2");

        // Groupe 2: [09:32, 10:02] HORS de [09:00, 09:30]
        Reservation res3 = new Reservation();
        res3.setIdReservation(3);
        res3.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 32)));
        res3.setNbPassager(2);
        res3.setHotel(hotelC);
        res3.setIdClient("3");

        // Créer véhicules
        Vehicule vh1 = new Vehicule();
        vh1.setIdVehicule(1);
        vh1.setReference("VH-001");
        vh1.setNbPlace(4);
        vh1.setTypeVehicule("D");
        vh1.setStatut("DISPONIBLE");

        Vehicule vh2 = new Vehicule();
        vh2.setIdVehicule(2);
        vh2.setReference("VH-002");
        vh2.setNbPlace(5);
        vh2.setTypeVehicule("ES");
        vh2.setStatut("DISPONIBLE");

        Vehicule vh3 = new Vehicule();
        vh3.setIdVehicule(3);
        vh3.setReference("VH-003");
        vh3.setNbPlace(2);
        vh3.setTypeVehicule("H");
        vh3.setStatut("DISPONIBLE");

        // Configurer les mocks
        when(mockReservationRepository.findByDate(date)).thenReturn(List.of(res1, res2, res3));
        when(mockVehiculeRepository.findAll()).thenReturn(List.of(vh1, vh2, vh3));
        when(mockDistanceService.getDistance(0, 1)).thenReturn(12); // Aéroport -> Hôtel A
        when(mockDistanceService.getDistance(0, 2)).thenReturn(8);  // Aéroport -> Hôtel B
        when(mockDistanceService.getDistance(0, 3)).thenReturn(15); // Aéroport -> Hôtel C
        when(mockDistanceService.getDistance(1, 0)).thenReturn(12); // Hôtel A -> Aéroport
        when(mockDistanceService.getDistance(2, 0)).thenReturn(8);  // Hôtel B -> Aéroport
        when(mockDistanceService.getDistance(3, 0)).thenReturn(15); // Hôtel C -> Aéroport

        // ACT
        PlanificationDTO resultat = assignationService.planifier(date, heureDepart);

        // ASSERT
        assertNotNull("Le résultat ne doit pas être null", resultat);
        assertEquals("Le nombre de trajets doit être 3", 3, resultat.getTrajets().size());
        assertEquals("Aucune réservation non affectée", 0, resultat.getReservationsNonAffectees().size());
        assertEquals("Total passagers = 10", 10, resultat.getTotalPassagers());

        // Vérifier les groupes et heures de départ
        TrajetVehiculeDTO trajet1 = resultat.getTrajets().get(0);
        TrajetVehiculeDTO trajet2 = resultat.getTrajets().get(1);
        TrajetVehiculeDTO trajet3 = resultat.getTrajets().get(2);

        // Groupe 1: Res1 et Res2 devraient avoir heure départ = 09:15 (MAX de [09:00, 09:15])
        assertEquals("Trajet 1: heure départ = 09:15", LocalTime.of(9, 15), trajet1.getHeureDepart().toLocalTime());
        assertEquals("Trajet 2: heure départ = 09:15", LocalTime.of(9, 15), trajet2.getHeureDepart().toLocalTime());

        // Groupe 2: Res3 a heure départ = 09:32 (MAX de [09:32])
        assertEquals("Trajet 3: heure départ = 09:32", LocalTime.of(9, 32), trajet3.getHeureDepart().toLocalTime());

        // Vérifier les hôtels visités (doivent être différents pour chaque trajet)
        assertTrue("Trajet 1 a au moins 1 réservation", trajet1.getListeReservations().size() > 0);
        assertTrue("Trajet 2 a au moins 1 réservation", trajet2.getListeReservations().size() > 0);
        assertTrue("Trajet 3 a au moins 1 réservation", trajet3.getListeReservations().size() > 0);
    }

    /**
     * TEST AUXILIAIRE: Vérifier la fonction de tri par heure d'arrivée
     */
    @Test
    public void trierReservationsParHeureArrivee_ordreAscendant() {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);

        Hotel hotel = new Hotel();
        hotel.setIdHotel(1);
        hotel.setNom("Test Hotel");

        Reservation res1 = new Reservation();
        res1.setIdReservation(1);
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(10, 0)));

        Reservation res2 = new Reservation();
        res2.setIdReservation(2);
        res2.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(8, 0)));

        Reservation res3 = new Reservation();
        res3.setIdReservation(3);
        res3.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));

        List<Reservation> reservations = List.of(res1, res2, res3);

        // ACT
        List<Reservation> triees = assignationService.trierReservationsParHeureArrivee(reservations);

        // ASSERT
        assertEquals("Ordre ascendant: res2 (08:00) en premier", 2, triees.get(0).getIdReservation());
        assertEquals("Ordre ascendant: res3 (09:00) au milieu", 3, triees.get(1).getIdReservation());
        assertEquals("Ordre ascendant: res1 (10:00) en dernier", 1, triees.get(2).getIdReservation());
    }

    /**
     * TEST AUXILIAIRE: Vérifier la fonction de regroupement par fenêtre 30 minutes
     */
    @Test
    public void regrouperReservationsParFenetre30Min_groupesCorrects() {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);

        Hotel hotel = new Hotel();
        hotel.setIdHotel(1);

        // Groupe 1: [09:00, 09:30]
        Reservation res1 = new Reservation();
        res1.setIdReservation(1);
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));

        Reservation res2 = new Reservation();
        res2.setIdReservation(2);
        res2.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 15)));

        // Groupe 2: [09:32, 10:02]
        Reservation res3 = new Reservation();
        res3.setIdReservation(3);
        res3.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 32)));

        List<Reservation> reservationsSorted = List.of(res1, res2, res3);

        // ACT
        List<List<Reservation>> groupes = assignationService.regrouperReservationsParFenetre30Min(reservationsSorted);

        // ASSERT
        assertEquals("2 groupes identifiés", 2, groupes.size());
        assertEquals("Groupe 1: 2 réservations", 2, groupes.get(0).size());
        assertEquals("Groupe 2: 1 réservation", 1, groupes.get(1).size());
    }

    /**
     * TEST AUXILIAIRE: Vérifier le calcul de l'heure de départ d'un groupe
     */
    @Test
    public void calculerHeureDepartGroupe_maxArrivee() {
        // ARRANGE
        LocalDate date = LocalDate.of(2026, 3, 16);

        Hotel hotel = new Hotel();
        hotel.setIdHotel(1);

        Reservation res1 = new Reservation();
        res1.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 0)));

        Reservation res2 = new Reservation();
        res2.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 15)));

        Reservation res3 = new Reservation();
        res3.setDateHeureArrive(LocalDateTime.of(date, LocalTime.of(9, 8)));

        List<Reservation> groupe = List.of(res1, res2, res3);

        // ACT
        LocalDateTime heureDepart = assignationService.calculerHeureDepartGroupe(groupe);

        // ASSERT
        assertEquals("Heure départ = MAX(09:15)", LocalTime.of(9, 15), heureDepart.toLocalTime());
    }
}
