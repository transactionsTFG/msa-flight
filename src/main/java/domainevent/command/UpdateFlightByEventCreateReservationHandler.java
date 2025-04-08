package domainevent.command;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventId;

import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationQualifier;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;

@Stateless
@UpdateFlightByEventCreateReservationQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventCreateReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        CreateReservationCommand c = this.gson.fromJson(json, CreateReservationCommand.class);
        List<ReservationWithSeatsDTO> addSeats = c.getFlightInstanceInfo().stream().map(info -> {
            ReservationWithSeatsDTO r = new ReservationWithSeatsDTO();
            r.setIdFlightInstance(info.getIdFlightInstance());
            r.setNewPassengerCounter(info.getNumberSeats());
            return r;
        }).toList();
        boolean isValid = this.flightInstanceService.addSeats(addSeats);
        if (isValid) 
            this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_CREATE_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, c);
        else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, c);
        
    }
    
}
