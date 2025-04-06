package business.services;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import business.mapper.FlightInstanceMapper;

@Stateless
public class FlightInstanceServiceImpl implements FlightInstanceService {
    private EntityManager entityManager;

    @Override
    public FlightInstanceDTO getFlightInstanceIsActiveById(long idFlightInstance) {
        FlightInstance fI = this.entityManager.find(FlightInstance.class, idFlightInstance, LockModeType.OPTIMISTIC);
        if (fI == null || !fI.isActive())
            return null;
        if(!fI.getFlight().isActive())
            return null;
        return FlightInstanceMapper.INSTANCE.entityToDto(fI); 
    }
    
    @Inject
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
