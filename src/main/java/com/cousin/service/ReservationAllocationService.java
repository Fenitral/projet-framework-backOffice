package com.cousin.service;

import com.cousin.dto.AllocationReservationDTO;
import com.cousin.dto.PassagerAssignationDTO;
import com.cousin.model.Reservation;
import com.cousin.model.Vehicule;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
public class ReservationAllocationService {

    public List<AllocationReservationDTO> prepareReservationsForAllocation(LocalDate date, List<Reservation> reservations) {
        reservations.sort((r1, r2) -> {
            int cmpPassagers = Integer.compare(r2.getNbPassager(), r1.getNbPassager());
            if (cmpPassagers != 0) {
                return cmpPassagers;
            }
            return resolveClientSortKey(r1).compareToIgnoreCase(resolveClientSortKey(r2));
        });

        List<AllocationReservationDTO> dtos = new ArrayList<>();
        int priorite = 1;
        for (Reservation r : reservations) {
            AllocationReservationDTO dto = new AllocationReservationDTO();
            dto.setReservationId((long) r.getIdReservation());
            dto.setNombrePassagersTotal(r.getNbPassager());
            dto.setNombrePassagersAssignes(0);
            dto.setListeAffectations(new ArrayList<>());
            dto.setNombrePassagersNonAssignes(r.getNbPassager());
            dto.setPrioriteClient(priorite++);
            dtos.add(dto);
        }
        return dtos;
    }

    public void allocatePassengersToVehicles(List<AllocationReservationDTO> reservations, List<Vehicule> vehicules, List<Reservation> reservationEntities) {
        // Création d'une map id -> Reservation pour accès rapide
        java.util.Map<Long, Reservation> reservationMap = new java.util.HashMap<>();
        for (Reservation r : reservationEntities) {
            reservationMap.put((long) r.getIdReservation(), r);
        }
        for (AllocationReservationDTO reservation : reservations) {
            int passagersRestants = reservation.getNombrePassagersTotal();
            // Récupérer la vraie date/heure de la réservation
            java.time.LocalDateTime heureReservation = null;
            Reservation entity = reservationMap.get(reservation.getReservationId());
            if (entity != null) {
                heureReservation = entity.getDateHeureArrive();
            }
            while (passagersRestants > 0) {
                Vehicule vehicule = findBestVehicule(passagersRestants, vehicules, heureReservation);
                if (vehicule == null) break;
                int placesDispo = vehicule.getPlacesDisponibles();
                int aAffecter = Math.min(passagersRestants, placesDispo);

                PassagerAssignationDTO affectation = new PassagerAssignationDTO();
                affectation.setReservationId(reservation.getReservationId());
                affectation.setNombrePassagers(aAffecter);
                affectation.setPositionVisite(reservation.getListeAffectations().size() + 1);
                affectation.setLieuVisite("HOTEL");
                reservation.getListeAffectations().add(affectation);

                vehicule.setPlacesDisponibles(placesDispo - aAffecter);
                vehicule.setNbTrajets(vehicule.getNbTrajets() + 1);
                passagersRestants -= aAffecter;
            }

            reservation.setNombrePassagersAssignes(reservation.getNombrePassagersTotal() - passagersRestants);
            reservation.setNombrePassagersNonAssignes(passagersRestants);
        }
    }

        private Vehicule findBestVehicule(int passagersRestants, List<Vehicule> vehicules, java.time.LocalDateTime heureReservation) {
        List<Vehicule> candidats = vehicules.stream()
            .filter(v -> v.getPlacesDisponibles() > 0)
            .filter(v -> {
                if (heureReservation == null) {
                    System.out.println("[DEBUG] Vehicule: " + v.getReference() + ", heure_disponibilite=" + v.getHeureDisponibilite() + ", heureReservation=null => ACCEPTE (pas d'heure)");
                    return true;
                }
                java.time.LocalTime heureDispo = java.time.LocalTime.parse(v.getHeureDisponibilite());
                java.time.LocalTime heureRes = heureReservation.toLocalTime();
                boolean ok = !heureRes.isBefore(heureDispo);
                System.out.println("[DEBUG] Vehicule: " + v.getReference() + ", heure_disponibilite=" + heureDispo + ", heureReservation=" + heureRes + (ok ? " => ACCEPTE" : " => REFUSE"));
                return ok;
            })
            .collect(Collectors.toList());
                // Si aucun véhicule n'est disponible, on retourne null (aucune assignation possible)
                if (candidats == null || candidats.isEmpty()) {
                System.out.println("[DEBUG] Aucun véhicule disponible pour heureReservation=" + (heureReservation != null ? heureReservation.toLocalTime() : "null"));
                return null;
                }

                Random random = new Random();
                Collections.shuffle(candidats, random);

                return candidats.stream()
                    .sorted(Comparator
                        .comparingInt((Vehicule v) -> Math.abs(v.getPlacesDisponibles() - passagersRestants))
                        .thenComparingInt(Vehicule::getNbTrajets)
                        .thenComparingInt(v -> isDiesel(v.getTypeVehicule()) ? 0 : 1)
                    )
                    .findFirst()
                    .orElse(null);
        }

    private boolean isDiesel(String typeVehicule) {
        if (typeVehicule == null) {
            return false;
        }
        String type = typeVehicule.trim().toUpperCase();
        return "D".equals(type) || "DIESEL".equals(type);
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
}
