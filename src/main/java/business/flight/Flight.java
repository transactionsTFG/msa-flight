package business.flight;

import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import business.flightinstance.FlightInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;
    @Column(name = "is_active", nullable = false)
    private boolean active;
    @Column(nullable = false, name = "code_flight")
	private String codeFlight;
	@Column(nullable = false, name = "week_day")
	private String weekDay;
	@Column(nullable = false, name = "departure_time")
	private LocalTime arrivalTime;
    @Column(nullable = false, name = "arrival_time")
    private LocalTime departureTime;
    @Column(nullable = false, name = "aircraft_id")
    private Long idAircraft;
    @Column(nullable = false, name = "origin_id")
    private Long idOriginAirport;
    @Column(nullable = false, name = "destination_id")
    private Long idDestinationAirport;
    @Version
    @Column(name = "version")
    private int version;
    @OneToMany(mappedBy = "flight")
	private Set<FlightInstance> flightInstance;
}
