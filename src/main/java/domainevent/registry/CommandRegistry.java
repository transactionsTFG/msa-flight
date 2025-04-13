package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationQualifier;
import msa.commons.microservices.flight.qualifier.ValidateFlightByEventCreateReservationQualifier;

@Singleton
@Startup
public class CommandRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);
    private EventHandler existFlightInstanceByIdHandler;
    private EventHandler updateFlightByEventCreateReservationHandler;

    @PostConstruct
    public void init(){
        this.handlers.put(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_CREATE_RESERVATION, existFlightInstanceByIdHandler);
        this.handlers.put(EventId.FLIGHT_UPDATE_FLIGHT_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, updateFlightByEventCreateReservationHandler);
    }

    public EventHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setFlightInstanceById(@ValidateFlightByEventCreateReservationQualifier EventHandler existFlightInstanceByIdHandler) {
        this.existFlightInstanceByIdHandler = existFlightInstanceByIdHandler;
    }

    @Inject
    public void setUpdateFlightByEventCreateReservationHandler(@UpdateFlightByEventCreateReservationQualifier EventHandler updateFlightByEventCreateReservationHandler) {
        this.updateFlightByEventCreateReservationHandler = updateFlightByEventCreateReservationHandler;
    }
}
