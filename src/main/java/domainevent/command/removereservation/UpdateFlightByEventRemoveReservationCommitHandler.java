package domainevent.command.removereservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.removereservation.UpdateFlightByRemoveReservationCommitQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.removereservation.IdWithSeats;
import msa.commons.commands.removereservation.RemoveReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@UpdateFlightByRemoveReservationCommitQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventRemoveReservationCommitHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();
        List<Long> listFlightInstances = c.getListIdFlightInstance().stream().map(IdWithSeats::getIdFlightInstance).toList();
        List<ReservationWithSeatsDTO> removeSeats = FlightInstanceMapper.idWithSeatsToReservationWithSeatsDTO(c.getListIdFlightInstance());
        boolean isValid = this.flightInstanceService.valdateSagaId(listFlightInstances, eventData.getSagaId()) && 
                          this.flightInstanceService.removeSeats(removeSeats);
        if (isValid) {
            c.setAllRemove(isValid);
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_COMMIT_SAGA, eventData);
        } else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
        
    }
    
}
