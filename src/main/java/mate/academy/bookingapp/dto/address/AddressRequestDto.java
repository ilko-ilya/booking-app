package mate.academy.bookingapp.dto.address;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class AddressRequestDto {
    @NonNull
    private String country;
    @NonNull
    private String city;
    @NonNull
    private String street;
    @NonNull
    private String addressLine;
    @NonNull
    private Integer zipCode;
}
