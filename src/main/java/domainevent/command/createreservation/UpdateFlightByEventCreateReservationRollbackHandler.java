package domainevent.command.createreservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.createreservation.UpdateFlightByEventCreateReservationRollbackQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.createreservation.CreateReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.ReservationAirline;

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
        eventData.setOperation(ReservationAirline.CREATE_RESERVATION_ONLY_AIRLINE_ROLLBACK);
        this.jmsEventPublisher.publish(EventId.CREATE_RESERVATION_TRAVEL, eventData);
    }
    
}
