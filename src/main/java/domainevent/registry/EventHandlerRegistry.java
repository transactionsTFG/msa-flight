package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.flight.qualifier.ExistsFlightInstanceQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);
    private EventHandler existFlightInstanceByIdHandler;

    @PostConstruct
    public void init(){
        this.handlers.put(EventId.EXIST_FLIGHT_INSTANCE, existFlightInstanceByIdHandler);
    }

    public EventHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setFlightInstanceById(@ExistsFlightInstanceQualifier EventHandler existFlightInstanceByIdHandler) {
        this.existFlightInstanceByIdHandler = existFlightInstanceByIdHandler;
    }
}
