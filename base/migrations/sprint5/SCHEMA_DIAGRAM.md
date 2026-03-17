```
╔═══════════════════════════════════════════════════════════════════════════╗
║                   SCHÉMA SPRINT 5 - LOCAL                                 ║
║          Planification complète des trajets véhicules (logique métier)    ║
╚═══════════════════════════════════════════════════════════════════════════╝

DONNÉES FONDAMENTALES (entrées)
───────────────────────────────

┌─────────────────┐       ┌──────────────┐       ┌──────────────┐
│ local.hotel     │       │ local.client │       │ local.unite  │
├─────────────────┤       ├──────────────┤       ├──────────────┤
│ id_hotel (PK)   │       │ client_id(PK)│       │ unite_id(PK) │
│ nom             │       │ name         │       │ nom_unite    │
│ aeroport        │       │ email (UQ)   │       └──────────────┘
│ is_aeroport     │       │ phone        │
└─────────────────┘       └──────────────┘
       △                          △
       │                          │
       │ id_hotel             client_id
       │                          │
    ┌──────────────────┐    ┌─────────────────────┐
    │local.reservation │    │ local.parametre     │
    ├──────────────────┤    ├──────────────────────┤
    │id_reservation(PK)│    │ parametre_id (PK)    │
    │dateheurearrive   │    │ nom_param            │
    │idclient          │    │ valeur               │
    │nbpassager        │    │ unite_id (FK)        │
    │id_hotel (FK)     │    └──────────────────────┘
    │client_id (FK)    │
    └──────────────────┘

VÉHICULES & REGROUPEMENT
─────────────────────────

┌──────────────────┐      ┌────────────────────┐
│ local.vehicule   │      │local.regroupement  │
├──────────────────┤      ├────────────────────┤
│ id_vehicule (PK) │      │regroupement_id(PK) │
│ reference        │      │ name               │
│ nbplace          │      │ description        │
│ typevehicule     │      └────────────────────┘
│ lieu_actuel      │
│ statut           │
└──────────────────┘

DISTANCES ENTRE HÔTELS
──────────────────────

┌──────────────────┐
│ local.distance   │
├──────────────────┤
│ distance_id (PK) │
│ idhotelfrom(FK)  │  (NULL = AÉROPORT)
│ idhotelto (FK)   │  (NULL = AÉROPORT)
│ valeur (km)      │
└──────────────────┘


PLANIFICATION (OUTPUT)
──────────────────────────────────────────────────────────────

┌────────────────────────────┐
│local.temps_attente_window  │
├────────────────────────────┤
│ window_id (PK)             │
│ departure_date             │
│ window_start (TIME)        │  ex: 09:00:00 - 09:30:00
│ window_end (TIME)          │      30 minutes d'attente
│ minutes_attente = 30       │
└────────┬────────────────────┘
         │
         │ window_id
         ▼
┌────────────────────────────┐
│ local.planification        │
├────────────────────────────┤
│ planification_id (PK)      │
│ departure_date             │
│ window_id (FK)             │
│ heure_depart (TIMESTAMP)   │  ← Max arrivée vol + 30 min
│ heure_retour_aeroport      │  ← Après parcours complet
│ description                │
│ created_at                 │
└────────┬────────────────────┘
         │
         │ planification_id
         ▼
┌────────────────────────────────────────┐
│ local.trajet_vehicule                  │
├────────────────────────────────────────┤
│ trajet_id (PK)                         │
│ planification_id (FK)                  │
│ vehicule_id (FK) ──────┐               │
│ distance_totale (km)   │               │
│ heure_depart_aeroport  │   RÉSULTAT    │
│ heure_retour_aeroport  │   ATTENDU:    │
│ ordre_trajet           │   ───────────►│
│ created_at             │               │
└────┬────────────────────────────────────┘ {
     │ trajet_id                          |  "heure_depart": "2026-03-16 09:30",
     ▼                                      |  "vehicule": {VH-001, ...},
┌──────────────────────────────────────┐   |  "reservations": [{...}, {...}],
│local.assignation_detaillee           │   |  "hotels": [2, 3, ...],
├──────────────────────────────────────┤   |  "distance_totale": 42,
│assignation_detaillee_id (PK)         │   |  "heure_retour": "2026-03-16 11:45"
│ trajet_id (FK)                       │   | }
│ reservation_id (FK) ──┐              │
│ ordre_visite          │  DÉTAIL      │
│ hotel_visite (FK)     │  TRAJET      │
└──────────────────────────────────────┘
        ▲
        │
        │ reservation_id
        │
┌──────────────────┐
│local.reservation │
│(déjà existe)     │
└──────────────────┘


FLUX LOGIQUE DE PLANIFICATION
──────────────────────────────

  1. ENTRÉE: Réservations pour 2026-03-16
     ├─ Réservation 1: 09:00, 3 passagers, Hôtel Colbert (CLI-001)
     ├─ Réservation 2: 09:15, 5 passagers, Hôtel Novotel (CLI-002)
     └─ Réservation 3: 10:00, 2 passagers, Hôtel Ibis (CLI-003)

  2. FENÊTRE DE TEMPS D'ATTENTE:
     └─ Rés 1 & 2 → même window (09:00-09:30)
     • Heure départ = max(09:15) + 30 min = 09:45
     • Si < 08:00, forcer 08:00
     └─ Rés 3 → window suivante (10:00-10:30)
     • Heure départ = 10:00 + 30 min = 10:30

  3. ASSIGNATION VÉHICULE (priorité):
     ├─ Capacité >= nbpassager ?
     ├─ Si plusieurs: prefer DIESEL
     ├─ Sinon: random
     └─ Ordre visite = ordre hôtels par distance aéroport ↑

  4. CALCUL TRAJET:
     ├─ Départ aéroport: 09:45
     ├─ Aéroport → Colbert: 12 km
     ├─ Colbert → Novotel: 3 km
     ├─ Novotel → Aéroport: 8 km
     ├─ Total: 23 km
     ├─ Temps = 23 km ÷ 50 km/h + attentes = +10 min/hôtel
     └─ Retour aéroport: 09:45 + 50 min ≈ 10:35

  5. FORMAT RÉSULTAT (attendu par frontend):
     {
       "heure_depart": "2026-03-16 09:45:00",
       "vehicule": {
         "id": 1, "reference": "VH-001", "nbplace": 4, "typevehicule": "D"
       },
       "reservations": [
         {"id": 1, "client": "CLI-001", "nbpassager": 3, "hotel": "Colbert"},
         {"id": 2, "client": "CLI-002", "nbpassager": 5, "hotel": "Novotel"}
       ],
       "hotels": [2, 3],
       "distance_totale": 23,
       "heure_retour_aeroport": "2026-03-16 10:35:00"
     }
```

## Tableaux clés:

| Table | Rôle |
|-------|------|
| **temps_attente_window** | Fenêtres de 30 min pour regrouper les clients |
| **planification** | Groupe final avec heure départ/retour |
| **trajet_vehicule** | Véhicule assigné + distances/horaires du trajet |
| **assignation_detaillee** | Ordre de visite hôtels par réservation |

## Règles métier intégrées:

- ✅ Regroupement par fenêtre 30 minutes
- ✅ Heure départ = dernière arrivée + 30 min (min. 08:00)
- ✅ Tous véhicules du groupe partent ensemble
- ✅ Chaque véhicule trajet différent (hôtels visités)
- ✅ Heure retour = départ + (distance/vitesse) + temps attentes
