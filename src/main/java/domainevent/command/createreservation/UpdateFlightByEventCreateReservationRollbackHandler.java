package domainevent.command.createreservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationRollbackQualifier;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;

@Stateless
@UpdateFlightByEventCreateReservationRollbackQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventCreateReservationRollbackHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData();
        if(c.isAllFlightBuy()) {
            List<ReservationWithSeatsDTO> removeSeats = FlightInstanceMapper.idFlightInstanceInfoToReservationWithSeatsDTO(c.getFlightInstanceInfo());
            this.flightInstanceService.removeSeats(removeSeats);
        }
    }
    
}
