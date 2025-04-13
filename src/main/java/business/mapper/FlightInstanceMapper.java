package business.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.dto.ReservationWithSeatsDTO;
import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import msa.commons.microservices.reservationairline.commandevent.model.IdFlightInstanceInfo;

@Mapper
public interface FlightInstanceMapper {
    FlightInstanceMapper INSTANCE = Mappers.getMapper(FlightInstanceMapper.class);
    @Mapping(target = "idFlight", source = "flight.id")
    @Mapping(target = "idAircraft", source = "flight.idAircraft")
    FlightInstanceDTO entityToDto(FlightInstance flight);

    static List<ReservationWithSeatsDTO> idFlightInstanceInfoToReservationWithSeatsDTO(List<IdFlightInstanceInfo> flightInstances) {
        return flightInstances.stream().map(flightInstance -> {
            ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
            reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
            reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
            return reservationWithSeatsDTO;
        })
        .toList();
    }
}
