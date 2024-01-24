package mate.academy.bookingapp.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import mate.academy.bookingapp.dto.address.AddressUpdateDto;

@Data
public class AccommodationUpdateDto {
    private String size;
    private String type;
    private List<String> amenities;
    private BigDecimal dailyRate;
    private Integer availability;
    private AddressUpdateDto location;
}
