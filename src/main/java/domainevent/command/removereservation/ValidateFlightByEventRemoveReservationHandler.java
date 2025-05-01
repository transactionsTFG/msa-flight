package domainevent.command.removereservation;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import business.qualifier.removereservation.ValidateFlightByRemoveReservationQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.removereservation.IdWithSeats;
import msa.commons.commands.removereservation.RemoveReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@ValidateFlightByRemoveReservationQualifier
@Local(EventHandler.class)
@Stateless
public class ValidateFlightByEventRemoveReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();
        final List<Long> listFlightInstances = c.getListIdFlightInstance().stream().map(IdWithSeats::getIdFlightInstance).toList();
        Map<Long, FlightInstanceDTO> mapFlightInstance = this.flightInstanceService.getFlightInstanceIsActiveById(listFlightInstances);
        this.flightInstanceService.updateSagaIdFlightInstance(listFlightInstances, eventData.getSagaId());
        if(mapFlightInstance.isEmpty())
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, eventData);
        else 
            this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_REMOVE_RESERVATION_COMMIT_SAGA, eventData);
        
    }
    
}
