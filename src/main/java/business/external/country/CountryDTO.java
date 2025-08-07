package business.external.country;

import lombok.Data;

@Data
public class CountryDTO {
    private long id;
    private String name;
    private boolean active;
    private String pathImg;
}
