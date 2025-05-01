package domainevent.command.removereservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.removereservation.UpdateFlightByRemoveReservationRollbackQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.reservationairline.removereservation.command.RemoveReservationCommand;

@Stateless
@UpdateFlightByRemoveReservationRollbackQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventRemoveReservationRollbackHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();
        if(c.isAllRemove()) {
            List<ReservationWithSeatsDTO> addSeats = FlightInstanceMapper.idWithSeatsToReservationWithSeatsDTO(c.getListIdFlightInstance());
            this.flightInstanceService.addSeats(addSeats);
        }
    }
    
}
