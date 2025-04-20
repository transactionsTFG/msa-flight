package business.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.dto.ReservationWithSeatsDTO;
import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import msa.commons.microservices.reservationairline.commandevent.model.IdFlightInstanceInfo;
import msa.commons.microservices.reservationairline.removereservation.command.IdWithSeats;
import msa.commons.microservices.reservationairline.updatereservation.model.Action;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;

@Mapper
public interface FlightInstanceMapper {
    FlightInstanceMapper INSTANCE = Mappers.getMapper(FlightInstanceMapper.class);
    @Mapping(target = "idFlight", source = "flight.id")
    @Mapping(target = "idAircraft", source = "flight.idAircraft")
    FlightInstanceDTO entityToDto(FlightInstance flight);

    static List<ReservationWithSeatsDTO> idWithSeatsToReservationWithSeatsDTO(List<IdWithSeats> flightInstances) {
        return flightInstances.stream().map(flightInstance -> {
            ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
            reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
            reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getSeats());
            return reservationWithSeatsDTO;
        })
        .toList();
    }

    static List<ReservationWithSeatsDTO> idFlightInstanceInfoToReservationWithSeatsDTO(List<IdFlightInstanceInfo> flightInstances) {
        return flightInstances.stream().map(flightInstance -> {
            ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
            reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
            reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
            return reservationWithSeatsDTO;
        })
        .toList();
    }

    static List<ReservationWithSeatsDTO> idFlightInstanceInfoToModReservationWithAddSeatsDTO(List<IdUpdateFlightInstanceInfo> flightInstances) {
        return flightInstances.stream().map(flightInstance -> {
            if (!flightInstance.getAction().equals(Action.ADD_SEATS))
                return null;
            ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
            reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
            reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
            return reservationWithSeatsDTO;
        })
        .toList();
    }

    static List<ReservationWithSeatsDTO> idFlightInstanceInfoToModReservationWithRemoveSeatsDTO(List<IdUpdateFlightInstanceInfo> flightInstances) {
        return flightInstances.stream().map(flightInstance -> {
            if (flightInstance.getAction().equals(Action.ADD_SEATS))
                return null;
            ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
            reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
            reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
            return reservationWithSeatsDTO;
        })
        .toList();
    }
}
