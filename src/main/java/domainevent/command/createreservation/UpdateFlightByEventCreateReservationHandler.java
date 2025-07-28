package domainevent.command.createreservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.MinAndMaxDateDTO;
import business.dto.ReservationWithSeatsDTO;
import business.mapper.FlightInstanceMapper;
import business.qualifier.createreservation.UpdateFlightByEventCreateReservationQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.commands.createreservation.CreateReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;


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
            MinAndMaxDateDTO minAndMaxDate = this.flightInstanceService.getMinAndMaxDateFlightInstances(listFlightInstances);
            c.setMinDateTime(minAndMaxDate.getMinDate());
            c.setMaxDateTime(minAndMaxDate.getMaxDate());
            c.setAllFlightBuy(isValid);
            this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_CREATE_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, eventData);
        } else 
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
        
    }
    
}
