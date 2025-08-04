package business.services;

import java.util.List;
import java.util.Map;

import business.dto.FlightParamsDTO;
import business.dto.MinAndMaxDateDTO;
import business.dto.ReservationWithSeatsDTO;
import business.flight.FlightDTO;
import business.flightinstance.FlightInstanceDTO;

public interface FlightInstanceService {
    FlightDTO getFlightById(long idFlight);
    List<FlightDTO> getFlightsByParams(FlightParamsDTO params);
    FlightInstanceDTO getFlightInstanceById(long instanceId);
    Map<Long, FlightInstanceDTO> getFlightInstanceIsActiveById(List<Long> listFlightInstanceIds);
    boolean addSeats(List<ReservationWithSeatsDTO> addSeats);
    boolean removeSeats(List<ReservationWithSeatsDTO> addSeats);
    void updateSagaIdFlightInstance(List<Long> listFlightInstanceIds, String sagaId);
    boolean valdateSagaId(List<Long> flightInstances,String sagaId);
    MinAndMaxDateDTO getMinAndMaxDateFlightInstances(List<Long> idFlightInstance);
}
