package business.external.airport;

import java.util.List;

public interface AirportClient {
    List<AirportDTO> getAirportsByCityName(String city);
    List<AirportDTO> getAirportsByCountry(String countryName);
}
