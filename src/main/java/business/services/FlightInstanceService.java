package business.services;

import java.util.List;
import java.util.Map;

import business.dto.MinAndMaxDateDTO;
import business.dto.ReservationWithSeatsDTO;
import business.flightinstance.FlightInstanceDTO;

public interface FlightInstanceService {
    Map<Long, FlightInstanceDTO> getFlightInstanceIsActiveById(List<Long> listFlightInstanceIds);
    boolean addSeats(List<ReservationWithSeatsDTO> addSeats);
    boolean removeSeats(List<ReservationWithSeatsDTO> addSeats);
    void updateSagaIdFlightInstance(List<Long> listFlightInstanceIds, String sagaId);
    boolean valdateSagaId(List<Long> flightInstances,String sagaId);
    MinAndMaxDateDTO getMinAndMaxDateFlightInstances(List<Long> idFlightInstance);
}
