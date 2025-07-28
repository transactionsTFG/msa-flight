package business.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinAndMaxDateDTO {
    private LocalDateTime minDate;
    private LocalDateTime maxDate;
}
