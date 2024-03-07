package mate.academy.bookingapp.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.bookingapp.dto.user.UserDto;
import mate.academy.bookingapp.dto.user.UserProfileUpdateDto;
import mate.academy.bookingapp.dto.user.UserRoleUpdateDto;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.service.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "John.doe@example.com", roles = "CUSTOMER")
    @DisplayName("Get current User profile")
    @Test
    public void getCurrentUserProfile_Success() throws Exception {
        User authenticatedUser = createUser(
                1L,
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );

        UserDto expectedUserDto = createUserDto(authenticatedUser);

        when(userService.getCurrentUserProfile(ArgumentMatchers.any(Authentication.class)))
                .thenReturn(expectedUserDto);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.firstName").value(expectedUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUserDto.getLastName()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()))
                .andExpect(jsonPath("$.role").value(expectedUserDto.getRole().toString()));
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Update UserProfile")
    @Test
    public void updateUserProfile_Success() throws Exception {
        User authenticatedUser = createUser(
                1L,
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );

        UserProfileUpdateDto updateDto = new UserProfileUpdateDto();
        updateDto.setFirstName("UpdatedFirstName");
        updateDto.setLastName("UpdatedLastName");

        UserDto updatedUserDto = createUserDto(authenticatedUser);
        updatedUserDto.setFirstName(updateDto.getFirstName());
        updatedUserDto.setLastName(updateDto.getLastName());

        when(userService.updateCurrentUserProfile(ArgumentMatchers.any(Authentication.class),
                ArgumentMatchers.any(UserProfileUpdateDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(updatedUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedUserDto.getLastName()))
                .andReturn();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Update UserRole by ID")
    @Test
    public void updateRoleById_Success() throws Exception {
        User authenticatedUser = createUser(
                1L,
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );

        UserRoleUpdateDto updateDto = new UserRoleUpdateDto();
        updateDto.setRole("MANAGER");

        UserDto updatedUserDto = new UserDto(
                authenticatedUser.getId(),
                authenticatedUser.getEmail(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                User.Role.MANAGER
        );
        when(userService.updateRoleById(authenticatedUser.getId(), updateDto))
                .thenReturn(updatedUserDto);

        mockMvc.perform(post("/api/users/{id}/role", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value(updatedUserDto.getRole().toString()))
                .andReturn();
    }

    private User createUser(
            Long id,
            String email,
            String password,
            String firstName,
            String lastName,
            User.Role role
    ) {
        User user = new User();
        user.setId(id);
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
}
