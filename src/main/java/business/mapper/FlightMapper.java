package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.flight.Flight;
import business.flight.FlightDTO;

@Mapper
public interface FlightMapper {
    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);
    FlightDTO entityToDto(Flight flight);
    
    FlightDTO entityToDto(Flight flight, String countryDestination);
}
