package business.external.country;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Stateless
public class CountryClientImpl implements CountryClient{
    private Client client = ClientBuilder.newClient();
    private static final String PATH = "http://localhost:9001/country/";

    @Override
    public CountryDTO getCountryName(String name) {
        return this.client.target(PATH + name)
                .request()
                .get(CountryDTO.class);
    }

    @Override
    public CountryDTO getCountryById(Long id) {
        return this.client.target(PATH + id)
                .request()
                .get(CountryDTO.class);
    }
    
}
