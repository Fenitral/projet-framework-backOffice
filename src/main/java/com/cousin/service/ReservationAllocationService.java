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
        reservations.sort(Comparator.comparingInt(Reservation::getNbPassager).reversed());
        // Mapper vers AllocationReservationDTO...
        List<AllocationReservationDTO> dtos = new ArrayList<>();
        for (Reservation r : reservations) {
            AllocationReservationDTO dto = new AllocationReservationDTO();
            dto.setReservationId((long) r.getIdReservation());
            dto.setNombrePassagersTotal(r.getNbPassager());
            dto.setNombrePassagersAssignes(0);
            dto.setListeAffectations(new ArrayList<>());
            dto.setNombrePassagersNonAssignes(r.getNbPassager());
            // prioriteClient à adapter selon votre logique
            dtos.add(dto);
        }
        return dtos;
    }

    public void allocatePassengersToVehicles(List<AllocationReservationDTO> reservations, List<Vehicule> vehicules) {
        for (AllocationReservationDTO reservation : reservations) {
            int passagersRestants = reservation.getNombrePassagersTotal();
            while (passagersRestants > 0) {
                Vehicule vehicule = findBestVehicule(passagersRestants, vehicules);
                if (vehicule == null) break;
                int placesDispo = vehicule.getPlacesDisponibles();
                int aAffecter = Math.min(passagersRestants, placesDispo);

                PassagerAssignationDTO affectation = new PassagerAssignationDTO();
                affectation.setReservationId(reservation.getReservationId());
                affectation.setNombrePassagers(aAffecter);
                // positionVisite, lieuVisite à compléter selon votre logique
                reservation.getListeAffectations().add(affectation);
                vehicule.setPlacesDisponibles(placesDispo - aAffecter);
                passagersRestants -= aAffecter;
            }
            reservation.setNombrePassagersAssignes(reservation.getNombrePassagersTotal() - passagersRestants);
            reservation.setNombrePassagersNonAssignes(passagersRestants);
            // statut à gérer côté entité Reservation
        }
    }

    private Vehicule findBestVehicule(int passagersRestants, List<Vehicule> vehicules) {
        List<Vehicule> candidats = vehicules.stream()
                .filter(v -> v.getPlacesDisponibles() > 0)
                .collect(Collectors.toList());
        if (candidats.isEmpty()) return null;
        Collections.shuffle(candidats); // Pour gérer l'aléatoire en dernier recours
        return candidats.stream()
                .sorted(Comparator
                .comparingInt((Vehicule v) -> Math.abs(v.getPlacesDisponibles() - passagersRestants))
                .thenComparing(Vehicule::getNbTrajets))
                .findFirst()
                .orElse(null);
    }
}
