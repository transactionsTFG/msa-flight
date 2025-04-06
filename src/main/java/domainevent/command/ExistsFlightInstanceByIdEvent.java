package domainevent.command;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.flightinstance.FlightInstanceDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventId;
import msa.commons.microservices.flight.qualifier.ExistsFlightInstanceQualifier;
import msa.commons.parser.NumberParser;

@Stateless
@ExistsFlightInstanceQualifier
@Local(EventHandler.class)
public class ExistsFlightInstanceByIdEvent extends BaseHandler {

    @Override
    public void handleCommand(Object data) {
        long idFlightInstance = NumberParser.toLong(data);
        FlightInstanceDTO f = this.flightInstanceService.getFlightInstanceIsActiveById(idFlightInstance);
        if(f == null) 
            this.jmsEventPublisher.publish(EventId.FLIGHT_INSTANCE_NOT_FOUND, -1L);
        else
            this.jmsEventPublisher.publish(EventId.FLIGHT_INSTANCE_FOUND, f);
    }
    
}
