package mate.academy.bookingapp.dto.address;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String country;
    private String city;
    private String street;
    private String addressLine;
    private Integer zipCode;
}
