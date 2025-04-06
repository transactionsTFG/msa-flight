package business.flightinstance;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FlightInstanceDTO {
    private long id;
	private LocalDate departureDate;
	private LocalDate arrivalDate;
	private String statusFlight;
	private int passengerCounter;
	private double price;
	private boolean active;
	private long idFlight;
}
