package mate.academy.bookingapp.dto.user;

import lombok.Data;
import mate.academy.bookingapp.model.User;

@Data
public class UserProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private User.Role role;
}
