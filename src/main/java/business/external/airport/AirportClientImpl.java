package business.external.airport;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;

@Stateless
public class AirportClientImpl implements AirportClient {
    private Client client = ClientBuilder.newClient();
    private static final String PATH = "http://localhost:9001/msa-airport/api/airport/";

    @Override
    public List<AirportDTO> getAirportsByCityName(String city) {
        return this.client.target(PATH + "/city/" + city)
                            .request()
                            .get(new GenericType<List<AirportDTO>>() {});
    }

    @Override
    public List<AirportDTO> getAirportsByCountry(String countryName) {
        return this.client.target(PATH + "/country/" + countryName)
                            .request()
                            .get(new GenericType<List<AirportDTO>>() {});
    }

    @Override
    public AirportDTO getAirportById(long id) {
        return this.client.target(PATH + "/" + id)
                            .request()
                            .get(AirportDTO.class);
    }

    
    
}
