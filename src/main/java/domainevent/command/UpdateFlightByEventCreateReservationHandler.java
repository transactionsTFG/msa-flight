package domainevent.command;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;

import msa.commons.event.EventData;
import msa.commons.event.EventId;

import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationQualifier;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;

@Stateless
@UpdateFlightByEventCreateReservationQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventCreateReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData();
        List<Long> listFlightInstances = c.getFlightInstanceInfo().stream().map(info -> info.getIdFlightInstance()).toList();
        List<ReservationWithSeatsDTO> addSeats = FlightInstanceMapper.idFlightInstanceInfoToReservationWithSeatsDTO(c.getFlightInstanceInfo());
        boolean isValid = this.flightInstanceService.valdateSagaId(listFlightInstances, eventData.getSagaId()) && 
                          this.flightInstanceService.addSeats(addSeats);
        if (isValid) {
            c.setAllFlightBuy(isValid);
            this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_CREATE_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, eventData);
        } else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
        
    }
    
}
