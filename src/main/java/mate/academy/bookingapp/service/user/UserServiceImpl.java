package mate.academy.bookingapp.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserDto;
import mate.academy.bookingapp.dto.user.UserProfileUpdateDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationResponseDto;
import mate.academy.bookingapp.dto.user.UserRoleUpdateDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.exception.RegistrationException;
import mate.academy.bookingapp.mapper.UserMapper;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = new User()
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(passwordEncoder.encode(requestDto.getPassword()))
                .setRole(User.Role.CUSTOMER);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getCurrentUserProfile(Authentication authentication) {
        return userMapper.toDtoFromModel(getUser(authentication));
    }

    @Override
    public UserDto updateCurrentUserProfile(Authentication authentication,
                                            UserProfileUpdateDto updateDto) {
        User userFromDb = getUser(authentication);

        if (updateDto.getFirstName() != null) {
            userFromDb.setFirstName(updateDto.getFirstName());
        }

        if (updateDto.getLastName() != null) {
            userFromDb.setLastName(updateDto.getLastName());
        }

        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(userFromDb.getEmail())) {
            if (userRepository.findByEmail(updateDto.getEmail()).isPresent()) {
                throw new RegistrationException("Email is already in use");
            }
            userFromDb.setEmail(updateDto.getEmail());
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            userFromDb.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        if (updateDto.getRole() != null) {
            userFromDb.setRole(User.Role.valueOf(updateDto.getRole()));
        }

        User updatedUser = userRepository.save(userFromDb);
        return userMapper.toDtoFromModel(updatedUser);
    }

    @Override
    public UserDto updateRoleById(Long userId, UserRoleUpdateDto roleUpdateDto) {
        User userFromDb = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by userId: " + userId));
        userFromDb.setRole(User.Role.valueOf(roleUpdateDto.getRole()));
        User savedUser = userRepository.save(userFromDb);
        return userMapper.toDtoFromModel(savedUser);
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: " + email));
    }
}
