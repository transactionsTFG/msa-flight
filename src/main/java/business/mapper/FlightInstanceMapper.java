package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.flightinstance.FlightInstance;
import business.flightinstance.FlightInstanceDTO;

@Mapper
public interface FlightInstanceMapper {
    FlightInstanceMapper INSTANCE = Mappers.getMapper(FlightInstanceMapper.class);
    @Mapping(target = "idFlight", source = "flight.id")
    @Mapping(target = "idAircraft", source = "flight.idAircraft")
    FlightInstanceDTO entityToDto(FlightInstance flight);
}
