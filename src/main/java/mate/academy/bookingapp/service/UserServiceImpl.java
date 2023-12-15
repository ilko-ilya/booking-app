package mate.academy.bookingapp.service;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationResponseDto;
import mate.academy.bookingapp.exception.RegistrationException;
import mate.academy.bookingapp.mapper.UserMapper;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findAllByEmail(requestDto.getEmail()).isPresent()) {
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
}
