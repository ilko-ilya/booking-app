package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.user.UserDto;
import mate.academy.bookingapp.dto.user.UserProfileUpdateDto;
import mate.academy.bookingapp.dto.user.UserRegistrationRequestDto;
import mate.academy.bookingapp.dto.user.UserRegistrationResponseDto;
import mate.academy.bookingapp.dto.user.UserRoleUpdateDto;
import mate.academy.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toDto(User user);

    UserDto toDtoFromModel(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(encodePassword(updateDto.getPassword() ))")
    User toModelFromUpdateDto(UserProfileUpdateDto updateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    User toModelFromRoleDto(UserRoleUpdateDto roleUpdateDto);

    default String encodePassword(String plainPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainPassword);
    }
}


