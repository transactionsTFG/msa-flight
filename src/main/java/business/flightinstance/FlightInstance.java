package business.flightinstance;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import business.flight.Flight;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flight_instance")
public class FlightInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;
    @Column(nullable = false, name = "departure_date")
	private LocalDate departureDate;
	@Column(nullable = false, name = "arrival_date")
	private LocalDate arrivalDate;
	@Column(nullable = false, name = "status_flight")
	private String statusFlight;
    @Column(nullable = false, name = "passenger_counter")
	private int passengerCounter;
	@Column(nullable = false, name = "price")
	private double price;
    @Column(name = "is_active", nullable = false)
	private boolean active;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_flight", nullable = false)
	private Flight flight;
    @Version
    @Column(name = "version")
    private int version;
}
