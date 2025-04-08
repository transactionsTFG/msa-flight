package domainevent.command;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventId;
import msa.commons.microservices.flight.qualifier.ValidateFlightByEventCreateReservationQualifier;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.commandevent.model.IdFlightInstanceInfo;

@Stateless
@ValidateFlightByEventCreateReservationQualifier
@Local(EventHandler.class)
public class ValidateFlightByEventCreateReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(Object data) {
        CreateReservationCommand c = this.gson.fromJson(data.toString(), CreateReservationCommand.class);
        List<Long> listFlightInstances = c.getFlightInstanceInfo().stream().map(IdFlightInstanceInfo::getIdFlightInstance)
                                                                            .toList();
        Map<Long, FlightInstanceDTO> mapFlightInstance = this.flightInstanceService.getFlightInstanceIsActiveById(listFlightInstances);
        if(mapFlightInstance.isEmpty())
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, c);
        else {
            c.getFlightInstanceInfo().forEach(info -> {
                info.setPrice(mapFlightInstance.get(info.getIdFlightInstance()).getPrice());
                info.setIdAircraft(mapFlightInstance.get(info.getIdFlightInstance()).getIdAircraft());
                info.setTotalOccupiedSeats(mapFlightInstance.get(info.getIdFlightInstance() + info.getNumberSeats()).getPassengerCounter());
            });
            this.jmsEventPublisher.publish(EventId.AIRCRAFT_VALIDATE_CAPACITY_RESERVATION_AIRLINE_CREATE_RESERVATION, c);
        }
    }
    
}
