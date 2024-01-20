package mate.academy.bookingapp.dto.user;

import lombok.Data;
import mate.academy.bookingapp.model.User;

@Data
public class UserRoleUpdateDto {
    private User.Role role;
}
