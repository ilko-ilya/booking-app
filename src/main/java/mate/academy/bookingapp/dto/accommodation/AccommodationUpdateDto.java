package mate.academy.bookingapp.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AccommodationUpdateDto {
    private List<String> amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
