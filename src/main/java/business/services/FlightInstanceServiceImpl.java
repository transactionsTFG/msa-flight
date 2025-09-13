package business.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import business.dto.FlightParamsDTO;
import business.dto.MinAndMaxDateDTO;
import business.dto.ReservationWithSeatsDTO;
import business.exceptions.FlightException;
import business.external.airport.AirportClient;
import business.external.airport.AirportDTO;
import business.external.country.CountryClient;
import business.flight.Flight;
import business.flight.FlightDTO;
import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;
import business.mapper.FlightInstanceMapper;
import business.mapper.FlightMapper;

@Stateless
public class FlightInstanceServiceImpl implements FlightInstanceService {
    private EntityManager entityManager;
    private AirportClient airportClient;
    private CountryClient countryClient;
    
    @Override
    public FlightDTO getFlightById(long idFlight) {
        return Optional.ofNullable(this.entityManager.find(Flight.class, idFlight, LockModeType.OPTIMISTIC))
                .map(FlightMapper.INSTANCE::entityToDto)
                .orElseThrow(() -> new FlightException("Flight not found"));
    }

    @Override
    public List<FlightDTO> getFlightsByParams(FlightParamsDTO params) {
        List<Long> originAirportIds = null;
        if (params.getCountryOrigin() != null && !params.getCountryOrigin().isEmpty()) {
            originAirportIds = airportClient
                .getAirportsByCountry(params.getCountryOrigin())
                .stream()
                .map(AirportDTO::getId)
                .toList();         
        }

        List<Long> destinationAirportIds = null;
        if (params.getCountryDestination() != null && !params.getCountryDestination().isEmpty()) {
            destinationAirportIds = airportClient
                .getAirportsByCountry(params.getCountryDestination())
                .stream()
                .map(AirportDTO::getId)
                .toList();
        }

        if (params.getCityOrigin() != null && !params.getCityOrigin().isEmpty()) {
            List<Long> cityAirportId = airportClient.getAirportsByCityName(params.getCityOrigin()).stream()
                    .map(AirportDTO::getId)
                    .toList();
            originAirportIds = (originAirportIds == null)
                    ? cityAirportId
                    : originAirportIds.stream()
                                    .filter(id -> cityAirportId.contains(id))
                                    .toList();
        }

        if (params.getCityDestination() != null && !params.getCityDestination().isEmpty()) {
            List<Long> cityAirportId = airportClient.getAirportsByCityName(params.getCityDestination()).stream()
                    .map(AirportDTO::getId)
                    .toList();
            destinationAirportIds = (destinationAirportIds == null)
                    ? cityAirportId
                    : destinationAirportIds.stream()
                                    .filter(id -> cityAirportId.contains(id))
                                    .toList();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flight> cq = cb.createQuery(Flight.class);
        Root<Flight> root = cq.from(Flight.class);

        List<Predicate> predicates = new ArrayList<>();
        if (originAirportIds != null) {
            if (originAirportIds.isEmpty())
                return List.of();
            predicates.add(root.get("idOriginAirport").in(originAirportIds));
        }

        if (destinationAirportIds != null) {
            if (destinationAirportIds.isEmpty())
                return List.of();
            predicates.add(root.get("idDestinationAirport").in(destinationAirportIds));
        }

        if (params.getDateOrigin() != null) {
            LocalDateTime date = params.getDateOrigin().atStartOfDay();
            predicates.add(cb.equal(root.get("dateOrigin"), date));
        }

        // WHERE p1 AND p2 AND …
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(cq)
                .getResultList()
                .stream()
                .map(f -> FlightMapper.INSTANCE.entityToDto(f, this.countryClient.getCountryById(this.airportClient.getAirportById(f.getIdDestinationAirport()).getCountryId()).getName()))
                .toList();
    }
        

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
    public boolean removeSeats(List<ReservationWithSeatsDTO> addSeats) {
        for (ReservationWithSeatsDTO r : addSeats) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, r.getIdFlightInstance(), LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive())
                return false;
            fI.setPassengerCounter(fI.getPassengerCounter() - r.getNewPassengerCounter());
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

    @Override
    public MinAndMaxDateDTO getMinAndMaxDateFlightInstances(List<Long> idFlightInstance) {
        LocalDateTime minDate = null;
        LocalDateTime maxDate = null;
        for (Long id : idFlightInstance) {
            FlightInstance fI = this.entityManager.find(FlightInstance.class, id, LockModeType.OPTIMISTIC);
            if (fI == null || !fI.isActive() || !fI.getFlight().isActive())
                continue;
            LocalDateTime departureDateTime = fI.getFlight().getArrivalTime().atDate(fI.getArrivalDate());
            LocalDateTime arrivalDateTime = fI.getFlight().getDepartureTime().atDate(fI.getDepartureDate());
            if (minDate == null || departureDateTime.isBefore(minDate)) 
                minDate = departureDateTime;
            
            if (maxDate == null || arrivalDateTime.isAfter(maxDate))
                maxDate = arrivalDateTime;
            
        }
        return new MinAndMaxDateDTO(minDate, maxDate);
    }

    @Override
    public FlightInstanceDTO getFlightInstanceById(long instanceId) {
        FlightInstance flightInstance = this.entityManager.find(FlightInstance.class, instanceId, LockModeType.OPTIMISTIC);
        if (flightInstance == null || !flightInstance.isActive())
            throw new FlightException("Flight instance not found or not active");
        if (!flightInstance.getFlight().isActive())
            throw new FlightException("Flight not active");
        return FlightInstanceMapper.INSTANCE.entityToDto(flightInstance);
    }

    @Inject
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Inject
    public void setAirportClient(AirportClient airportClient) {
        this.airportClient = airportClient;
    }

    @Inject
    public void setCountryClient(CountryClient countryClient) {
        this.countryClient = countryClient;
    }

}
