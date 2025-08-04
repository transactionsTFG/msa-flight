package business.external.airport;

import lombok.Data;

@Data
public class AirportDTO {
    private long id;
    private boolean active;
    private String city;
    private String codename;
    private String name;
    private Long countryId;
}
