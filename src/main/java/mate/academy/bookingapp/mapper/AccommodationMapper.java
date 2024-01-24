package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {
    @Mapping(source = "location.id", target = "locationId")
    AccommodationDto toDto(Accommodation accommodation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Accommodation toModel(AccommodationRequestDto requestDto);
}
