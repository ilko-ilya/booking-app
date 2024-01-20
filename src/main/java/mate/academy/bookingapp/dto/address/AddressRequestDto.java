package mate.academy.bookingapp.dto.address;

import lombok.Data;

@Data
public class AddressRequestDto {
    private String country;
    private String city;
    private String street;
    private String addressLine;
    private Integer zipCode;
}
