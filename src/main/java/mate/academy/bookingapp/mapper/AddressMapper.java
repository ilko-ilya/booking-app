package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.dto.address.AddressUpdateDto;
import mate.academy.bookingapp.model.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    Address toModel(AddressRequestDto requestDto);

    Address toModelFromUpdate(AddressUpdateDto updateDto);

    Address toModelFromAccommodationUpdateDto(AccommodationUpdateDto updateDto);
}
