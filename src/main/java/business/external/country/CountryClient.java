package business.external.country;

public interface CountryClient {
    CountryDTO getCountryName(String name);
    CountryDTO getCountryById(Long id);
}
