package mate.academy.bookingapp.dto.user;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
