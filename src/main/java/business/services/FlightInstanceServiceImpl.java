package business.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.dto.ReservationWithSeatsDTO;
import business.exceptions.FlightException;
import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import business.mapper.FlightInstanceMapper;

@Stateless
public class FlightInstanceServiceImpl implements FlightInstanceService {
    private EntityManager entityManager;

    @Override
    public Map<Long, FlightInstanceDTO> getFlightInstanceIsActiveById(List<Long> listFlightInstanceIds) {
        Map<Long, FlightInstanceDTO> mapFlightInstances = new HashMap<>();
        for (Long idFlightInstance : listFlightInstanceIds) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, idFlightInstance, LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive())
                return new HashMap<>();
            if(!fI.getFlight().isActive())
                return new HashMap<>();
            mapFlightInstances.put(fI.getId(), FlightInstanceMapper.INSTANCE.entityToDto(fI));
        }
        return mapFlightInstances; 
    }

    @Override
    public boolean addSeats(List<ReservationWithSeatsDTO> addSeats) {
        for (ReservationWithSeatsDTO r : addSeats) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, r.getIdFlightInstance(), LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive())
                return false;
            fI.setPassengerCounter(fI.getPassengerCounter() + r.getNewPassengerCounter());
            this.entityManager.merge(fI);
        }
        return true;
    }

    @Override
    public void updateSagaIdFlightInstance(List<Long> listFlightInstanceIds, String sagaId) {
        for (Long idFlightInstance : listFlightInstanceIds) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, idFlightInstance, LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive())
                throw new FlightException("FlightInstance not found or not active");
            if(!fI.getFlight().isActive())
                throw new FlightException("Flight not active");
            fI.setSagaId(sagaId);
            this.entityManager.merge(fI);
        }
    }

    @Override
    public boolean valdateSagaId(List<Long> flightInstances, String sagaId) {
        for (Long idFlightInstance : flightInstances) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, idFlightInstance, LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive())
                return false;
            if(!fI.getFlight().isActive())
                return false;
            if (!fI.getSagaId().equals(sagaId))
                return false;
        }
        return true;
    }

    @Inject
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
 
}
