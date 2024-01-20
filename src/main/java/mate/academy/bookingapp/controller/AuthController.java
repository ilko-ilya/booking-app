package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.user.UserLoginRequestDto;
import mate.academy.bookingapp.dto.user.UserLoginResponseDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationResponseDto;
import mate.academy.bookingapp.security.AuthenticationService;
import mate.academy.bookingapp.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Login", description = "To login users")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @Operation(summary = "Register", description = "To register users")
    @PostMapping("/register")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request
    ) {
        return userService.register(request);
    }
}
