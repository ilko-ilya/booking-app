package mate.academy.bookingapp.dto.user;

import lombok.Data;
import mate.academy.bookingapp.model.User;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private User.Role role;

    public UserDto(Long id, String firstName, String lastName, String email, User.Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

}
