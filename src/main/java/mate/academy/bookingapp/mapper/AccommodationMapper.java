package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toModel(AccommodationRequestDto requestDto);

}
