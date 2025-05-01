package domainevent.command.updatereservation;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import business.qualifier.modifyreservation.ValidateFlightByModifyReservation;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;
import msa.commons.microservices.reservationairline.updatereservation.model.Action;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;

@Stateless
@ValidateFlightByModifyReservation
@Local(EventHandler.class)
public class ValidateFlightByEventModifyReservation extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand u = (UpdateReservationCommand) eventData.getData();
        List<Long> listFlightInstances = u.getFlightInstanceInfo().stream().map(IdUpdateFlightInstanceInfo::getIdFlightInstance).toList();
        Map<Long, FlightInstanceDTO> mapFlightInstance = this.flightInstanceService.getFlightInstanceIsActiveById(listFlightInstances);
        this.flightInstanceService.updateSagaIdFlightInstance(listFlightInstances, eventData.getSagaId());
        if(mapFlightInstance.isEmpty())
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
        else {
            u.getFlightInstanceInfo().forEach(info -> {
                info.setPrice(mapFlightInstance.get(info.getIdFlightInstance()).getPrice());
                info.setIdAircraft(mapFlightInstance.get(info.getIdFlightInstance()).getIdAircraft());
                if (info.getAction().equals(Action.ADD_SEATS)) 
                    info.setTotalOccupiedSeats(mapFlightInstance.get(info.getIdFlightInstance()).getPassengerCounter() + info.getNumberSeats());
                else if (info.getAction().equals(Action.REMOVE_SEATS))
                    info.setTotalOccupiedSeats(mapFlightInstance.get(info.getIdFlightInstance()).getPassengerCounter() - info.getNumberSeats());
                else if (info.getAction().equals(Action.REMOVE_FLIGHT))
                    info.setTotalOccupiedSeats(mapFlightInstance.get(info.getIdFlightInstance()).getPassengerCounter());
            });
            this.jmsEventPublisher.publish(EventId.AIRCRAFT_VALIDATE_CAPACITY_RESERVATION_AIRLINE_MODIFY_RESERVATION, eventData);
        }
    }
    
}
