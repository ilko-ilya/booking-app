package mate.academy.bookingapp.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import mate.academy.bookingapp.dto.address.AddressRequestDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AccommodationRequestDto {
    @NonNull
    private String type;
    @NonNull
    private String size;
    private List<String> amenities;
    @NonNull
    private BigDecimal dailyRate;
    @NonNull
    private Integer availability;
    @NonNull
    private AddressRequestDto location;
}
