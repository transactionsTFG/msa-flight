package domainevent.command.updatereservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.MinAndMaxDateDTO;
import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.modifyreservation.UpdateFlightByModifyReservationCommit;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.modifyreservation.UpdateReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@UpdateFlightByModifyReservationCommit
@Local(EventHandler.class)
public class UpdateFlightByEventModifyReservationCommit extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        List<Long> listFlightInstances = c.getFlightInstanceInfo().stream().map(info -> info.getIdFlightInstance()).toList();
        List<ReservationWithSeatsDTO> addSeats = FlightInstanceMapper.idFlightInstanceInfoToModReservationWithAddSeatsDTO(c.getFlightInstanceInfo());
        List<ReservationWithSeatsDTO> removeSeats = FlightInstanceMapper.idFlightInstanceInfoToModReservationWithRemoveSeatsDTO(c.getFlightInstanceInfo());
        boolean isValid = this.flightInstanceService.valdateSagaId(listFlightInstances, eventData.getSagaId()) && 
                          this.flightInstanceService.addSeats(addSeats) && 
                          this.flightInstanceService.removeSeats(removeSeats);
        if (isValid) {
            c.setAllFlightUpdate(isValid);
            MinAndMaxDateDTO info = this.flightInstanceService.getMinAndMaxDateFlightInstances(c.getIdFlightInstances());
            c.setMinDateTime(info.getMinDate());
            c.setMaxDateTime(info.getMaxDate());
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_COMMIT_SAGA, eventData);
        } else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
