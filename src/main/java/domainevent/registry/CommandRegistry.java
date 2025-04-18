package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.flight.modifyreservation.qualifier.UpdateFlightByModifyReservationCommit;
import msa.commons.microservices.flight.modifyreservation.qualifier.UpdateFlightByModifyReservationRollback;
import msa.commons.microservices.flight.modifyreservation.qualifier.ValidateFlightByModifyReservation;
import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationQualifier;
import msa.commons.microservices.flight.qualifier.UpdateFlightByEventCreateReservationRollbackQualifier;
import msa.commons.microservices.flight.qualifier.ValidateFlightByEventCreateReservationQualifier;

@Singleton
@Startup
public class CommandRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);
    /* EVENTS OF CREATE RESERVATION */
    private EventHandler existFlightInstanceByIdHandler;
    private EventHandler updateFlightByEventCreateReservationHandler;
    private EventHandler updateFlightByEventCreateReservationRollbackHandler;
    /* EVENTS OF MODIFY RESERVATION */
    private EventHandler existFlightInstanceByEventModifyReservationHandler;
    private EventHandler updateFlightByEventModifyReservationCommitHandler;
    private EventHandler updateFlightByEventModifyReservationRollbackHandler;

    @PostConstruct
    public void init(){
        this.handlers.put(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_CREATE_RESERVATION, existFlightInstanceByIdHandler);
        this.handlers.put(EventId.FLIGHT_UPDATE_FLIGHT_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, updateFlightByEventCreateReservationHandler);
        this.handlers.put(EventId.FLIGHT_UPDATE_FLIGHT_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, updateFlightByEventCreateReservationRollbackHandler);
        this.handlers.put(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_MODIFY_RESERVATION, existFlightInstanceByEventModifyReservationHandler);
        this.handlers.put(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_COMMIT_SAGA, updateFlightByEventModifyReservationCommitHandler);
        this.handlers.put(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, updateFlightByEventModifyReservationRollbackHandler);    
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

    @Inject
    public void setUpdateFlightByEventCreateReservationRollbackHandler(@UpdateFlightByEventCreateReservationRollbackQualifier EventHandler updateFlightByEventCreateReservationRollbackHandler) {
        this.updateFlightByEventCreateReservationRollbackHandler = updateFlightByEventCreateReservationRollbackHandler;
    }

    @Inject
    public void setExistFlightInstanceByEventModifyReservationHandler(@ValidateFlightByModifyReservation EventHandler existFlightInstanceByEventModifyReservationHandler) {
        this.existFlightInstanceByEventModifyReservationHandler = existFlightInstanceByEventModifyReservationHandler;
    }

    @Inject
    public void setUpdateFlightByEventModifyReservationCommitHandler(@UpdateFlightByModifyReservationCommit EventHandler updateFlightByEventModifyReservationCommitHandler) {
        this.updateFlightByEventModifyReservationCommitHandler = updateFlightByEventModifyReservationCommitHandler;
    }

    @Inject
    public void setUpdateFlightByEventModifyReservationRollbackHandler(@UpdateFlightByModifyReservationRollback EventHandler updateFlightByEventModifyReservationRollbackHandler) {
        this.updateFlightByEventModifyReservationRollbackHandler = updateFlightByEventModifyReservationRollbackHandler;
    }

}
