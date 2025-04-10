package business.services;

import java.util.List;
import java.util.Map;

import business.dto.ReservationWithSeatsDTO;
import business.flightinstance.FlightInstanceDTO;

public interface FlightInstanceService {
    Map<Long, FlightInstanceDTO> getFlightInstanceIsActiveById(List<Long> listFlightInstanceIds);
    boolean addSeats(List<ReservationWithSeatsDTO> addSeats);
}
