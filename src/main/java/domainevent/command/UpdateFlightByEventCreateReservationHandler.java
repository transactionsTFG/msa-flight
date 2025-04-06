package domainevent.command;

import java.beans.EventHandler;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationWithSeatsDTO;
import domainevent.command.handler.BaseHandler;
import msa.commons.event.EventId;
import msa.commons.microservices.customerairline.commandevent.ValidateFlightCommand;
import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationQualifier;

@Stateless
@UpdateFlightByEventCreateReservationQualifier
@Local(EventHandler.class)
public class UpdateFlightByEventCreateReservationHandler extends BaseHandler {

    @Override
    public void handleCommand(Object data) {
        ValidateFlightCommand v = (ValidateFlightCommand) data;
        List<ReservationWithSeatsDTO> addSeats = v.getFlightInstanceInfo().stream().map(info -> {
            ReservationWithSeatsDTO r = new ReservationWithSeatsDTO();
            r.setIdFlightInstance(info.getIdFlightInstance());
            r.setNewPassengerCounter(info.getNumberSeats());
            return r;
        }).toList();
        boolean isValid = this.flightInstanceService.addSeats(addSeats);
        if (isValid) 
            this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_CREATE_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, v);
        else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, v);
        
    }
    
}
