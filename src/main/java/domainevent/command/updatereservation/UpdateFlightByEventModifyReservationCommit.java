package domainevent.command.updatereservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.flight.modifyreservation.qualifier.UpdateFlightByModifyReservationCommit;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;

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
            this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_CREATE_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, eventData);
        } else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
