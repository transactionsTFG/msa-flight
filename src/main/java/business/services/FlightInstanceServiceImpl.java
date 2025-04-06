package business.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.dto.ReservationWithSeatsDTO;
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

    @Inject
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    
}
