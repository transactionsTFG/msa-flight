package business.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.dto.ReservationWithSeatsDTO;
import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import msa.commons.commands.createreservation.model.IdFlightInstanceInfo;
import msa.commons.commands.modifyreservation.model.Action;
import msa.commons.commands.modifyreservation.model.IdUpdateFlightInstanceInfo;
import msa.commons.commands.removereservation.IdWithSeats;


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
        return flightInstances.stream()
            .filter(flightInstance -> flightInstance.getAction().equals(Action.ADD_SEATS))
            .map(flightInstance -> {
                ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
                reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
                reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
                return reservationWithSeatsDTO;
            })
        .toList();
    }

    static List<ReservationWithSeatsDTO> idFlightInstanceInfoToModReservationWithRemoveSeatsDTO(List<IdUpdateFlightInstanceInfo> flightInstances) {
        return flightInstances.stream()
            .filter(flightInstance -> !flightInstance.getAction().equals(Action.ADD_SEATS))    
            .map(flightInstance -> {
                ReservationWithSeatsDTO reservationWithSeatsDTO = new ReservationWithSeatsDTO();
                reservationWithSeatsDTO.setIdFlightInstance(flightInstance.getIdFlightInstance());
                reservationWithSeatsDTO.setNewPassengerCounter(flightInstance.getNumberSeats());
                return reservationWithSeatsDTO;
            })
            .toList();
    }
}
