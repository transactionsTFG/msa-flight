package domainevent.command.updatereservation;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.flight.modifyreservation.qualifier.UpdateFlightByModifyReservationCommit;
import msa.commons.microservices.flight.modifyreservation.qualifier.UpdateFlightByModifyReservationRollback;
import msa.commons.microservices.flight.modifyreservation.qualifier.ValidateFlightByModifyReservation;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;
import msa.commons.microservices.reservationairline.updatereservation.model.Action;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;

@Stateless
@UpdateFlightByModifyReservationRollback
@Local(EventHandler.class)
public class UpdateFlightByEventModifyReservationRollback extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommand'");
    }
    
}
