package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import mate.academy.bookingapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("User registration with valid data. Should return UserRegistrationResponseDto")
    @Test
    public void userRegistration_WithValidData_ShouldReturnUserRegistrationResponseDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "john.doe@example.com",
                "password123",
                "password123",
                "John",
                "Doe"
        );
        User expectedUser = createUser(

                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                User.Role.CUSTOMER
        );

        UserRegistrationResponseDto expectedResponseDto =
                createUserRegistrationResponseDto(expectedUser);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(userMapper.toDto(expectedUser)).thenReturn(expectedResponseDto);

        UserRegistrationResponseDto actualResult = userService.register(requestDto);

        assertNotNull(actualResult);
        assertEquals(expectedResponseDto, actualResult);

        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(expectedUser);
    }

    @DisplayName("User registration with Existing email. Should throw RegistrationException")
    @Test
    public void userRegistration_WithExistingEmail_ShouldThrowRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "John.doe@example.com",
                "password123",
                "password123",
                "John",
                "Doe"
        );

        User existingUser = createUser(

                requestDto.getEmail(),
                "Hashed password",
                requestDto.getFirstName(),
                requestDto.getLastName(),
                User.Role.CUSTOMER
        );

        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(existingUser))
                .thenThrow(new RegistrationException("Unable to complete registration"));

        assertThrows(RegistrationException.class, () -> userService.register(requestDto));

        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("Get current User Profile")
    @Test
    public void getCurrentUserProfile_ShouldReturnUserDto() {
        User authenticatedUser = createUser(
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authenticatedUser, null);
        UserDto expectedUserDto = createUserDto(authenticatedUser);

        when(userRepository.findByEmail(authenticatedUser.getEmail()))
                .thenReturn(Optional.of(authenticatedUser));
        when(userMapper.toDtoFromModel(authenticatedUser)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.getCurrentUserProfile(authentication);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);

        verify(userMapper, times(1)).toDtoFromModel(authenticatedUser);
    }

    @DisplayName("Get current User Profile. User - Not Found")
    @Test
    public void getCurrentUserProfile_UserNotFound_ShouldThrowEntityNotFoundException() {
        User authenticatedUser = createUser(
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authenticatedUser, null);

        when(userRepository.findByEmail(authenticatedUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getCurrentUserProfile(authentication));

        verify(userRepository, times(1))
                .findByEmail(authenticatedUser.getEmail());
    }

    @DisplayName("Update current User Profile. Should return updated UserDto")
    @Test
    public void updateCurrentUserProfile_WithValidDate_ShouldReturnUpdatedUserDto() {
        User authenticatedUser = createUser(
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );
        final Authentication authentication =
                new UsernamePasswordAuthenticationToken(authenticatedUser, null);

        UserProfileUpdateDto updateDto = new UserProfileUpdateDto();
        updateDto.setEmail("newJohn.doe@example.com");
        updateDto.setPassword("newPassword123");
        updateDto.setFirstName("Bob");
        updateDto.setLastName("Bobikov");

        when(userRepository.findByEmail(authenticatedUser.getEmail()))
                .thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(updateDto.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDtoFromModel(any(User.class))).thenAnswer(invocation -> {
            User updatedUser = invocation.getArgument(0);
            return createUserDto(updatedUser);
        });

        UserDto updatedUserDto = userService.updateCurrentUserProfile(authentication, updateDto);

        assertNotNull(updatedUserDto);
        assertEquals("Bob", updatedUserDto.getFirstName());
        assertEquals("Bobikov", updatedUserDto.getLastName());
        assertEquals("newJohn.doe@example.com", updatedUserDto.getEmail());

        verify(userRepository, times(1)).findByEmail(authenticatedUser.getEmail());
        verify(userRepository, times(1)).findByEmail(updateDto.getEmail());
        verify(passwordEncoder, times(1)).encode(updateDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDtoFromModel(any(User.class));
    }

    @DisplayName("Update Role by id")
    @Test
    public void updateRoleById_ShouldReturnUpdatedUserDto() {
        Long userId = 1L;

        UserRoleUpdateDto updateDto = new UserRoleUpdateDto();
        updateDto.setRole("MANAGER");

        User user = createUser(
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User updatedUser = createUser(
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.MANAGER
        );
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto expectedUserDto = createUserDto(updatedUser);
        when(userMapper.toDtoFromModel(updatedUser)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.updateRoleById(userId, updateDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDtoFromModel(updatedUser);
    }

    @DisplayName("Update Role by ID: User Not Found")
    @Test
    public void updateRoleById_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long userId = 1L;

        UserRoleUpdateDto updateDto = new UserRoleUpdateDto();
        updateDto.setRole("MANAGER");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleById(userId, updateDto),
                "Can't find user by userId: " + userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDtoFromModel(any());
    }

    @DisplayName("Check if User exists by id")
    @Test
    public void existById_ShouldReturnTrueIfUserExists() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        boolean actual = userService.existsById(userId);
        assertTrue(actual);

        verify(userRepository,times(1)).existsById(userId);
    }

    private User createUser(
            String email,
            String password,
            String firstName,
            String lastName,
            User.Role role
    ) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);

        return user;
    }

    private UserDto createUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole());
    }

    private UserRegistrationResponseDto createUserRegistrationResponseDto(User user) {
        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto();
        responseDto.setId(user.getId());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());

        return responseDto;
    }
}
