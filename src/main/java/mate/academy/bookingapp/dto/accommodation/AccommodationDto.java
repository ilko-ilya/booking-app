package mate.academy.bookingapp.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import mate.academy.bookingapp.model.Accommodation;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AccommodationDto {
    private Long id;
    private Accommodation.Type type;
    private Long locationId;
    private String size;
    private List<String> amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
