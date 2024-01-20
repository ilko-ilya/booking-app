package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserDto;
import mate.academy.bookingapp.dto.user.UserProfileUpdateDto;
import mate.academy.bookingapp.dto.user.UserRoleUpdateDto;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "get current user's profile",
            description = "get all information about current user")
    @GetMapping("/me")
    public UserDto getCurrentUserProfile(Authentication authentication) {
        return userService.getCurrentUserProfile(authentication);
    }

    @Operation(summary = "update user's profile",
            description = "update user's profile")
    @PatchMapping("/me")
    public UserDto updateUserProfile(Authentication authentication,
                                     @RequestBody @Valid UserProfileUpdateDto updateDto) {
        return userService.updateCurrentUserProfile(authentication, updateDto);
    }

    @Operation(summary = "update user's role by id",
            description = "update user's role by id")
    @PostMapping("/{id}/role")
    public UserDto updateRoleById(@PathVariable Long id,
                                  @RequestBody @Valid UserRoleUpdateDto updateDto) {
        return userService.updateRoleById(id, updateDto);
    }
}
