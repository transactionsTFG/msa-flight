package domainevent.command.updatereservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.modifyreservation.UpdateFlightByModifyReservationRollback;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.modifyreservation.UpdateReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.UpdateReservation;

@Stateless
@UpdateFlightByModifyReservationRollback
@Local(EventHandler.class)
public class UpdateFlightByEventModifyReservationRollback extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        if (c.isAllFlightUpdate()) {
            List<ReservationWithSeatsDTO> addSeats = FlightInstanceMapper.idFlightInstanceInfoToModReservationWithAddSeatsDTO(c.getFlightInstanceInfo());
            List<ReservationWithSeatsDTO> removeSeats = FlightInstanceMapper.idFlightInstanceInfoToModReservationWithRemoveSeatsDTO(c.getFlightInstanceInfo());
            this.flightInstanceService.addSeats(removeSeats);
            this.flightInstanceService.removeSeats(addSeats);
        }
        eventData.setOperation(UpdateReservation.UPDATE_RESERVATION_ONLY_AIRLINE_ROLLBACK);
        this.jmsEventPublisher.publish(EventId.UPDATE_RESERVATION_TRAVEL, eventData);
    }
    
}
