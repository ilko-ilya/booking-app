package mate.academy.bookingapp.service.user;

import mate.academy.bookingapp.dto.user.UserDto;
import mate.academy.bookingapp.dto.user.UserProfileUpdateDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationResponseDto;
import mate.academy.bookingapp.dto.user.UserRoleUpdateDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    UserDto getCurrentUserProfile(Authentication authentication);

    UserDto updateCurrentUserProfile(Authentication authentication, UserProfileUpdateDto updateDto);

    UserDto updateRoleById(Long userId, UserRoleUpdateDto roleUpdateDto);

    boolean existsById(Long userId);
}


