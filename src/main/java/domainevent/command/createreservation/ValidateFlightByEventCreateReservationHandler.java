package domainevent.command.createreservation;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import business.qualifier.createreservation.ValidateFlightByEventCreateReservationQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.createreservation.CreateReservationCommand;
import msa.commons.commands.createreservation.model.IdFlightInstanceInfo;
import msa.commons.event.EventData;
import msa.commons.event.EventId;


@Stateless
@ValidateFlightByEventCreateReservationQualifier
@Local(EventHandler.class)
public class ValidateFlightByEventCreateReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData();
        List<Long> listFlightInstances = c.getFlightInstanceInfo().stream().map(IdFlightInstanceInfo::getIdFlightInstance)
                                                                            .toList();
        Map<Long, FlightInstanceDTO> mapFlightInstance = this.flightInstanceService.getFlightInstanceIsActiveById(listFlightInstances);
        this.flightInstanceService.updateSagaIdFlightInstance(listFlightInstances, eventData.getSagaId());
        if(mapFlightInstance.isEmpty())
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
        else {
            c.getFlightInstanceInfo().forEach(info -> {
                info.setPrice(mapFlightInstance.get(info.getIdFlightInstance()).getPrice());
                info.setIdAircraft(mapFlightInstance.get(info.getIdFlightInstance()).getIdAircraft());
                info.setTotalOccupiedSeats(mapFlightInstance.get(info.getIdFlightInstance()).getPassengerCounter() + info.getNumberSeats());
            });
            this.jmsEventPublisher.publish(EventId.AIRCRAFT_VALIDATE_CAPACITY_RESERVATION_AIRLINE_CREATE_RESERVATION, eventData);
        }
    }
    
}
