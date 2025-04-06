package business.dto;

import lombok.Data;

@Data
public class ReservationWithSeatsDTO {   
    private long idFlightInstance;
    private int newPassengerCounter;
}
