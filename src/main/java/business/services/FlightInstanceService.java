package business.services;

import business.flightinstance.FlightInstanceDTO;

public interface FlightInstanceService {
    FlightInstanceDTO getFlightInstanceIsActiveById(long idFlightInstance);
}
